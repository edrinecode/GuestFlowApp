package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConnectionErrorScreen
import com.example.ui.components.FrontDeskAssistScreen
import com.example.ui.components.GuestFlowBrandMark
import com.example.ui.components.GuestFlowKioskCard
import com.example.ui.components.GuestFlowPrimaryButton
import com.example.ui.components.GuestFlowRegisterGreenButton
import com.example.ui.components.PendingApprovalScreen
import com.example.ui.components.RegistrationWizard
import com.example.ui.components.RevokedScreen
import com.example.ui.components.StaffSettingsDialog
import com.example.ui.components.SuccessScreen
import com.example.ui.components.getTimeOfDayGreeting
import com.example.ui.components.subtleEntrance
import com.example.ui.theme.DmMono
import com.example.ui.theme.DmSans
import com.example.ui.theme.GuestFlowCream
import com.example.ui.theme.GuestFlowError
import com.example.ui.theme.GuestFlowInk
import com.example.ui.theme.GuestFlowLine
import com.example.ui.theme.GuestFlowLineInput
import com.example.ui.theme.GuestFlowMuted
import com.example.ui.theme.GuestFlowOrange
import com.example.ui.theme.GuestFlowSoftSurface
import com.example.ui.theme.GuestFlowWhite
import com.example.ui.theme.Playfair
import com.example.ui.theme.PlayfairDisplay
import com.example.ui.viewmodel.DeviceGateState
import com.example.ui.viewmodel.KioskScreen
import com.example.ui.viewmodel.KioskViewModel

@Composable
fun MainKioskScreen(
    viewModel: KioskViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Staff settings dialog if requested
    if (uiState.showStaffSettings) {
        StaffSettingsDialog(
            deviceId = viewModel.getDeviceId(),
            serverUrl = uiState.staffServerUrl,
            branchId = uiState.staffBranchId,
            statusMessage = uiState.staffStatusMessage,
            isTesting = uiState.isTestingConnection,
            onServerUrlChange = viewModel::updateStaffServerUrl,
            onBranchIdChange = viewModel::updateStaffBranchId,
            onTestConnection = viewModel::testConnection,
            onSave = viewModel::saveStaffSettings,
            onDismiss = viewModel::closeStaffSettings
        )
    }

    // Evaluate device gate status first
    when (val gate = uiState.deviceGate) {
        is DeviceGateState.Checking -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .background(GuestFlowCream),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GuestFlowBrandMark(size = 54.dp, iconSize = 28.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    CircularProgressIndicator(color = GuestFlowOrange, strokeWidth = 3.dp, modifier = Modifier.size(36.dp))
                    Text(
                        text = "Connecting to GuestFlow...",
                        fontFamily = DmSans,
                        fontSize = 15.sp,
                        color = GuestFlowInk,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        is DeviceGateState.PendingApproval -> {
            PendingApprovalScreen(
                deviceId = gate.deviceId,
                branchId = gate.branchId,
                branchName = gate.branchName,
                onRefreshClick = viewModel::checkDeviceStatus,
                onOpenSettingsClick = viewModel::openStaffSettings
            )
        }
        is DeviceGateState.Revoked -> {
            RevokedScreen(
                message = gate.message,
                onRetryClick = viewModel::checkDeviceStatus,
                onOpenSettingsClick = viewModel::openStaffSettings
            )
        }
        is DeviceGateState.ConnectionError -> {
            ConnectionErrorScreen(
                message = gate.message,
                onRetryClick = viewModel::checkDeviceStatus,
                onOpenSettingsClick = viewModel::openStaffSettings
            )
        }
        is DeviceGateState.Approved -> {
            // Device is authorized: Show the customer-facing kiosk
            Scaffold(
                modifier = modifier
                    .fillMaxSize()
                    .imePadding(),
                containerColor = GuestFlowCream,
                topBar = {
                    KioskTopBar(
                        branchName = gate.branchName,
                        onOpenSettings = viewModel::openStaffSettings
                    )
                }
            ) { innerPadding ->
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(GuestFlowCream)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val isCompact = maxWidth < 600.dp

                    AnimatedContent(
                        targetState = uiState.currentScreen,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(320, easing = FastOutSlowInEasing)) +
                                slideInVertically(
                                    animationSpec = tween(320, easing = FastOutSlowInEasing),
                                    initialOffsetY = { (it * 0.04f).toInt() }
                                )).togetherWith(
                                    fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing))
                                )
                        },
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth(),
                        label = "kiosk_screen_transition"
                    ) { screen ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            when (screen) {
                                is KioskScreen.Welcome -> {
                                    WelcomeKioskCard(
                                        phone = uiState.phoneInput,
                                        error = uiState.phoneError,
                                        phoneNotFound = uiState.phoneNotFound,
                                        branchName = gate.branchName,
                                        isCompact = isCompact,
                                        onPhoneChange = viewModel::onPhoneInputChange,
                                        onCheckInClick = viewModel::startLookup,
                                        onRegisterClick = viewModel::startNewGuestRegistration
                                    )
                                }
                                is KioskScreen.LookingUp -> {
                                    LoadingKioskCard(
                                        title = "Looking Up Your Reservation",
                                        subtitle = "Searching SpaGym client records..."
                                    )
                                }
                                is KioskScreen.RegisterWizard -> {
                                    RegistrationWizard(
                                        step = screen.step,
                                        name = uiState.regName,
                                        month = uiState.regMonth,
                                        day = uiState.regDay,
                                        phone = uiState.regPhone,
                                        error = uiState.regError,
                                        onNameChange = viewModel::updateRegName,
                                        onMonthChange = viewModel::updateRegMonth,
                                        onDayChange = viewModel::updateRegDay,
                                        onPhoneChange = viewModel::updateRegPhone,
                                        onNextStep = viewModel::nextRegStep,
                                        onPrevStep = viewModel::prevRegStep,
                                        onCancel = viewModel::privacyReset
                                    )
                                }
                                is KioskScreen.CheckingIn -> {
                                    LoadingKioskCard(
                                        title = "Recording Spa Visit",
                                        subtitle = "Finalizing your check-in details..."
                                    )
                                }
                                is KioskScreen.Success -> {
                                    SuccessScreen(
                                        firstName = screen.firstName,
                                        alreadyCheckedIn = screen.alreadyCheckedIn,
                                        isBirthday = screen.isBirthday,
                                        countdownSeconds = screen.countdownSeconds,
                                        onDoneClick = viewModel::privacyReset
                                    )
                                }
                                is KioskScreen.FrontDeskAssist -> {
                                    FrontDeskAssistScreen(
                                        firstName = screen.firstName,
                                        message = screen.message,
                                        onDoneClick = viewModel::privacyReset
                                    )
                                }
                                is KioskScreen.Error -> {
                                    ErrorKioskCard(
                                        message = screen.message,
                                        canRetry = screen.canRetry,
                                        onRetryClick = viewModel::startLookup,
                                        onBackClick = viewModel::privacyReset
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KioskTopBar(
    branchName: String,
    onOpenSettings: () -> Unit
) {
    Surface(
        color = GuestFlowCream,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Title & Status Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GuestFlowBrandMark(size = 38.dp, iconSize = 20.dp)

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "GuestFlow",
                            fontFamily = DmSans,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuestFlowInk,
                            letterSpacing = (-0.3).sp
                        )
                        Surface(
                            color = GuestFlowSoftSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GuestFlowLine),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Text(
                                    text = "Ready",
                                    fontFamily = DmMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GuestFlowInk
                                )
                            }
                        }
                    }
                    Text(
                        text = branchName,
                        fontFamily = DmSans,
                        fontSize = 12.sp,
                        color = GuestFlowMuted
                    )
                }
            }

            // Staff Gear Icon
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("kiosk_settings_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Staff Kiosk Settings",
                    tint = GuestFlowMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Image 1 & Image 3: The signature Welcome & Check-In card
 * Responsive padding and font sizes guarantee zero text squeezing on Samsung A54 and mini tablets.
 */
@Composable
private fun WelcomeKioskCard(
    phone: String,
    error: String?,
    phoneNotFound: Boolean,
    branchName: String,
    isCompact: Boolean,
    onPhoneChange: (String) -> Unit,
    onCheckInClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val cardHorizontalPadding = if (isCompact) 20.dp else 36.dp
    val cardVerticalPadding = if (isCompact) 24.dp else 38.dp

    GuestFlowKioskCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = cardHorizontalPadding, vertical = cardVerticalPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 14.dp else 18.dp)
        ) {
            // Header: Dynamic time of day greeting with warm dot
            Row(
                modifier = Modifier.subtleEntrance(delayMillis = 0),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(GuestFlowOrange)
                )
                Text(
                    text = getTimeOfDayGreeting(),
                    fontFamily = DmMono,
                    fontSize = if (isCompact) 11.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = GuestFlowOrange,
                    textAlign = TextAlign.Center
                )
            }

            // Header: Welcome to [BranchName]
            Text(
                text = buildAnnotatedString {
                    append("Welcome to ")
                    withStyle(style = SpanStyle(color = GuestFlowOrange, fontWeight = FontWeight.Bold)) {
                        append(branchName.ifBlank { "Soothing Spot Spa" })
                    }
                },
                fontFamily = DmSans,
                fontSize = if (isCompact) 16.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = GuestFlowInk,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .subtleEntrance(delayMillis = 50)
            )

            // Massive Distinct Display Title:
            // "Digital" in bold dark DM Sans
            // "Registration Book" in Google Font Playfair - Bold 700 Italic in luxury orange
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .subtleEntrance(delayMillis = 100),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Digital",
                    fontFamily = DmSans,
                    fontSize = if (isCompact) 34.sp else 42.sp,
                    lineHeight = if (isCompact) 38.sp else 46.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1.0).sp,
                    color = GuestFlowInk,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Registration Book",
                    fontFamily = Playfair,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isCompact) 36.sp else 44.sp,
                    lineHeight = if (isCompact) 40.sp else 48.sp,
                    color = GuestFlowOrange,
                    textAlign = TextAlign.Center
                )
            }

            // Subtitle
            Text(
                text = "Please enter your phone number below so we know you're here.",
                fontFamily = DmSans,
                fontSize = if (isCompact) 14.sp else 15.sp,
                lineHeight = if (isCompact) 20.sp else 22.sp,
                color = GuestFlowMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isCompact) 4.dp else 8.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Phone Number Input with Label
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Phone number",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GuestFlowInk
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isCompact) 56.dp else 60.dp)
                        .testTag("phone_input_field"),
                    placeholder = {
                        Text(
                            text = "+256 7XX XXX XXX or your local number",
                            fontSize = if (isCompact) 14.sp else 15.sp,
                            color = GuestFlowMuted.copy(alpha = 0.55f)
                        )
                    },
                    trailingIcon = {
                        if (phone.isNotEmpty()) {
                            IconButton(onClick = { onPhoneChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = GuestFlowMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = DmSans,
                        fontSize = if (isCompact) 18.sp else 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GuestFlowInk
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GuestFlowOrange,
                        unfocusedBorderColor = GuestFlowLineInput,
                        focusedContainerColor = GuestFlowWhite,
                        unfocusedContainerColor = GuestFlowWhite,
                        cursorColor = GuestFlowOrange
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onCheckInClick() }
                    )
                )

                if (error != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = GuestFlowError,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = error,
                            color = GuestFlowError,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Primary Check In Button: "Please check in →"
            GuestFlowPrimaryButton(
                text = "Please check in",
                height = if (isCompact) 54.dp else 58.dp,
                onClick = onCheckInClick,
                modifier = Modifier.testTag("welcome_check_in_button")
            )

            // Image 3: Green button displayed when phone lookup returns 404
            if (phoneNotFound) {
                GuestFlowRegisterGreenButton(
                    text = "Phone No. not found! Register Here.",
                    height = if (isCompact) 54.dp else 58.dp,
                    onClick = onRegisterClick,
                    modifier = Modifier.testTag("welcome_register_not_found_button")
                )
            } else {
                // Secondary subtle prompt for new guests
                Row(
                    modifier = Modifier
                        .clickable(onClick = onRegisterClick)
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .testTag("welcome_register_new_guest_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "First time visiting? ",
                        fontSize = 13.sp,
                        color = GuestFlowMuted
                    )
                    Text(
                        text = "Register as a new guest →",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuestFlowOrange
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingKioskCard(
    title: String,
    subtitle: String
) {
    GuestFlowKioskCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 42.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GuestFlowBrandMark(size = 50.dp, iconSize = 25.dp)

            Spacer(modifier = Modifier.height(2.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(44.dp),
                color = GuestFlowOrange,
                strokeWidth = 3.5.dp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                fontFamily = DmSans,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                color = GuestFlowInk,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .subtleEntrance(delayMillis = 40)
            )

            Text(
                text = subtitle,
                fontFamily = DmSans,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = GuestFlowMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .subtleEntrance(delayMillis = 100)
            )
        }
    }
}

@Composable
private fun ErrorKioskCard(
    message: String,
    canRetry: Boolean,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit
) {
    GuestFlowKioskCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(GuestFlowError.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Error",
                    tint = GuestFlowError,
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "Check-in Incomplete",
                fontFamily = DmSans,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GuestFlowInk,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = message,
                fontFamily = DmSans,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = GuestFlowMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Start Over", color = GuestFlowInk, fontWeight = FontWeight.SemiBold)
                }

                if (canRetry) {
                    Box(modifier = Modifier.weight(1.2f)) {
                        GuestFlowPrimaryButton(
                            text = "Retry",
                            height = 50.dp,
                            showArrow = false,
                            onClick = onRetryClick
                        )
                    }
                }
            }
        }
    }
}
