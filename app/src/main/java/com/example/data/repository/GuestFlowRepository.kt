package com.example.data.repository

import com.example.data.api.ApiClient
import com.example.data.model.BirthdayUtils
import com.example.data.model.BranchItem
import com.example.data.model.CheckInRequest
import com.example.data.model.CheckInResponse
import com.example.data.model.CreateClientRequest
import com.example.data.model.CreateClientResponse
import com.example.data.model.DeviceStatusResponse
import com.example.data.model.SpaGymClient
import com.example.data.model.SpaVisit
import com.example.data.security.KioskPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

sealed class LookupResult {
    data class Found(val client: SpaGymClient) : LookupResult()
    data object NotFound : LookupResult()
    data class Error(val message: String, val isRevoked: Boolean = false) : LookupResult()
}

sealed class RegistrationResult {
    data class Success(val client: SpaGymClient, val created: Boolean) : RegistrationResult()
    data class Error(val message: String, val isRevoked: Boolean = false) : RegistrationResult()
}

sealed class CheckInResult {
    data class Success(
        val client: SpaGymClient,
        val visit: SpaVisit?,
        val alreadyCheckedIn: Boolean,
        val isBirthday: Boolean
    ) : CheckInResult()
    data class Error(val message: String, val isRevoked: Boolean = false) : CheckInResult()
}

class GuestFlowRepository(
    private val apiClient: ApiClient,
    val preferences: KioskPreferences
) {
    fun getDeviceId(): String = preferences.getOrCreateInstallationId()

    fun getBranchId(): String = preferences.getBranchId()

    fun getBranchName(): String = preferences.getBranchName()

    fun setBranchId(branchId: String) = preferences.setBranchId(branchId)

    fun setBranchName(branchName: String) = preferences.setBranchName(branchName)

    fun getServerBaseUrl(): String = preferences.getServerBaseUrl()

    fun setServerBaseUrl(url: String) = preferences.setServerBaseUrl(url)

    fun clearSession() {
        apiClient.cookieJar.clearAllCookies()
    }

    suspend fun checkDeviceStatus(branchId: String): Result<DeviceStatusResponse> =
        withContext(Dispatchers.IO) {
            try {
                val deviceId = getDeviceId()
                val response = apiClient.getApi().getDeviceStatus(deviceId, branchId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        body.branchName?.let { preferences.setBranchName(it) }
                        if (body.status.equals("revoked", ignoreCase = true)) {
                            clearSession()
                        }
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Empty status response from server"))
                    }
                } else if (response.code() == 403) {
                    clearSession()
                    Result.success(DeviceStatusResponse(status = "revoked", message = "Device access revoked."))
                } else {
                    Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun sendHeartbeat(): Boolean = withContext(Dispatchers.IO) {
        try {
            val deviceId = getDeviceId()
            val response = apiClient.getApi().sendHeartbeat(deviceId)
            if (response.code() == 403) {
                clearSession()
                false
            } else {
                response.isSuccessful
            }
        } catch (_: Exception) {
            false
        }
    }

    suspend fun fetchBranches(): List<BranchItem> = withContext(Dispatchers.IO) {
        try {
            val deviceId = getDeviceId()
            val response = apiClient.getApi().getBranches(deviceId)
            if (response.isSuccessful) {
                val rawBody = response.body()?.string() ?: ""
                parseBranchesJson(rawBody)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseBranchesJson(raw: String): List<BranchItem> {
        val list = mutableListOf<BranchItem>()
        if (raw.isBlank()) return list
        try {
            if (raw.trim().startsWith("[")) {
                val jsonArray = JSONArray(raw)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = obj.optString("id", obj.optString("branchId", ""))
                    val name = obj.optString("name", obj.optString("branchName", "Branch"))
                    if (id.isNotEmpty()) list.add(BranchItem(id, name))
                }
            } else {
                val jsonObj = JSONObject(raw)
                val branchesArr = jsonObj.optJSONArray("branches")
                if (branchesArr != null) {
                    for (i in 0 until branchesArr.length()) {
                        val obj = branchesArr.getJSONObject(i)
                        val id = obj.optString("id", obj.optString("branchId", ""))
                        val name = obj.optString("name", obj.optString("branchName", "Branch"))
                        if (id.isNotEmpty()) list.add(BranchItem(id, name))
                    }
                }
            }
        } catch (_: Exception) {
            // Non-fatal branch list parsing failure
        }
        return list
    }

    suspend fun lookupClient(phone: String): LookupResult = withContext(Dispatchers.IO) {
        try {
            val deviceId = getDeviceId()
            val encodedPhone = URLEncoder.encode(phone.trim(), "UTF-8")
            val response = apiClient.getApi().lookupClient(deviceId, encodedPhone)

            when {
                response.isSuccessful -> {
                    val client = response.body()?.client
                    if (client != null) {
                        LookupResult.Found(client)
                    } else {
                        LookupResult.NotFound
                    }
                }
                response.code() == 404 -> {
                    LookupResult.NotFound
                }
                response.code() == 401 || response.code() == 403 -> {
                    clearSession()
                    LookupResult.Error("Kiosk authorization expired or revoked. Please notify reception.", isRevoked = true)
                }
                response.code() == 429 -> {
                    LookupResult.Error("Too many lookup attempts. Please wait a moment.")
                }
                else -> {
                    LookupResult.Error("Unable to reach spa guest service (HTTP ${response.code()}). Please see front desk.")
                }
            }
        } catch (e: Exception) {
            LookupResult.Error("Network error: ${e.localizedMessage ?: "Could not connect to kiosk service"}")
        }
    }

    suspend fun createClient(
        name: String,
        day: String,
        month: String,
        phone: String,
        branchId: String
    ): RegistrationResult = withContext(Dispatchers.IO) {
        try {
            val deviceId = getDeviceId()
            val request = CreateClientRequest(
                name = name.trim(),
                day = day.trim(),
                month = month.trim(),
                phone = phone.trim(),
                branchId = branchId.trim()
            )
            val response = apiClient.getApi().createClient(deviceId, request)

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    val client = body?.client
                    if (client != null) {
                        RegistrationResult.Success(client, body.created ?: true)
                    } else {
                        RegistrationResult.Error("Server returned empty profile response. Please see reception.")
                    }
                }
                response.code() == 401 || response.code() == 403 -> {
                    clearSession()
                    RegistrationResult.Error("Kiosk approval expired or revoked.", isRevoked = true)
                }
                else -> {
                    RegistrationResult.Error("Could not create spa profile (HTTP ${response.code()}). Please see reception.")
                }
            }
        } catch (e: Exception) {
            RegistrationResult.Error("Network error during registration: ${e.localizedMessage ?: "Connection error"}")
        }
    }

    suspend fun checkInClient(
        phone: String,
        clientId: String,
        branchId: String?
    ): CheckInResult = withContext(Dispatchers.IO) {
        try {
            val deviceId = getDeviceId()
            val request = CheckInRequest(
                phone = phone.trim(),
                clientId = clientId.trim(),
                branchId = branchId?.trim()
            )
            val response = apiClient.getApi().checkIn(deviceId, request)

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    val client = body?.client
                    if (client != null) {
                        val isBday = BirthdayUtils.isTodayInKampala(client.day, client.month)
                        CheckInResult.Success(
                            client = client,
                            visit = body.visit,
                            alreadyCheckedIn = body.alreadyCheckedIn ?: false,
                            isBirthday = isBday
                        )
                    } else {
                        CheckInResult.Error("Check-in response missing client record. Please confirm with reception.")
                    }
                }
                response.code() == 401 || response.code() == 403 -> {
                    clearSession()
                    CheckInResult.Error("Kiosk authorization revoked.", isRevoked = true)
                }
                else -> {
                    CheckInResult.Error("Visit check-in could not be completed (HTTP ${response.code()}). Please see reception.")
                }
            }
        } catch (e: Exception) {
            CheckInResult.Error("Network error during check-in: ${e.localizedMessage ?: "Connection error"}")
        }
    }
}
