package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DmSans
import com.example.ui.theme.GuestFlowError
import com.example.ui.theme.GuestFlowInk
import com.example.ui.theme.GuestFlowLine
import com.example.ui.theme.GuestFlowLineInput
import com.example.ui.theme.GuestFlowMuted
import com.example.ui.theme.GuestFlowOrange
import com.example.ui.theme.GuestFlowWhite
import com.example.ui.theme.Playfair
import com.example.ui.theme.PlayfairDisplay
import java.time.YearMonth

@Composable
fun RegistrationWizard(
    step: Int,
    name: String,
    month: String,
    day: String,
    phone: String,
    error: String?,
    onNameChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onDayChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onPrevStep()
    }

    val progress = step / 3f

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isCompact = maxWidth < 600.dp
        val horizontalCardPadding = if (isCompact) 20.dp else 34.dp
        val verticalCardPadding = if (isCompact) 22.dp else 34.dp

        GuestFlowKioskCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalCardPadding, vertical = verticalCardPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 14.dp else 18.dp)
            ) {
                // Top Navigation: ← Back on left, Step "1 / 3" on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(onClick = onPrevStep)
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .testTag("reg_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GuestFlowMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Back",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = GuestFlowMuted
                        )
                    }

                    Text(
                        text = "$step  /  3",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuestFlowOrange
                    )
                }

                // Dark progressive indicator line matching reference design
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFE5E7EB))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(GuestFlowInk)
                    )
                }

                // Inline error alert if any
                AnimatedVisibility(visible = error != null) {
                    Surface(
                        color = GuestFlowError.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = GuestFlowError,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = error ?: "",
                                color = GuestFlowError,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Step Content with minimal professional step-to-step animation
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally(animationSpec = tween(280, easing = FastOutSlowInEasing)) { it / 6 } +
                                fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)))
                                .togetherWith(
                                    slideOutHorizontally(animationSpec = tween(200, easing = FastOutSlowInEasing)) { -it / 6 } +
                                        fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing))
                                )
                        } else {
                            (slideInHorizontally(animationSpec = tween(280, easing = FastOutSlowInEasing)) { -it / 6 } +
                                fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)))
                                .togetherWith(
                                    slideOutHorizontally(animationSpec = tween(200, easing = FastOutSlowInEasing)) { it / 6 } +
                                        fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing))
                                )
                        }
                    },
                    label = "wizard_step_transition"
                ) { currentStep ->
                    when (currentStep) {
                        1 -> StepOneName(
                            name = name,
                            isCompact = isCompact,
                            onNameChange = onNameChange,
                            onContinue = onNextStep
                        )
                        2 -> StepTwoBirthday(
                            selectedMonth = month,
                            selectedDay = day,
                            isCompact = isCompact,
                            onMonthChange = onMonthChange,
                            onDayChange = onDayChange,
                            onContinue = onNextStep
                        )
                        3 -> StepThreePhone(
                            phone = phone,
                            isCompact = isCompact,
                            onPhoneChange = onPhoneChange,
                            onSubmit = onNextStep
                        )
                    }
                }

                // Bottom trust footer
                Text(
                    text = "Your details are used only for your client record.",
                    fontSize = 12.sp,
                    color = GuestFlowMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun StepOneName(
    name: String,
    isCompact: Boolean,
    onNameChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 14.dp else 18.dp)
    ) {
        // Headline
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "What's your",
                fontFamily = DmSans,
                fontSize = if (isCompact) 28.sp else 36.sp,
                lineHeight = if (isCompact) 32.sp else 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp,
                color = GuestFlowInk,
                textAlign = TextAlign.Center
            )
            Text(
                text = "name?",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = if (isCompact) 32.sp else 40.sp,
                lineHeight = if (isCompact) 36.sp else 44.sp,
                fontWeight = FontWeight.Bold,
                color = GuestFlowOrange,
                textAlign = TextAlign.Center,
                modifier = Modifier.subtleEntrance(delayMillis = 70)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Let's start with the basics.",
                fontSize = if (isCompact) 14.sp else 16.sp,
                color = GuestFlowMuted,
                textAlign = TextAlign.Center
            )
        }

        // Field Label and Input
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Full name",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = GuestFlowInk
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 56.dp else 60.dp)
                    .testTag("reg_name_input"),
                placeholder = {
                    Text(
                        text = "e.g. Jordan Lee",
                        fontSize = if (isCompact) 15.sp else 17.sp,
                        color = GuestFlowMuted.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = DmSans,
                    fontSize = if (isCompact) 17.sp else 18.sp,
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
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { onContinue() })
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Primary Continue Button
        GuestFlowPrimaryButton(
            text = "Continue",
            height = if (isCompact) 54.dp else 58.dp,
            onClick = onContinue,
            modifier = Modifier.testTag("reg_step_next_button")
        )
    }
}

@Composable
private fun StepTwoBirthday(
    selectedMonth: String,
    selectedDay: String,
    isCompact: Boolean,
    onMonthChange: (String) -> Unit,
    onDayChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    // Mode toggles between Month Grid (Image 5) and Calendar Day picker (Image 6)
    var showCalendarDayPicker by remember { mutableStateOf(false) }

    val shortMonths = listOf(
        "Jan" to "January", "Feb" to "February", "Mar" to "March", "Apr" to "April",
        "May" to "May", "Jun" to "June", "Jul" to "July", "Aug" to "August",
        "Sep" to "September", "Oct" to "October", "Nov" to "November", "Dec" to "December"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 16.dp)
    ) {
        // Headline
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "When's your",
                fontFamily = DmSans,
                fontSize = if (isCompact) 28.sp else 36.sp,
                lineHeight = if (isCompact) 32.sp else 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp,
                color = GuestFlowInk,
                textAlign = TextAlign.Center
            )
            Text(
                text = "birthday?",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = if (isCompact) 32.sp else 40.sp,
                lineHeight = if (isCompact) 36.sp else 44.sp,
                fontWeight = FontWeight.Bold,
                color = GuestFlowOrange,
                textAlign = TextAlign.Center,
                modifier = Modifier.subtleEntrance(delayMillis = 70)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Choose a month, then select a day.",
                fontSize = if (isCompact) 14.sp else 15.sp,
                color = GuestFlowMuted,
                textAlign = TextAlign.Center
            )
        }

        if (!showCalendarDayPicker) {
            // Image 5: Month Selection Card (4x3 grid)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = GuestFlowLine, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = GuestFlowWhite
            ) {
                Column(
                    modifier = Modifier.padding(if (isCompact) 12.dp else 16.dp),
                    verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📅", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Select a month",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GuestFlowInk
                        )
                    }

                    // 4-column x 3-row month grid
                    val chunkedMonths = shortMonths.chunked(4)
                    chunkedMonths.forEach { rowMonths ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
                        ) {
                            rowMonths.forEach { (shortName, fullName) ->
                                val isSelected = selectedMonth.equals(fullName, ignoreCase = true)
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(if (isCompact) 42.dp else 46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) GuestFlowOrange else GuestFlowLine,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            onMonthChange(fullName)
                                            showCalendarDayPicker = true
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) GuestFlowOrange.copy(alpha = 0.08f) else GuestFlowWhite
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = shortName,
                                            fontSize = if (isCompact) 13.sp else 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) GuestFlowOrange else GuestFlowInk
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Image 6: Calendar Day Picker for the selected month
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = GuestFlowLine, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = GuestFlowWhite
            ) {
                Column(
                    modifier = Modifier.padding(if (isCompact) 12.dp else 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header with < Month Name >
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val currentIdx = shortMonths.indexOfFirst { it.second.equals(selectedMonth, ignoreCase = true) }
                                val prevIdx = if (currentIdx <= 0) 11 else currentIdx - 1
                                onMonthChange(shortMonths[prevIdx].second)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = GuestFlowInk)
                        }

                        Text(
                            text = selectedMonth,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuestFlowInk,
                            modifier = Modifier
                                .clickable { showCalendarDayPicker = false }
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )

                        IconButton(
                            onClick = {
                                val currentIdx = shortMonths.indexOfFirst { it.second.equals(selectedMonth, ignoreCase = true) }
                                val nextIdx = if (currentIdx >= 11) 0 else currentIdx + 1
                                onMonthChange(shortMonths[nextIdx].second)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Month", tint = GuestFlowInk)
                        }
                    }

                    // Days of week: S M T W T F S
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { dayLetter ->
                            Text(
                                text = dayLetter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GuestFlowMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(if (isCompact) 32.dp else 36.dp)
                            )
                        }
                    }

                    // Month days alignment calculation
                    val monthIndex = (shortMonths.indexOfFirst { it.second.equals(selectedMonth, ignoreCase = true) } + 1).coerceIn(1, 12)
                    val yearMonth = YearMonth.of(2026, monthIndex)
                    val daysInMonth = yearMonth.lengthOfMonth()
                    // 1st of month day-of-week (Sunday=0, Monday=1, ... Saturday=6)
                    val firstDayOfWeek = (yearMonth.atDay(1).dayOfWeek.value % 7)

                    val totalCells = firstDayOfWeek + daysInMonth
                    val rows = (totalCells + 6) / 7

                    for (r in 0 until rows) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            for (c in 0 until 7) {
                                val cellIndex = r * 7 + c
                                val dayNum = cellIndex - firstDayOfWeek + 1
                                if (dayNum in 1..daysInMonth) {
                                    val isSelected = selectedDay == dayNum.toString()
                                    val cellSize = if (isCompact) 32.dp else 36.dp
                                    Box(
                                        modifier = Modifier
                                            .size(cellSize)
                                            .clip(CircleShape)
                                            .background(if (isSelected) GuestFlowOrange else Color.Transparent)
                                            .clickable { onDayChange(dayNum.toString()) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dayNum.toString(),
                                            fontSize = if (isCompact) 13.sp else 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else GuestFlowInk
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(if (isCompact) 32.dp else 36.dp))
                                }
                            }
                        }
                    }

                    // Calendar footer note
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📅", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Selected: $selectedMonth $selectedDay",
                            fontSize = 12.sp,
                            color = GuestFlowMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Continue Button
        GuestFlowPrimaryButton(
            text = "Continue",
            height = if (isCompact) 54.dp else 58.dp,
            onClick = onContinue,
            modifier = Modifier.testTag("reg_step_next_button")
        )
    }
}

@Composable
private fun StepThreePhone(
    phone: String,
    isCompact: Boolean,
    onPhoneChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 14.dp else 18.dp)
    ) {
        // Headline - wrapped and scaled for phones
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "What's your",
                fontFamily = DmSans,
                fontSize = if (isCompact) 28.sp else 36.sp,
                lineHeight = if (isCompact) 32.sp else 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp,
                color = GuestFlowInk,
                textAlign = TextAlign.Center
            )
            Text(
                text = "phone number?",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = if (isCompact) 30.sp else 40.sp,
                lineHeight = if (isCompact) 34.sp else 44.sp,
                fontWeight = FontWeight.Bold,
                color = GuestFlowOrange,
                textAlign = TextAlign.Center,
                modifier = Modifier.subtleEntrance(delayMillis = 70)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "We'll use this to make your next visit quick.",
                fontSize = if (isCompact) 14.sp else 15.sp,
                color = GuestFlowMuted,
                textAlign = TextAlign.Center
            )
        }

        // Field Label and Input
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                    .testTag("reg_phone_input"),
                placeholder = {
                    Text(
                        text = "+256 700 000 000",
                        fontSize = if (isCompact) 15.sp else 17.sp,
                        color = GuestFlowMuted.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = DmSans,
                    fontSize = if (isCompact) 17.sp else 18.sp,
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
                keyboardActions = KeyboardActions(onDone = { onSubmit() })
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Primary Register & check in Button
        GuestFlowPrimaryButton(
            text = "Register & check in",
            height = if (isCompact) 54.dp else 58.dp,
            onClick = onSubmit,
            modifier = Modifier.testTag("reg_step_next_button")
        )
    }
}
