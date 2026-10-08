package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DmSans
import com.example.ui.theme.GuestFlowCream
import com.example.ui.theme.GuestFlowGold
import com.example.ui.theme.GuestFlowInk
import com.example.ui.theme.GuestFlowLilac
import com.example.ui.theme.GuestFlowLine
import com.example.ui.theme.GuestFlowMuted
import com.example.ui.theme.GuestFlowOrange
import com.example.ui.theme.GuestFlowSoftSurface
import com.example.ui.theme.Playfair
import com.example.ui.theme.PlayfairDisplay

@Composable
fun SuccessScreen(
    firstName: String,
    alreadyCheckedIn: Boolean,
    isBirthday: Boolean,
    countdownSeconds: Int,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (countdownSeconds / 5f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "countdown_progress")

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
        val horizontalCardPadding = if (isCompact) 22.dp else 36.dp
        val verticalCardPadding = if (isCompact) 26.dp else 40.dp

        GuestFlowKioskCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalCardPadding, vertical = verticalCardPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 14.dp else 18.dp)
            ) {
                // Top Badge: Soft pastel yellow/gold rounded square with checkmark icon
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 48.dp else 54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFDE68A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success Checkmark",
                        tint = Color(0xFF854D0E),
                        modifier = Modifier.size(if (isCompact) 24.dp else 28.dp)
                    )
                }

                // Subtitle Header: YOU'RE ALL SET
                Text(
                    text = "YOU'RE  ALL  SET",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = GuestFlowMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.subtleEntrance(delayMillis = 0)
                )

                // Headline: Welcome, [FirstName].
                // Uses buildAnnotatedString with Playfair Google Font
                Text(
                    text = buildAnnotatedString {
                        append("Welcome, ")
                        withStyle(
                            style = SpanStyle(
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold,
                                color = GuestFlowOrange
                            )
                        ) {
                            append("$firstName.")
                        }
                    },
                    fontFamily = DmSans,
                    fontSize = if (isCompact) 32.sp else 38.sp,
                    lineHeight = if (isCompact) 38.sp else 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp,
                    color = GuestFlowInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .subtleEntrance(delayMillis = 60)
                )

                // Description
                Text(
                    text = if (alreadyCheckedIn) {
                        "Your visit for today is already recorded. Please take a seat and we'll be with you shortly."
                    } else {
                        "Your visit has been recorded. Please take a seat and we'll be with you shortly."
                    },
                    fontSize = if (isCompact) 15.sp else 16.sp,
                    lineHeight = if (isCompact) 22.sp else 24.sp,
                    color = GuestFlowMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = if (isCompact) 4.dp else 12.dp)
                )

                // Celebratory Birthday Banner if applicable
                if (isBirthday) {
                    Surface(
                        color = Color(0xFFFFFBEB),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, GuestFlowGold),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GuestFlowOrange, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Happy Birthday, $firstName! 🎂",
                                    fontWeight = FontWeight.Bold,
                                    color = GuestFlowInk,
                                    fontSize = 16.sp
                                )
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GuestFlowLilac, modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = "Wishing you a wonderfully relaxing spa day! Please mention your birthday to reception for your complimentary gift.",
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center,
                                color = GuestFlowMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Privacy countdown progress bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = GuestFlowOrange,
                        trackColor = GuestFlowLine
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = GuestFlowMuted
                        )
                        Text(
                            text = "Resetting in $countdownSeconds seconds for privacy",
                            fontSize = 12.sp,
                            color = GuestFlowMuted
                        )
                    }
                }

                // Done Button
                GuestFlowPrimaryButton(
                    text = "Done  •  Next Guest",
                    height = if (isCompact) 54.dp else 58.dp,
                    showArrow = false,
                    onClick = onDoneClick,
                    modifier = Modifier.testTag("done_next_guest_button")
                )
            }
        }
    }
}

@Composable
fun FrontDeskAssistScreen(
    firstName: String,
    message: String,
    onDoneClick: () -> Unit,
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
        val horizontalCardPadding = if (isCompact) 22.dp else 36.dp
        val verticalCardPadding = if (isCompact) 26.dp else 40.dp

        GuestFlowKioskCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalCardPadding, vertical = verticalCardPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 56.dp else 68.dp)
                        .clip(CircleShape)
                        .background(GuestFlowSoftSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Reception Assistance",
                        tint = GuestFlowOrange,
                        modifier = Modifier.size(if (isCompact) 30.dp else 36.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome, $firstName!",
                        fontFamily = DmSans,
                        fontSize = if (isCompact) 24.sp else 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuestFlowInk,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message,
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        lineHeight = if (isCompact) 20.sp else 23.sp,
                        color = GuestFlowMuted,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    color = GuestFlowSoftSurface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Our reception team will gladly finalize your check-in right away.",
                        fontSize = 13.sp,
                        color = GuestFlowMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                GuestFlowPrimaryButton(
                    text = "Return to Welcome",
                    height = if (isCompact) 54.dp else 58.dp,
                    showArrow = false,
                    onClick = onDoneClick,
                    modifier = Modifier.testTag("front_desk_done_button")
                )
            }
        }
    }
}
