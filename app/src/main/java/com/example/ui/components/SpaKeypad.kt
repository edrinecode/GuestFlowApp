package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GuestFlowInk
import com.example.ui.theme.GuestFlowLine
import com.example.ui.theme.GuestFlowMuted
import com.example.ui.theme.GuestFlowOrange
import com.example.ui.theme.GuestFlowSoftSurface

@Composable
fun SpaKeypad(
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: 1, 2, 3
        KeypadRow(
            keys = listOf("1", "2", "3"),
            subtitles = listOf("", "ABC", "DEF"),
            onKeyClick = onDigitClick
        )

        // Row 2: 4, 5, 6
        KeypadRow(
            keys = listOf("4", "5", "6"),
            subtitles = listOf("GHI", "JKL", "MNO"),
            onKeyClick = onDigitClick
        )

        // Row 3: 7, 8, 9
        KeypadRow(
            keys = listOf("7", "8", "9"),
            subtitles = listOf("PQRS", "TUV", "WXYZ"),
            onKeyClick = onDigitClick
        )

        // Row 4: +, 0, Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Plus Key
            KeypadButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag("keypad_key_plus"),
                onClick = { onDigitClick("+") }
            ) {
                Text(
                    text = "+",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = GuestFlowInk
                )
            }

            // Zero Key
            KeypadButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag("keypad_key_0"),
                onClick = { onDigitClick("0") }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "0",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GuestFlowInk
                    )
                    Text(
                        text = "SPACE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = GuestFlowMuted,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Backspace Key
            KeypadButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag("keypad_key_backspace"),
                onClick = onBackspaceClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Delete last digit",
                    tint = GuestFlowMuted
                )
            }
        }
    }
}

@Composable
private fun KeypadRow(
    keys: List<String>,
    subtitles: List<String>,
    onKeyClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        keys.forEachIndexed { index, digit ->
            KeypadButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag("keypad_key_$digit"),
                onClick = { onKeyClick(digit) }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = digit,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GuestFlowInk
                    )
                    val sub = subtitles.getOrNull(index) ?: ""
                    if (sub.isNotEmpty()) {
                        Text(
                            text = sub,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = GuestFlowMuted,
                            letterSpacing = 1.2.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(width = 1.dp, color = GuestFlowLine.copy(alpha = 0.8f), shape = RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = GuestFlowSoftSurface.copy(alpha = 0.85f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier.padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
