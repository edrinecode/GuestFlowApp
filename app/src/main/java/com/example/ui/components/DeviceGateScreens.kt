package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GuestFlowCream
import com.example.ui.theme.GuestFlowError
import com.example.ui.theme.GuestFlowGold
import com.example.ui.theme.GuestFlowInk
import com.example.ui.theme.GuestFlowLine
import com.example.ui.theme.GuestFlowMuted
import com.example.ui.theme.GuestFlowOrange
import com.example.ui.theme.GuestFlowSoftSurface

@Composable
fun PendingApprovalScreen(
    deviceId: String,
    branchId: String,
    branchName: String,
    onRefreshClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(GuestFlowCream)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val isCompact = maxWidth < 600.dp
        val horizontalCardPadding = if (isCompact) 20.dp else 36.dp
        val verticalCardPadding = if (isCompact) 24.dp else 40.dp

        GuestFlowKioskCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalCardPadding, vertical = verticalCardPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 14.dp else 18.dp)
            ) {
                // Branded Emblem
                GuestFlowBrandMark(size = if (isCompact) 48.dp else 56.dp, iconSize = if (isCompact) 24.dp else 30.dp)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Device Pending Approval",
                        fontSize = if (isCompact) 22.sp else 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.6).sp,
                        color = GuestFlowInk,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "This kiosk is registered for $branchName ($branchId). Reception staff can approve this device in the GuestFlow Web Dashboard.",
                        fontSize = if (isCompact) 14.sp else 15.sp,
                        lineHeight = if (isCompact) 20.sp else 22.sp,
                        color = GuestFlowMuted,
                        textAlign = TextAlign.Center
                    )
                }

                // Copyable Device ID Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(width = 1.dp, color = GuestFlowLine, shape = RoundedCornerShape(14.dp))
                        .clickable {
                            clipboard.setText(AnnotatedString(deviceId))
                            Toast.makeText(context, "Device ID copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                    color = GuestFlowSoftSurface,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "INSTALLATION DEVICE ID  (TAP TO COPY)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = GuestFlowOrange
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = deviceId,
                                fontSize = if (isCompact) 11.sp else 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                color = GuestFlowInk
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Device ID",
                            tint = GuestFlowOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Polling indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(15.dp),
                        strokeWidth = 2.dp,
                        color = GuestFlowOrange
                    )
                    Text(
                        text = "Checking approval automatically every 5 seconds...",
                        fontSize = 12.sp,
                        color = GuestFlowMuted
                    )
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenSettingsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isCompact) 50.dp else 56.dp)
                            .testTag("kiosk_settings_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = GuestFlowMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Settings", color = GuestFlowInk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    Box(modifier = Modifier.weight(1.3f)) {
                        GuestFlowPrimaryButton(
                            text = "Check Now",
                            height = if (isCompact) 50.dp else 56.dp,
                            leadingIcon = Icons.Default.Refresh,
                            showArrow = false,
                            onClick = onRefreshClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RevokedScreen(
    message: String,
    onRetryClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(GuestFlowCream)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val isCompact = maxWidth < 600.dp
        val horizontalCardPadding = if (isCompact) 20.dp else 36.dp
        val verticalCardPadding = if (isCompact) 24.dp else 40.dp

        GuestFlowKioskCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalCardPadding, vertical = verticalCardPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 14.dp else 18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 52.dp else 64.dp)
                        .clip(CircleShape)
                        .background(GuestFlowError.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = "Revoked",
                        tint = GuestFlowError,
                        modifier = Modifier.size(if (isCompact) 26.dp else 32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Kiosk Access Revoked",
                        fontSize = if (isCompact) 20.sp else 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GuestFlowError,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.ifEmpty { "This device is no longer authorized. Please contact reception staff to re-enable kiosk check-ins." },
                        fontSize = if (isCompact) 14.sp else 15.sp,
                        lineHeight = if (isCompact) 20.sp else 22.sp,
                        color = GuestFlowMuted,
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenSettingsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isCompact) 50.dp else 54.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Settings", color = GuestFlowInk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    Box(modifier = Modifier.weight(1.3f)) {
                        GuestFlowPrimaryButton(
                            text = "Recheck",
                            height = if (isCompact) 50.dp else 54.dp,
                            showArrow = false,
                            onClick = onRetryClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionErrorScreen(
    message: String,
    onRetryClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(GuestFlowCream)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val isCompact = maxWidth < 600.dp
        val horizontalCardPadding = if (isCompact) 20.dp else 36.dp
        val verticalCardPadding = if (isCompact) 24.dp else 40.dp

        GuestFlowKioskCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalCardPadding, vertical = verticalCardPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 14.dp else 18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 52.dp else 64.dp)
                        .clip(CircleShape)
                        .background(GuestFlowGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Connection Error",
                        tint = GuestFlowOrange,
                        modifier = Modifier.size(if (isCompact) 26.dp else 32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Service Unavailable",
                        fontSize = if (isCompact) 20.sp else 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GuestFlowInk,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Could not connect to the GuestFlow server. Please check device connectivity or verify the server address.",
                        fontSize = if (isCompact) 14.sp else 15.sp,
                        lineHeight = if (isCompact) 20.sp else 22.sp,
                        color = GuestFlowMuted,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    color = GuestFlowSoftSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = GuestFlowMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenSettingsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isCompact) 50.dp else 54.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Configure URL", color = GuestFlowInk, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    Box(modifier = Modifier.weight(1.3f)) {
                        GuestFlowPrimaryButton(
                            text = "Retry",
                            height = if (isCompact) 50.dp else 54.dp,
                            showArrow = false,
                            onClick = onRetryClick
                        )
                    }
                }
            }
        }
    }
}
