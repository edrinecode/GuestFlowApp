package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GuestFlowLine
import com.example.ui.theme.GuestFlowOrange
import com.example.ui.theme.GuestFlowOrangeDark
import com.example.ui.theme.GuestFlowOrangeStart
import com.example.ui.theme.GuestFlowShadow
import com.example.ui.theme.GuestFlowWhite
import java.time.LocalTime

/**
 * Dynamic time-of-day greeting: "GOOD MORNING", "GOOD AFTERNOON", or "GOOD EVENING"
 */
fun getTimeOfDayGreeting(): String {
    val hour = LocalTime.now().hour
    return when (hour) {
        in 5..11 -> "GOOD MORNING"
        in 12..16 -> "GOOD AFTERNOON"
        else -> "GOOD EVENING"
    }
}

/**
 * Minimalist luxury brand emblem: a soft-radius rounded square with warm orange gradient and clean emblem.
 */
@Composable
fun GuestFlowBrandMark(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 26.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(14.dp), ambientColor = GuestFlowOrange, spotColor = GuestFlowOrange)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(GuestFlowOrangeStart, GuestFlowOrange)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "GuestFlow Emblem",
            tint = GuestFlowWhite,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Signature GuestFlow primary button with arrow:
 * Orange linear gradient #FF710F to #FF5A0A, 14dp rounded, white bold label with arrow.
 */
@Composable
fun GuestFlowPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    height: Dp = 58.dp,
    leadingIcon: ImageVector? = null,
    showArrow: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.985f else 1.0f, label = "button_scale")

    val gradient = if (enabled) {
        Brush.linearGradient(
            colors = if (isPressed) {
                listOf(GuestFlowOrangeDark, GuestFlowOrangeDark)
            } else {
                listOf(GuestFlowOrangeStart, GuestFlowOrange)
            }
        )
    } else {
        Brush.linearGradient(colors = listOf(Color(0xFFE5DDD7), Color(0xFFD8CEC6)))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            .shadow(
                elevation = if (enabled && !isPressed) 6.dp else 1.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = GuestFlowShadow,
                spotColor = GuestFlowShadow
            )
            .clip(RoundedCornerShape(14.dp))
            .background(gradient)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = GuestFlowWhite,
                strokeWidth = 3.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = GuestFlowWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = GuestFlowWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp
                )
                if (showArrow) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = GuestFlowWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Green registration prompt button (shown when phone is not found or for quick register):
 * Background #22C55E, 14dp rounded, white bold label with arrow.
 */
@Composable
fun GuestFlowRegisterGreenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 58.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.985f else 1.0f, label = "green_button_scale")

    val greenColor = if (isPressed) Color(0xFF16A34A) else Color(0xFF22C55E)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            .shadow(
                elevation = if (!isPressed) 6.dp else 1.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0x2216A34A),
                spotColor = Color(0x2216A34A)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(greenColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Responsive centered card container tailored for Samsung Galaxy A54 phone & Galaxy Tab A-series mini tablets:
 * Rounded 28dp corners, 1dp subtle warm border, soft brown shadow.
 */
@Composable
fun GuestFlowKioskCard(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 580.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .widthIn(max = maxWidth)
            .fillMaxWidth()
            .border(width = 1.dp, color = GuestFlowLine, shape = RoundedCornerShape(28.dp))
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = GuestFlowShadow,
                spotColor = GuestFlowShadow
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = GuestFlowWhite)
    ) {
        content()
    }
}

/**
 * Minimal, subtle, and professional entrance animation:
 * Plays strictly ONCE when the element appears on screen.
 * Softly fades in (alpha 0 -> 1) and drifts up by a subtle 6dp offset.
 * Does NOT repeat on and on.
 */
@Composable
fun Modifier.subtleEntrance(
    delayMillis: Int = 0,
    durationMillis: Int = 320
): Modifier {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (delayMillis > 0) {
            delay(delayMillis.toLong())
        }
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        label = "subtle_entrance_alpha"
    )
    val translationY by animateFloatAsState(
        targetValue = if (visible) 0f else 6f,
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        label = "subtle_entrance_translation"
    )
    return this.graphicsLayer {
        this.alpha = alpha
        this.translationY = translationY * density
    }
}

// Compatibility mappings that guarantee non-repeating, subtle entrance motion
@Composable
fun Modifier.soothingBreathe(
    durationMillis: Int = 2200,
    minScale: Float = 0.94f,
    maxScale: Float = 1.04f,
    minAlpha: Float = 0.65f
): Modifier = this.subtleEntrance(durationMillis = 300)

@Composable
fun Modifier.soothingFloat(
    driftDp: Float = 4.5f,
    durationMillis: Int = 2400
): Modifier = this.subtleEntrance(durationMillis = 300)

@Composable
fun Modifier.soothingLuxuryGlow(
    durationMillis: Int = 2400
): Modifier = this.subtleEntrance(durationMillis = 340)

