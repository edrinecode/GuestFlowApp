package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.BirthdayUtils
import com.example.data.model.SpaGymClient
import com.example.data.repository.CheckInResult
import com.example.data.repository.GuestFlowRepository
import com.example.data.repository.LookupResult
import com.example.data.repository.RegistrationResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed class DeviceGateState {
    data object Checking : DeviceGateState()
    data class PendingApproval(val deviceId: String, val branchId: String, val branchName: String) : DeviceGateState()
    data class Revoked(val message: String = "Device access has been revoked.") : DeviceGateState()
    data class ConnectionError(val message: String) : DeviceGateState()
    data class Approved(val branchName: String, val branchId: String) : DeviceGateState()
}

sealed class KioskScreen {
    data object Welcome : KioskScreen()
    data object LookingUp : KioskScreen()
    data class RegisterWizard(val step: Int = 1) : KioskScreen() // 1: Name, 2: Birthday, 3: Phone
    data object CheckingIn : KioskScreen()
    data class Success(
        val firstName: String,
        val alreadyCheckedIn: Boolean,
        val isBirthday: Boolean,
        val countdownSeconds: Int = 5
    ) : KioskScreen()
    data class FrontDeskAssist(val firstName: String, val message: String) : KioskScreen()
    data class Error(val message: String, val canRetry: Boolean = true) : KioskScreen()
}

data class KioskUiState(
    val deviceGate: DeviceGateState = DeviceGateState.Checking,
    val currentScreen: KioskScreen = KioskScreen.Welcome,
    val phoneInput: String = "",
    val phoneError: String? = null,
    val phoneNotFound: Boolean = false,
    // Registration wizard form state
    val regName: String = "",
    val regMonth: String = "January",
    val regDay: String = "1",
    val regPhone: String = "",
    val regError: String? = null,
    // Staff Settings Modal state
    val showStaffSettings: Boolean = false,
    val staffServerUrl: String = "",
    val staffBranchId: String = "",
    val staffBranchName: String = "",
    val staffStatusMessage: String? = null,
    val isTestingConnection: Boolean = false
)

class KioskViewModel(
    private val repository: GuestFlowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(KioskUiState())
    val uiState: StateFlow<KioskUiState> = _uiState.asStateFlow()

    private var statusPollingJob: Job? = null
    private var heartbeatJob: Job? = null
    private var successTimerJob: Job? = null

    init {
        initKiosk()
    }

    fun initKiosk() {
        val branchId = repository.getBranchId()
        val branchName = repository.getBranchName()
        val serverUrl = repository.getServerBaseUrl()

        _uiState.update {
            it.copy(
                staffServerUrl = serverUrl,
                staffBranchId = branchId,
                staffBranchName = branchName
            )
        }
        checkDeviceStatus()
    }

    fun checkDeviceStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(deviceGate = DeviceGateState.Checking) }
            val branchId = repository.getBranchId()
            val result = repository.checkDeviceStatus(branchId)

            result.onSuccess { response ->
                when (response.status.lowercase()) {
                    "approved" -> {
                        val branchName = response.branchName ?: repository.getBranchName()
                        _uiState.update {
                            it.copy(
                                deviceGate = DeviceGateState.Approved(
                                    branchName = branchName,
                                    branchId = branchId
                                ),
                                staffBranchName = branchName
                            )
                        }
                        stopPolling()
                        startHeartbeat()
                    }
                    "revoked" -> {
                        _uiState.update {
                            it.copy(deviceGate = DeviceGateState.Revoked(response.message ?: "Kiosk access has been revoked."))
                        }
                        stopPolling()
                        stopHeartbeat()
                    }
                    else -> {
                        // Pending
                        val branchName = response.branchName ?: repository.getBranchName()
                        _uiState.update {
                            it.copy(
                                deviceGate = DeviceGateState.PendingApproval(
                                    deviceId = repository.getDeviceId(),
                                    branchId = branchId,
                                    branchName = branchName
                                )
                            )
                        }
                        startPollingStatus()
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        deviceGate = DeviceGateState.ConnectionError(
                            err.localizedMessage ?: "Could not connect to GuestFlow server."
                        )
                    )
                }
                // Try polling again after 10s
                startPollingStatus(intervalMs = 10000L)
            }
        }
    }

    private fun startPollingStatus(intervalMs: Long = 6000L) {
        if (statusPollingJob?.isActive == true) return
        statusPollingJob = viewModelScope.launch {
            while (isActive) {
                delay(intervalMs)
                val branchId = repository.getBranchId()
                val result = repository.checkDeviceStatus(branchId)
                if (result.isSuccess) {
                    val resp = result.getOrNull()
                    if (resp?.status.equals("approved", ignoreCase = true)) {
                        val branchName = resp?.branchName ?: repository.getBranchName()
                        _uiState.update {
                            it.copy(
                                deviceGate = DeviceGateState.Approved(branchName, branchId),
                                staffBranchName = branchName
                            )
                        }
                        startHeartbeat()
                        break
                    } else if (resp?.status.equals("revoked", ignoreCase = true)) {
                        _uiState.update {
                            it.copy(deviceGate = DeviceGateState.Revoked(resp?.message ?: "Device revoked."))
                        }
                        break
                    }
                }
            }
        }
    }

    private fun stopPolling() {
        statusPollingJob?.cancel()
        statusPollingJob = null
    }

    private fun startHeartbeat() {
        if (heartbeatJob?.isActive == true) return
        heartbeatJob = viewModelScope.launch {
            while (isActive) {
                delay(60000L) // heartbeat every 60 seconds
                val ok = repository.sendHeartbeat()
                if (!ok) {
                    // Re-check status in case revoked
                    val branchId = repository.getBranchId()
                    val statusRes = repository.checkDeviceStatus(branchId)
                    statusRes.onSuccess { st ->
                        if (st.status.equals("revoked", ignoreCase = true)) {
                            _uiState.update {
                                it.copy(deviceGate = DeviceGateState.Revoked(st.message ?: "Revoked"))
                            }
                            stopHeartbeat()
                        }
                    }
                }
            }
        }
    }

    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    // --- Phone Input / Keypad Actions ---
    fun onKeypadDigit(digit: String) {
        val current = _uiState.value.phoneInput
        if (current.length < 18) {
            _uiState.update { it.copy(phoneInput = current + digit, phoneError = null) }
        }
    }

    fun onKeypadBackspace() {
        val current = _uiState.value.phoneInput
        if (current.isNotEmpty()) {
            _uiState.update { it.copy(phoneInput = current.dropLast(1), phoneError = null) }
        }
    }

    fun onKeypadClear() {
        _uiState.update { it.copy(phoneInput = "", phoneError = null) }
    }

    fun onPhoneInputChange(newPhone: String) {
        // Strip out non-phone characters except +, 0-9, and space/hyphen
        val filtered = newPhone.filter { it.isDigit() || it == '+' || it == ' ' || it == '-' }
        _uiState.update { it.copy(phoneInput = filtered, phoneError = null, phoneNotFound = false) }
    }

    fun startLookup() {
        val rawPhone = _uiState.value.phoneInput.trim()
        val digitsOnly = rawPhone.filter { it.isDigit() }
        if (digitsOnly.length < 6) {
            _uiState.update {
                it.copy(phoneError = "Please enter a valid phone number (at least 6 digits)")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(currentScreen = KioskScreen.LookingUp, phoneError = null, phoneNotFound = false) }

            when (val lookupResult = repository.lookupClient(rawPhone)) {
                is LookupResult.Found -> {
                    // Found client, perform check-in
                    executeCheckIn(lookupResult.client, rawPhone)
                }
                is LookupResult.NotFound -> {
                    // Stay on Welcome screen and display the green register button
                    _uiState.update {
                        it.copy(
                            currentScreen = KioskScreen.Welcome,
                            phoneNotFound = true
                        )
                    }
                }
                is LookupResult.Error -> {
                    if (lookupResult.isRevoked) {
                        _uiState.update {
                            it.copy(deviceGate = DeviceGateState.Revoked(lookupResult.message))
                        }
                    } else {
                        _uiState.update {
                            it.copy(currentScreen = KioskScreen.Error(lookupResult.message))
                        }
                    }
                }
            }
        }
    }

    fun startNewGuestRegistration() {
        val currentPhone = _uiState.value.phoneInput.trim()
        _uiState.update {
            it.copy(
                currentScreen = KioskScreen.RegisterWizard(step = 1),
                regName = "",
                regMonth = "January",
                regDay = "1",
                regPhone = currentPhone,
                regError = null
            )
        }
    }

    private suspend fun executeCheckIn(client: SpaGymClient, phone: String) {
        _uiState.update { it.copy(currentScreen = KioskScreen.CheckingIn) }
        val branchId = repository.getBranchId()

        when (val checkInRes = repository.checkInClient(phone, client.id, branchId)) {
            is CheckInResult.Success -> {
                showSuccessScreen(
                    firstName = checkInRes.client.firstName,
                    alreadyCheckedIn = checkInRes.alreadyCheckedIn,
                    isBirthday = checkInRes.isBirthday
                )
            }
            is CheckInResult.Error -> {
                if (checkInRes.isRevoked) {
                    _uiState.update { it.copy(deviceGate = DeviceGateState.Revoked(checkInRes.message)) }
                } else {
                    _uiState.update { it.copy(currentScreen = KioskScreen.Error(checkInRes.message)) }
                }
            }
        }
    }

    // --- Registration Wizard Actions ---
    fun updateRegName(name: String) {
        _uiState.update { it.copy(regName = name, regError = null) }
    }

    fun updateRegMonth(month: String) {
        _uiState.update { it.copy(regMonth = month, regError = null) }
    }

    fun updateRegDay(day: String) {
        _uiState.update { it.copy(regDay = day, regError = null) }
    }

    fun updateRegPhone(phone: String) {
        _uiState.update { it.copy(regPhone = phone, regError = null) }
    }

    fun nextRegStep() {
        val current = _uiState.value
        val wizard = current.currentScreen as? KioskScreen.RegisterWizard ?: return

        when (wizard.step) {
            1 -> {
                if (current.regName.trim().length < 2) {
                    _uiState.update { it.copy(regError = "Please enter your full name") }
                    return
                }
                _uiState.update {
                    it.copy(currentScreen = KioskScreen.RegisterWizard(step = 2), regError = null)
                }
            }
            2 -> {
                val dayInt = current.regDay.toIntOrNull()
                if (dayInt == null || dayInt !in 1..31) {
                    _uiState.update { it.copy(regError = "Please select a valid day") }
                    return
                }
                _uiState.update {
                    it.copy(currentScreen = KioskScreen.RegisterWizard(step = 3), regError = null)
                }
            }
            3 -> {
                submitRegistration()
            }
        }
    }

    fun prevRegStep() {
        val wizard = _uiState.value.currentScreen as? KioskScreen.RegisterWizard ?: return
        if (wizard.step > 1) {
            _uiState.update {
                it.copy(currentScreen = KioskScreen.RegisterWizard(step = wizard.step - 1), regError = null)
            }
        } else {
            // Cancel registration, return to welcome screen
            privacyReset()
        }
    }

    fun submitRegistration() {
        val state = _uiState.value
        val phoneDigits = state.regPhone.filter { it.isDigit() }
        if (phoneDigits.length < 6) {
            _uiState.update { it.copy(regError = "Please enter a valid phone number") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(currentScreen = KioskScreen.CheckingIn, regError = null) }
            val branchId = repository.getBranchId()

            when (val regResult = repository.createClient(
                name = state.regName,
                day = state.regDay,
                month = state.regMonth,
                phone = state.regPhone,
                branchId = branchId
            )) {
                is RegistrationResult.Success -> {
                    val client = regResult.client
                    // Client profile created, now attempt check-in
                    when (val checkInRes = repository.checkInClient(state.regPhone, client.id, branchId)) {
                        is CheckInResult.Success -> {
                            showSuccessScreen(
                                firstName = checkInRes.client.firstName,
                                alreadyCheckedIn = checkInRes.alreadyCheckedIn,
                                isBirthday = checkInRes.isBirthday
                            )
                        }
                        is CheckInResult.Error -> {
                            // Profile was created but check-in failed: show front-desk assist state
                            _uiState.update {
                                it.copy(
                                    currentScreen = KioskScreen.FrontDeskAssist(
                                        firstName = client.firstName,
                                        message = "Your profile is saved! Please confirm your check-in with the reception desk."
                                    )
                                )
                            }
                            startAutoResetTimer(durationSeconds = 8)
                        }
                    }
                }
                is RegistrationResult.Error -> {
                    if (regResult.isRevoked) {
                        _uiState.update { it.copy(deviceGate = DeviceGateState.Revoked(regResult.message)) }
                    } else {
                        _uiState.update { it.copy(currentScreen = KioskScreen.Error(regResult.message)) }
                    }
                }
            }
        }
    }

    // --- Success & Privacy Reset ---
    private fun showSuccessScreen(firstName: String, alreadyCheckedIn: Boolean, isBirthday: Boolean) {
        _uiState.update {
            it.copy(
                currentScreen = KioskScreen.Success(
                    firstName = firstName,
                    alreadyCheckedIn = alreadyCheckedIn,
                    isBirthday = isBirthday,
                    countdownSeconds = 5
                )
            )
        }
        startAutoResetTimer(durationSeconds = 5)
    }

    private fun startAutoResetTimer(durationSeconds: Int = 5) {
        successTimerJob?.cancel()
        successTimerJob = viewModelScope.launch {
            for (sec in durationSeconds downTo 1) {
                delay(1000L)
                val screen = _uiState.value.currentScreen
                if (screen is KioskScreen.Success) {
                    _uiState.update { it.copy(currentScreen = screen.copy(countdownSeconds = sec - 1)) }
                }
            }
            privacyReset()
        }
    }

    /**
     * Complete privacy wipe: clears all personal information and resets to blank Welcome screen.
     */
    fun privacyReset() {
        successTimerJob?.cancel()
        successTimerJob = null
        _uiState.update {
            it.copy(
                currentScreen = KioskScreen.Welcome,
                phoneInput = "",
                phoneError = null,
                phoneNotFound = false,
                regName = "",
                regMonth = "January",
                regDay = "1",
                regPhone = "",
                regError = null
            )
        }
    }

    // --- Staff Settings Dialog ---
    fun openStaffSettings() {
        _uiState.update {
            it.copy(
                showStaffSettings = true,
                staffServerUrl = repository.getServerBaseUrl(),
                staffBranchId = repository.getBranchId(),
                staffBranchName = repository.getBranchName(),
                staffStatusMessage = null
            )
        }
    }

    fun closeStaffSettings() {
        _uiState.update { it.copy(showStaffSettings = false, staffStatusMessage = null) }
    }

    fun updateStaffServerUrl(url: String) {
        _uiState.update { it.copy(staffServerUrl = url, staffStatusMessage = null) }
    }

    fun updateStaffBranchId(branchId: String) {
        _uiState.update { it.copy(staffBranchId = branchId, staffStatusMessage = null) }
    }

    fun saveStaffSettings() {
        val state = _uiState.value
        repository.setServerBaseUrl(state.staffServerUrl)
        repository.setBranchId(state.staffBranchId)
        _uiState.update {
            it.copy(
                showStaffSettings = false,
                staffStatusMessage = "Settings saved successfully."
            )
        }
        checkDeviceStatus()
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTestingConnection = true, staffStatusMessage = null) }
            val state = _uiState.value
            repository.setServerBaseUrl(state.staffServerUrl)
            repository.setBranchId(state.staffBranchId)

            val result = repository.checkDeviceStatus(state.staffBranchId)
            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isTestingConnection = false,
                        staffStatusMessage = "Connected: status is '${response.status}' (Branch: ${response.branchName ?: "OK"})"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isTestingConnection = false,
                        staffStatusMessage = "Connection failed: ${err.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun getDeviceId(): String = repository.getDeviceId()

    override fun onCleared() {
        super.onCleared()
        stopPolling()
        stopHeartbeat()
        successTimerJob?.cancel()
    }

    class Factory(private val repository: GuestFlowRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(KioskViewModel::class.java)) {
                return KioskViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
