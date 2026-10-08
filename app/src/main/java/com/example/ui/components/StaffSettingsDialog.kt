package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.GuestFlowInk
import com.example.ui.theme.GuestFlowLine
import com.example.ui.theme.GuestFlowLineInput
import com.example.ui.theme.GuestFlowMuted
import com.example.ui.theme.GuestFlowOrange
import com.example.ui.theme.GuestFlowSoftSurface
import com.example.ui.theme.GuestFlowWhite

@Composable
fun StaffSettingsDialog(
    deviceId: String,
    serverUrl: String,
    branchId: String,
    statusMessage: String?,
    isTesting: Boolean,
    onServerUrlChange: (String) -> Unit,
    onBranchIdChange: (String) -> Unit,
    onTestConnection: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .border(width = 1.dp, color = GuestFlowLine, shape = RoundedCornerShape(24.dp))
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GuestFlowWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Kiosk Administration",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuestFlowInk
                        )
                        Text(
                            text = "Staff device enrollment & endpoint setup",
                            fontSize = 13.sp,
                            color = GuestFlowMuted
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = GuestFlowInk)
                    }
                }

                // Installation Device ID display
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(width = 1.dp, color = GuestFlowLine, shape = RoundedCornerShape(14.dp))
                        .clickable {
                            clipboard.setText(AnnotatedString(deviceId))
                            Toast.makeText(context, "Device ID copied!", Toast.LENGTH_SHORT).show()
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
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = deviceId,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = GuestFlowInk
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = GuestFlowOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Server Base URL field
                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = onServerUrlChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("staff_server_url_input"),
                    label = { Text("GuestFlow Server Base URL") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GuestFlowOrange,
                        unfocusedBorderColor = GuestFlowLineInput,
                        focusedLabelColor = GuestFlowOrange,
                        cursorColor = GuestFlowOrange
                    )
                )

                // Branch ID field
                OutlinedTextField(
                    value = branchId,
                    onValueChange = onBranchIdChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("staff_branch_id_input"),
                    label = { Text("Kiosk Branch ID") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GuestFlowOrange,
                        unfocusedBorderColor = GuestFlowLineInput,
                        focusedLabelColor = GuestFlowOrange,
                        cursorColor = GuestFlowOrange
                    )
                )

                // Status Message display
                if (statusMessage != null) {
                    Surface(
                        color = GuestFlowSoftSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GuestFlowLine),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = GuestFlowOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = statusMessage,
                                fontSize = 13.sp,
                                color = GuestFlowInk
                            )
                        }
                    }
                }

                // Action buttons: Test Connection & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onTestConnection,
                        enabled = !isTesting,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("staff_test_connection_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = GuestFlowOrange
                            )
                        } else {
                            Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = null, tint = GuestFlowInk, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test API", color = GuestFlowInk, fontWeight = FontWeight.SemiBold)
                    }

                    Box(modifier = Modifier.weight(1.3f)) {
                        GuestFlowPrimaryButton(
                            text = "Save & Apply",
                            height = 52.dp,
                            onClick = onSave
                        )
                    }
                }
            }
        }
    }
}
