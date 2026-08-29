package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundFX
import com.example.model.AlphablocksRegistry

@Composable
fun RealisticAlphablockView(
    letter: Char,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 80.dp,
    showGlow: Boolean = false,
    interactive: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val data = remember(letter) { AlphablocksRegistry.get(letter) }

    val infiniteTransition = rememberInfiniteTransition(label = "alpha_bounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = modifier
            .size(sizeDp)
            .testTag("alphablock_${letter}")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = interactive
            ) {
                SoundFX.playLetterSound(letter)
                onClick?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (showGlow) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD54F).copy(alpha = 0.8f),
                            data.color.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(w / 2, h / 2 + bounceOffset),
                        radius = w * 0.65f
                    )
                )
            }

            val blockSize = minOf(w, h) * 0.75f
            val bx = (w - blockSize) / 2
            val by = (h - blockSize) / 2 + bounceOffset
            val cornerRadius = CornerRadius(blockSize * 0.22f, blockSize * 0.22f)
            val bevel = blockSize * 0.12f

            // Shadow / Base
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.35f),
                topLeft = Offset(bx, by + bevel * 0.6f),
                size = Size(blockSize, blockSize),
                cornerRadius = cornerRadius
            )

            // Glossy Front
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        data.color.copy(alpha = 0.7f),
                        data.color,
                        data.color.copy(alpha = 0.9f)
                    ),
                    start = Offset(bx, by),
                    end = Offset(bx + blockSize, by + blockSize)
                ),
                topLeft = Offset(bx + bevel * 0.3f, by + bevel * 0.3f),
                size = Size(blockSize - bevel * 0.6f, blockSize - bevel * 0.6f),
                cornerRadius = CornerRadius(blockSize * 0.20f, blockSize * 0.20f)
            )

            // Specular Highlight
            val highlight = Path().apply {
                moveTo(bx + bevel * 0.5f, by + bevel * 1.5f)
                lineTo(bx + bevel * 1.5f, by + bevel * 0.5f)
                lineTo(bx + blockSize * 0.65f, by + bevel * 0.5f)
                lineTo(bx + bevel * 0.5f, by + blockSize * 0.65f)
                close()
            }
            drawPath(highlight, Color.White.copy(alpha = 0.45f), style = Fill)

            // Eyes & Smile
            val eyeR = blockSize * 0.12f
            val cx = bx + blockSize / 2
            val cy = by + blockSize * 0.38f

            val leftEye = Offset(cx - eyeR * 1.2f, cy)
            val rightEye = Offset(cx + eyeR * 1.2f, cy)

            drawCircle(Color.White, radius = eyeR, center = leftEye)
            drawCircle(Color.White, radius = eyeR, center = rightEye)
            drawCircle(Color.Black, radius = eyeR * 0.55f, center = leftEye)
            drawCircle(Color.Black, radius = eyeR * 0.55f, center = rightEye)
            drawCircle(Color.White, radius = eyeR * 0.2f, center = leftEye - Offset(eyeR * 0.2f, eyeR * 0.2f))
            drawCircle(Color.White, radius = eyeR * 0.2f, center = rightEye - Offset(eyeR * 0.2f, eyeR * 0.2f))

            // Smile
            val smile = Path().apply {
                moveTo(cx - eyeR * 0.9f, cy + eyeR * 1.3f)
                quadraticBezierTo(cx, cy + eyeR * 2.0f, cx + eyeR * 0.9f, cy + eyeR * 1.3f)
            }
            drawPath(smile, Color.Black.copy(alpha = 0.85f), style = Stroke(width = blockSize * 0.06f))
        }

        // Letter Label on top of block
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = (sizeDp * 0.45f))
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "${letter.uppercaseChar()}",
                    fontWeight = FontWeight.Black,
                    fontSize = (sizeDp.value * 0.28f).sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
}
