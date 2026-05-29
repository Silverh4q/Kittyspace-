package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// GLOWING TEXT COMPOSABLE
@Composable
fun GlowText(
    text: String,
    glowColor: Color = MatrixGreen,
    textColor: Color = Color.White,
    fontSize: Float = 24f,
    letterSpacing: Float = 4f,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Blur Shadow for glow
        Text(
            text = text,
            color = glowColor.copy(alpha = 0.5f),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = letterSpacing.sp,
            modifier = Modifier
                .blur(4.dp)
                .offset(y = 1.dp)
        )
        // Hard Glow
        Text(
            text = text,
            color = glowColor.copy(alpha = 0.8f),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = letterSpacing.sp,
            modifier = Modifier.offset(x = 1.dp, y = 1.dp)
        )
        // Foreground Text
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = letterSpacing.sp
        )
    }
}

// CYBER CARD WITH HIGH CONTRAST NEON LIGHTING BORDER
@Composable
fun CyberCard(
    borderColor: Color = MatrixGreen,
    glowEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_border")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(
                1.5.dp,
                borderColor.copy(alpha = if (glowEnabled) borderAlpha else 0.4f),
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp)
    ) {
        content()
    }
}

// CAT CLAW PAW DYNAMIC LOGO (CANVAS VECTOR)
@Composable
fun CatPawLogo(
    iconSize: Dp = 60.dp,
    glowColor: Color = MatrixGreen,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "claw_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(iconSize * pulseScale)
            .blur(if (pulseScale > 1f) 1.dp else 0.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2, h / 2 + 10)

            // 1. Draw large center main pad (Cat claw pad)
            val padPath = Path().apply {
                moveTo(center.x - w * 0.2f, center.y + h * 0.12f)
                quadraticTo(center.x - w * 0.35f, center.y - h * 0.05f, center.x - w * 0.15f, center.y - h * 0.15f)
                quadraticTo(center.x, center.y - h * 0.22f, center.x + w * 0.15f, center.y - h * 0.15f)
                quadraticTo(center.x + w * 0.35f, center.y - h * 0.05f, center.x + w * 0.2f, center.y + h * 0.12f)
                quadraticTo(center.x + w * 0.10f, center.y + h * 0.24f, center.x, center.y + h * 0.20f)
                quadraticTo(center.x - w * 0.10f, center.y + h * 0.24f, center.x - w * 0.2f, center.y + h * 0.12f)
                close()
            }
            
            // Outer glow ring
            drawPath(
                path = padPath,
                color = glowColor.copy(alpha = 0.3f),
                style = Stroke(width = 6f)
            )
            
            drawPath(
                path = padPath,
                color = glowColor
            )

            // 2. Draw 4 Paw Fingers
            val padRadius = w * 0.11f
            // Left-Most Finger
            drawCircle(
                color = glowColor,
                radius = padRadius * 0.8f,
                center = Offset(w * 0.15f, h * 0.32f)
            )
            // Left-Middle Finger
            drawCircle(
                color = glowColor,
                radius = padRadius,
                center = Offset(w * 0.36f, h * 0.16f)
            )
            // Right-Middle Finger
            drawCircle(
                color = glowColor,
                radius = padRadius,
                center = Offset(w * 0.64f, h * 0.16f)
            )
            // Right-Most Finger
            drawCircle(
                color = glowColor,
                radius = padRadius * 0.8f,
                center = Offset(w * 0.85f, h * 0.32f)
            )

            // 3. Draw Cat scratches/claws on top of pad
            val lineStroke = 4f
            drawLine(
                color = SpaceBackground,
                start = Offset(w * 0.36f, h * 0.16f),
                end = Offset(w * 0.36f, h * 0.10f),
                strokeWidth = lineStroke
            )
            drawLine(
                color = SpaceBackground,
                start = Offset(w * 0.64f, h * 0.16f),
                end = Offset(w * 0.64f, h * 0.10f),
                strokeWidth = lineStroke
            )
        }
    }
}

// HIGH-FIDELITY VECTOR-RENDERED EMBEDDED RETRO APP ICONS COMPOSABLE
@Composable
fun AppIconRenderer(
    iconName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF261942), Color(0xFF110724))))
            .border(1.dp, Color(0xFF4C307C), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val w = size.width
            val h = size.height

            when (iconName) {
                "adder" -> {
                    // Pre-designed Green Plus Add button
                    // Draw outer glass rim
                    drawRoundRect(
                        color = MatrixGreen,
                        size = Size(w, h),
                        cornerRadius = CornerRadius(12f, 12f),
                        style = Stroke(width = 4f)
                    )
                    // Draw Plus lines
                    val lineLen = w * 0.5f
                    drawLine(
                        color = MatrixGreen,
                        start = Offset(w / 2 - lineLen / 2, h / 2),
                        end = Offset(w / 2 + lineLen / 2, h / 2),
                        strokeWidth = 8f
                    )
                    drawLine(
                        color = MatrixGreen,
                        start = Offset(w / 2, h / 2 - lineLen / 2),
                        end = Offset(w / 2, h / 2 + lineLen / 2),
                        strokeWidth = 8f
                    )
                }

                "weather" -> {
                    // Yellow glowing Sun behind soft white Cloud
                    // 1. Yellow Sun Circle
                    drawCircle(
                        color = Color(0xFFFFB300),
                        radius = w * 0.25f,
                        center = Offset(w * 0.65f, h * 0.35f)
                    )
                    // 2. Cloud paths (3 circles & base rect)
                    drawRoundRect(
                        color = Color(0xFFECEFF1),
                        topLeft = Offset(w * 0.15f, h * 0.50f),
                        size = Size(w * 0.7f, h * 0.28f),
                        cornerRadius = CornerRadius(15f, 15f)
                    )
                    drawCircle(
                        color = Color(0xFFECEFF1),
                        radius = w * 0.22f,
                        center = Offset(w * 0.40f, h * 0.46f)
                    )
                    drawCircle(
                        color = Color(0xFFCFD8DC),
                        radius = w * 0.18f,
                        center = Offset(w * 0.65f, h * 0.52f)
                    )
                }

                "remote" -> {
                    // Infrared Remote Indicator - concentric blue radar lines
                    val center = Offset(w / 2, h / 2)
                    drawCircle(
                        color = CyberCyan,
                        radius = w * 0.1f,
                        center = center
                    )
                    drawCircle(
                        color = CyberCyan,
                        radius = w * 0.25f,
                        center = center,
                        style = Stroke(width = 3f)
                    )
                    drawCircle(
                        color = CyberCyan.copy(alpha = 0.6f),
                        radius = w * 0.40f,
                        center = center,
                        style = Stroke(width = 3f)
                    )
                    // Signal rays
                    drawLine(
                        color = CyberCyan,
                        start = Offset(w * 0.15f, h * 0.15f),
                        end = Offset(w * 0.3f, h * 0.3f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = CyberCyan,
                        start = Offset(w * 0.85f, h * 0.15f),
                        end = Offset(w * 0.7f, h * 0.3f),
                        strokeWidth = 3f
                    )
                }

                "translate" -> {
                    // Translating card, glowing Blue Globe glyph
                    drawRoundRect(
                        color = Color(0xFF29B6F6),
                        topLeft = Offset(w * 0.15f, h * 0.15f),
                        size = Size(w * 0.7f, h * 0.7f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    // Simplified characters 'A' and 'T' inside
                    drawLine(
                        color = Color.White,
                        start = Offset(w * 0.3f, h * 0.7f),
                        end = Offset(w * 0.4f, h * 0.35f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(w * 0.4f, h * 0.35f),
                        end = Offset(w * 0.5f, h * 0.7f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(w * 0.33f, h * 0.58f),
                        end = Offset(w * 0.47f, h * 0.58f),
                        strokeWidth = 4f
                    )

                    // East Character cross symbol
                    drawLine(
                        color = Color.White,
                        start = Offset(w * 0.55f, h * 0.45f),
                        end = Offset(w * 0.75f, h * 0.45f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(w * 0.65f, h * 0.35f),
                        end = Offset(w * 0.65f, h * 0.65f),
                        strokeWidth = 4f
                    )
                }

                "recorder" -> {
                    // Vintage Cassette Tape icon (orange casing with two reels)
                    drawRoundRect(
                        color = Color(0xFFF15A24),
                        topLeft = Offset(w * 0.1f, h * 0.2f),
                        size = Size(w * 0.8f, h * 0.6f),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    // Cassette center window
                    drawRoundRect(
                        color = SpaceBackground,
                        topLeft = Offset(w * 0.25f, h * 0.35f),
                        size = Size(w * 0.5f, h * 0.3f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    // Reels
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.08f,
                        center = Offset(w * 0.38f, h * 0.48f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.08f,
                        center = Offset(w * 0.62f, h * 0.48f)
                    )
                }

                "calculator" -> {
                    // Grid of standard computing signs (plus, minus, division, equal)
                    val gap = 6f
                    val sizeW = (w - gap) / 2
                    val sizeH = (h - gap) / 2

                    // Card 1: Top Left Plus
                    drawRoundRect(
                        color = Color(0xFFECEFF1).copy(alpha = 0.15f),
                        topLeft = Offset(0f, 0f),
                        size = Size(sizeW, sizeH),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    drawLine(color = Color.White, start = Offset(sizeW / 2, sizeH * 0.25f), end = Offset(sizeW / 2, sizeH * 0.75f), strokeWidth = 3f)
                    drawLine(color = Color.White, start = Offset(sizeW * 0.25f, sizeH / 2), end = Offset(sizeW * 0.75f, sizeH / 2), strokeWidth = 3f)

                    // Card 2: Top Right Minus
                    drawRoundRect(
                        color = Color(0xFFECEFF1).copy(alpha = 0.15f),
                        topLeft = Offset(sizeW + gap, 0f),
                        size = Size(sizeW, sizeH),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    drawLine(color = Color.White, start = Offset(sizeW + gap + sizeW * 0.25f, sizeH / 2), end = Offset(sizeW + gap + sizeW * 0.75f, sizeH / 2), strokeWidth = 3f)

                    // Card 3: Bottom Left Multiply
                    drawRoundRect(
                        color = Color(0xFFECEFF1).copy(alpha = 0.15f),
                        topLeft = Offset(0f, sizeH + gap),
                        size = Size(sizeW, sizeH),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    drawLine(color = Color.White, start = Offset(sizeW * 0.28f, sizeH + gap + sizeH * 0.28f), end = Offset(sizeW * 0.72f, sizeH + gap + sizeH * 0.72f), strokeWidth = 3f)
                    drawLine(color = Color.White, start = Offset(sizeW * 0.72f, sizeH + gap + sizeH * 0.28f), end = Offset(sizeW * 0.28f, sizeH + gap + sizeH * 0.72f), strokeWidth = 3f)

                    // Card 4: Bottom Right Equal
                    drawRoundRect(
                        color = Color(0xFFFF5252).copy(alpha = 0.85f),
                        topLeft = Offset(sizeW + gap, sizeH + gap),
                        size = Size(sizeW, sizeH),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    drawLine(color = Color.White, start = Offset(sizeW + gap + sizeW * 0.25f, sizeH + gap + sizeH * 0.38f), end = Offset(sizeW + gap + sizeW * 0.75f, sizeH + gap + sizeH * 0.38f), strokeWidth = 3.5f)
                    drawLine(color = Color.White, start = Offset(sizeW + gap + sizeW * 0.25f, sizeH + gap + sizeH * 0.62f), end = Offset(sizeW + gap + sizeW * 0.75f, sizeH + gap + sizeH * 0.62f), strokeWidth = 3.5f)
                }

                "genshin" -> {
                    // Genshin Sword icon on glowing violet
                    val center = Offset(w / 2, h / 2)
                    drawCircle(
                        color = Color(0xFF9C27B0).copy(alpha = 0.4f),
                        radius = w * 0.45f,
                        center = center
                    )
                    // Drawing vector sword
                    drawLine(
                        color = Color(0xFFFFEB3B),
                        start = Offset(w * 0.25f, h * 0.75f),
                        end = Offset(w * 0.75f, h * 0.25f),
                        strokeWidth = 6f
                    )
                    // Hilt guard
                    drawLine(
                        color = Color(0xFFEEEEEE),
                        start = Offset(w * 0.32f, h * 0.58f),
                        end = Offset(w * 0.42f, h * 0.68f),
                        strokeWidth = 6f
                    )
                }

                "pubg" -> {
                    // Unreal tactical steel helmet
                    val center = Offset(w / 2, h / 2)
                    drawRoundRect(
                        color = Color(0xFF455A64),
                        topLeft = Offset(w * 0.2f, h * 0.25f),
                        size = Size(w * 0.6f, h * 0.45f),
                        cornerRadius = CornerRadius(14f, 14f)
                    )
                    // Eye shield grill
                    drawRect(
                        color = MatrixGreen.copy(alpha = 0.8f),
                        topLeft = Offset(w * 0.25f, h * 0.36f),
                        size = Size(w * 0.5f, h * 0.12f)
                    )
                    drawLine(
                        color = SpaceBackground,
                        start = Offset(w * 0.4f, h * 0.36f),
                        end = Offset(w * 0.4f, h * 0.48f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = SpaceBackground,
                        start = Offset(w * 0.5f, h * 0.36f),
                        end = Offset(w * 0.5f, h * 0.48f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = SpaceBackground,
                        start = Offset(w * 0.6f, h * 0.36f),
                        end = Offset(w * 0.6f, h * 0.48f),
                        strokeWidth = 3f
                    )
                }

                "subway" -> {
                    // Retro subway surf spray paint can icon
                    drawRoundRect(
                        color = Color(0xFFFF5252),
                        topLeft = Offset(w * 0.32f, h * 0.28f),
                        size = Size(w * 0.36f, h * 0.52f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    // Spray nozzle
                    drawRect(
                        color = Color.LightGray,
                        topLeft = Offset(w * 0.42f, h * 0.16f),
                        size = Size(w * 0.16f, h * 0.12f)
                    )
                    // Spray mist vectors
                    drawCircle(color = CyberCyan.copy(alpha = 0.7f), radius = w * 0.08f, center = Offset(w * 0.2f, h * 0.14f))
                    drawCircle(color = CyberCyan.copy(alpha = 0.5f), radius = w * 0.05f, center = Offset(w * 0.25f, h * 0.22f))
                }

                "minecraft" -> {
                    // Pixel dirt/grass block grid
                    val sizeX = w / 3
                    val sizeY = h / 3
                    for (row in 0..2) {
                        for (col in 0..2) {
                            val colorHex = when {
                                row == 0 -> Color(0xFF4CAF50) // Grass
                                row == 1 && col == 1 -> Color(0xFF8D6E63) // Stone/dirt
                                else -> Color(0xFF795548) // Dirt
                            }
                            drawRect(
                                color = colorHex,
                                topLeft = Offset(col * sizeX, row * sizeY),
                                size = Size(sizeX - 1f, sizeY - 1f)
                            )
                        }
                    }
                }

                else -> {
                    // Dynamic Custom Cloned Game / App - Drawing retro game pad controller
                    val center = Offset(w / 2, h / 2)
                    drawRoundRect(
                        color = CyberPink,
                        topLeft = Offset(w * 0.15f, h * 0.3f),
                        size = Size(w * 0.7f, h * 0.4f),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    // Joypad buttons
                    drawCircle(color = Color.White, radius = w * 0.08f, center = Offset(w * 0.32f, h * 0.5f))
                    drawCircle(color = MatrixGreen, radius = w * 0.06f, center = Offset(w * 0.62f, h * 0.44f))
                    drawCircle(color = MatrixGreen, radius = w * 0.06f, center = Offset(w * 0.72f, h * 0.56f))
                }
            }
        }
    }
}
