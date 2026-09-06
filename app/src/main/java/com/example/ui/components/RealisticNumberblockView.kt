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
import com.example.model.EyeStyle
import com.example.model.NumberblockData
import com.example.model.NumberblocksRegistry
import kotlin.math.min

@Composable
fun RealisticNumberblockView(
    number: Int,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 140.dp,
    blockSize: Dp? = null,
    showNumberTag: Boolean = false,
    showCatchphrase: Boolean = false,
    customRows: Int? = null,
    customCols: Int? = null,
    showGlow: Boolean = false,
    interactive: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val blockData = remember(number) { NumberblocksRegistry.getBlockForValue(number) }
    val effectiveSize = blockSize?.let { bs ->
        val rows = (customRows ?: blockData.defaultRows).coerceIn(1, 6)
        (bs * (rows + 1)).coerceIn(50.dp, 160.dp)
    } ?: sizeDp

    val infiniteTransition = rememberInfiniteTransition(label = "block_bounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showNumberTag) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(
                    text = "$number",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(effectiveSize)
                .testTag("numberblock_${number}")
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = interactive
                ) {
                    SoundFX.playNumberblockPop(number)
                    onClick?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                // Glow effect if active or large number
                if (showGlow || number >= 1000) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                blockData.primaryColor.copy(alpha = glowAlpha * 0.7f),
                                blockData.secondaryColor.copy(alpha = glowAlpha * 0.3f),
                                Color.Transparent
                            ),
                            center = Offset(canvasW / 2, canvasH / 2 + bounceOffset),
                            radius = canvasW * 0.6f
                        )
                    )
                }

                // Draw Base Layout according to number type
                when {
                    number >= 10000 -> {
                        drawTenThousandMegaBlock(blockData, canvasW, canvasH, bounceOffset)
                    }
                    number >= 1000 -> {
                        drawThousandCubeBlock(number, blockData, canvasW, canvasH, bounceOffset)
                    }
                    number == 100 -> {
                        drawHundredSquareBlock(blockData, canvasW, canvasH, bounceOffset)
                    }
                    else -> {
                        drawStandardGridBlock(number, blockData, customRows, customCols, canvasW, canvasH, bounceOffset)
                    }
                }
            }
        }

        if (showCatchphrase && blockData.catchphrase.isNotBlank()) {
            Spacer(Modifier.height(3.dp))
            Text(
                text = "\"${blockData.catchphrase}\"",
                color = Color(0xFFFFD54F),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun DrawScope.drawStandardGridBlock(
    number: Int,
    data: NumberblockData,
    customRows: Int?,
    customCols: Int?,
    w: Float,
    h: Float,
    bounce: Float
) {
    val rows = (customRows ?: data.defaultRows).coerceAtLeast(1)
    val cols = (customCols ?: data.defaultCols).coerceAtLeast(1)
    val totalCount = number.coerceIn(1, 100)

    val padding = w * 0.12f
    val availableW = w - padding * 2
    val availableH = h - padding * 2
    val unitSize = min(availableW / cols, availableH / rows)

    val gridW = cols * unitSize
    val gridH = rows * unitSize
    val startX = (w - gridW) / 2
    val startY = (h - gridH) / 2 + bounce

    var drawnCount = 0
    for (r in (rows - 1) downTo 0) {
        for (c in 0 until cols) {
            if (drawnCount >= totalCount && number <= 20) break
            val bx = startX + c * unitSize
            val by = startY + r * unitSize

            draw3DCubeBlock(
                x = bx,
                y = by,
                size = unitSize,
                primaryColor = if (number == 7) getRainbowColor(drawnCount) else data.primaryColor,
                secondaryColor = data.secondaryColor,
                accentColor = data.accentColor,
                isTop = (r == 0)
            )
            drawnCount++
        }
    }

    // Draw Character Face on the Head/Center block
    val faceCenterX = startX + (gridW / 2)
    val faceCenterY = startY + (unitSize * 0.5f)
    drawCharacterFeatures(data, number, faceCenterX, faceCenterY, unitSize)
}

private fun DrawScope.draw3DCubeBlock(
    x: Float,
    y: Float,
    size: Float,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color,
    isTop: Boolean
) {
    val cornerRadius = CornerRadius(size * 0.18f, size * 0.18f)
    val bevel = size * 0.14f

    // 1. Dark drop bevel (Bottom & Right)
    drawRoundRect(
        color = accentColor.copy(alpha = 0.9f),
        topLeft = Offset(x, y),
        size = Size(size, size),
        cornerRadius = cornerRadius
    )

    // 2. Main glossy 3D front face
    val faceBrush = Brush.linearGradient(
        colors = listOf(
            secondaryColor,
            primaryColor,
            primaryColor.copy(alpha = 0.85f)
        ),
        start = Offset(x, y),
        end = Offset(x + size, y + size)
    )
    drawRoundRect(
        brush = faceBrush,
        topLeft = Offset(x + bevel * 0.4f, y + bevel * 0.4f),
        size = Size(size - bevel * 0.8f, size - bevel * 0.8f),
        cornerRadius = CornerRadius(size * 0.15f, size * 0.15f)
    )

    // 3. Top-Left glossy specular highlight
    val highlightPath = Path().apply {
        moveTo(x + bevel * 0.6f, y + bevel * 1.8f)
        lineTo(x + bevel * 1.8f, y + bevel * 0.6f)
        lineTo(x + size * 0.65f, y + bevel * 0.6f)
        lineTo(x + bevel * 0.6f, y + size * 0.65f)
        close()
    }
    drawPath(
        path = highlightPath,
        color = Color.White.copy(alpha = 0.45f),
        style = Fill
    )

    // 4. Inner block subtle outline
    drawRoundRect(
        color = Color.White.copy(alpha = 0.25f),
        topLeft = Offset(x + bevel * 0.4f, y + bevel * 0.4f),
        size = Size(size - bevel * 0.8f, size - bevel * 0.8f),
        cornerRadius = CornerRadius(size * 0.15f, size * 0.15f),
        style = Stroke(width = size * 0.04f)
    )
}

private fun DrawScope.drawCharacterFeatures(
    data: NumberblockData,
    number: Int,
    cx: Float,
    cy: Float,
    unitSize: Float
) {
    val eyeR = unitSize * 0.20f

    when (data.eyeStyle) {
        EyeStyle.SINGLE_BIG -> {
            // One: Red, single big center eye
            drawCircle(
                color = Color.White,
                radius = eyeR * 1.4f,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = Color(0xFFD32F2F),
                radius = eyeR * 0.8f,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = Color.Black,
                radius = eyeR * 0.45f,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = Color.White,
                radius = eyeR * 0.22f,
                center = Offset(cx - eyeR * 0.25f, cy - eyeR * 0.25f)
            )

            // Smile
            val smilePath = Path().apply {
                moveTo(cx - eyeR * 0.8f, cy + eyeR * 1.1f)
                quadraticBezierTo(cx, cy + eyeR * 1.7f, cx + eyeR * 0.8f, cy + eyeR * 1.1f)
            }
            drawPath(smilePath, Color(0xFF880E4F), style = Stroke(width = unitSize * 0.08f))
        }

        EyeStyle.PURPLE_GLASSES -> {
            // Two: Orange with purple glasses
            val leftEye = Offset(cx - eyeR * 1.1f, cy)
            val rightEye = Offset(cx + eyeR * 1.1f, cy)

            // Glasses Frames
            drawCircle(Color(0xFF7B1FA2), radius = eyeR * 1.25f, center = leftEye)
            drawCircle(Color(0xFF7B1FA2), radius = eyeR * 1.25f, center = rightEye)
            drawLine(Color(0xFF7B1FA2), leftEye, rightEye, strokeWidth = unitSize * 0.08f)

            // Eyes inside glasses
            drawCircle(Color.White, radius = eyeR * 0.95f, center = leftEye)
            drawCircle(Color.White, radius = eyeR * 0.95f, center = rightEye)
            drawCircle(Color.Black, radius = eyeR * 0.45f, center = leftEye)
            drawCircle(Color.Black, radius = eyeR * 0.45f, center = rightEye)
            drawCircle(Color.White, radius = eyeR * 0.18f, center = leftEye - Offset(eyeR * 0.15f, eyeR * 0.15f))
            drawCircle(Color.White, radius = eyeR * 0.18f, center = rightEye - Offset(eyeR * 0.15f, eyeR * 0.15f))

            // Smile
            drawArc(
                color = Color(0xFFE65100),
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(cx - eyeR * 0.9f, cy + eyeR * 0.4f),
                size = Size(eyeR * 1.8f, eyeR * 1.1f),
                style = Stroke(width = unitSize * 0.07f)
            )
        }

        EyeStyle.YELLOW_CLOWN -> {
            // Three: Yellow performer with 3 red entertainer buttons
            drawTwoEyes(cx, cy, eyeR, unitSize)
            drawCircle(Color(0xFFD32F2F), radius = eyeR * 0.45f, center = Offset(cx, cy + unitSize * 0.7f))
            drawCircle(Color(0xFFD32F2F), radius = eyeR * 0.45f, center = Offset(cx, cy + unitSize * 1.5f))
        }

        EyeStyle.SQUARE_GLASSES -> {
            // Four / Sixteen: Square glasses
            val leftEye = Offset(cx - eyeR * 1.1f, cy)
            val rightEye = Offset(cx + eyeR * 1.1f, cy)
            val frameSize = eyeR * 2.2f

            drawRoundRect(Color(0xFF1B5E20), topLeft = leftEye - Offset(frameSize / 2, frameSize / 2), size = Size(frameSize, frameSize), cornerRadius = CornerRadius(eyeR * 0.3f, eyeR * 0.3f))
            drawRoundRect(Color(0xFF1B5E20), topLeft = rightEye - Offset(frameSize / 2, frameSize / 2), size = Size(frameSize, frameSize), cornerRadius = CornerRadius(eyeR * 0.3f, eyeR * 0.3f))
            drawRoundRect(Color.White, topLeft = leftEye - Offset(frameSize * 0.4f, frameSize * 0.4f), size = Size(frameSize * 0.8f, frameSize * 0.8f), cornerRadius = CornerRadius(eyeR * 0.2f, eyeR * 0.2f))
            drawRoundRect(Color.White, topLeft = rightEye - Offset(frameSize * 0.4f, frameSize * 0.4f), size = Size(frameSize * 0.8f, frameSize * 0.8f), cornerRadius = CornerRadius(eyeR * 0.2f, eyeR * 0.2f))
            drawCircle(Color.Black, radius = eyeR * 0.4f, center = leftEye)
            drawCircle(Color.Black, radius = eyeR * 0.4f, center = rightEye)
        }

        EyeStyle.BLUE_STAR -> {
            // Five: Blue Star eye/face
            drawStar(cx, cy - eyeR * 0.2f, eyeR * 2.0f, Color(0xFF0D47A1))
            drawTwoEyes(cx, cy, eyeR * 0.9f, unitSize)
        }

        EyeStyle.DICE_SPOTS -> {
            // Six: Dice spots
            drawTwoEyes(cx, cy, eyeR, unitSize)
            val dotR = eyeR * 0.25f
            val dotOffsets = listOf(
                Offset(-unitSize * 0.25f, unitSize * 0.6f), Offset(unitSize * 0.25f, unitSize * 0.6f),
                Offset(-unitSize * 0.25f, unitSize * 1.1f), Offset(unitSize * 0.25f, unitSize * 1.1f),
                Offset(-unitSize * 0.25f, unitSize * 1.6f), Offset(unitSize * 0.25f, unitSize * 1.6f)
            )
            for (off in dotOffsets) {
                drawCircle(Color(0xFF311B92), radius = dotR, center = Offset(cx + off.x, cy + off.y))
            }
        }

        EyeStyle.OCTO_MASK -> {
            // Eight: Octoblock superhero mask
            val maskPath = Path().apply {
                moveTo(cx - unitSize * 0.48f, cy - eyeR * 0.8f)
                lineTo(cx + unitSize * 0.48f, cy - eyeR * 0.8f)
                lineTo(cx + unitSize * 0.40f, cy + eyeR * 0.9f)
                lineTo(cx, cy + eyeR * 0.4f)
                lineTo(cx - unitSize * 0.40f, cy + eyeR * 0.9f)
                close()
            }
            drawPath(maskPath, Color(0xFF880E4F))
            drawTwoEyes(cx, cy, eyeR * 0.85f, unitSize)
        }

        else -> {
            drawTwoEyes(cx, cy, eyeR, unitSize)
        }
    }
}

private fun DrawScope.drawTwoEyes(cx: Float, cy: Float, eyeR: Float, unitSize: Float) {
    val leftEye = Offset(cx - eyeR * 1.05f, cy)
    val rightEye = Offset(cx + eyeR * 1.05f, cy)

    drawCircle(Color.White, radius = eyeR, center = leftEye)
    drawCircle(Color.White, radius = eyeR, center = rightEye)
    drawCircle(Color.Black, radius = eyeR * 0.5f, center = leftEye)
    drawCircle(Color.Black, radius = eyeR * 0.5f, center = rightEye)
    drawCircle(Color.White, radius = eyeR * 0.2f, center = leftEye - Offset(eyeR * 0.18f, eyeR * 0.18f))
    drawCircle(Color.White, radius = eyeR * 0.2f, center = rightEye - Offset(eyeR * 0.18f, eyeR * 0.18f))

    val smilePath = Path().apply {
        moveTo(cx - eyeR * 0.9f, cy + eyeR * 1.2f)
        quadraticBezierTo(cx, cy + eyeR * 1.8f, cx + eyeR * 0.9f, cy + eyeR * 1.2f)
    }
    drawPath(smilePath, Color.Black.copy(alpha = 0.8f), style = Stroke(width = unitSize * 0.08f))
}

private fun DrawScope.drawStar(cx: Float, cy: Float, radius: Float, color: Color) {
    val path = Path()
    val innerRadius = radius * 0.45f
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = (i * 36 - 90) * (Math.PI / 180.0)
        val x = cx + (r * Math.cos(angle)).toFloat()
        val y = cy + (r * Math.sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color, style = Fill)
}

private fun DrawScope.drawHundredSquareBlock(
    data: NumberblockData,
    w: Float,
    h: Float,
    bounce: Float
) {
    val sizePx = min(w, h) * 0.82f
    val startX = (w - sizePx) / 2
    val startY = (h - sizePx) / 2 + bounce
    val subSize = sizePx / 10f

    for (r in 0 until 10) {
        for (c in 0 until 10) {
            val isSalmon = (r + c) % 2 == 0
            val color = if (isSalmon) Color(0xFFFF7043) else Color(0xFFC2185B)
            drawRect(
                color = color,
                topLeft = Offset(startX + c * subSize, startY + r * subSize),
                size = Size(subSize, subSize)
            )
        }
    }
    drawRect(
        color = Color(0xFF880E4F),
        topLeft = Offset(startX, startY),
        size = Size(sizePx, sizePx),
        style = Stroke(width = sizePx * 0.035f)
    )

    val cx = startX + sizePx / 2
    val cy = startY + sizePx * 0.35f
    val eyeW = sizePx * 0.28f

    drawRoundRect(Color.White, topLeft = Offset(cx - eyeW / 2, cy - eyeW / 2), size = Size(eyeW, eyeW), cornerRadius = CornerRadius(8f, 8f))
    drawRoundRect(Color(0xFFD32F2F), topLeft = Offset(cx - eyeW * 0.35f, cy - eyeW * 0.35f), size = Size(eyeW * 0.7f, eyeW * 0.7f), cornerRadius = CornerRadius(6f, 6f))
    drawCircle(Color.Black, radius = eyeW * 0.18f, center = Offset(cx, cy))
    drawCircle(Color.White, radius = eyeW * 0.08f, center = Offset(cx - eyeW * 0.07f, cy - eyeW * 0.07f))
}

private fun DrawScope.drawThousandCubeBlock(
    number: Int,
    data: NumberblockData,
    w: Float,
    h: Float,
    bounce: Float
) {
    val cx = w / 2
    val cy = h / 2 + bounce
    val cubeSize = min(w, h) * 0.42f

    val topPath = Path().apply {
        moveTo(cx, cy - cubeSize * 0.9f)
        lineTo(cx + cubeSize * 0.85f, cy - cubeSize * 0.45f)
        lineTo(cx, cy)
        lineTo(cx - cubeSize * 0.85f, cy - cubeSize * 0.45f)
        close()
    }
    val leftPath = Path().apply {
        moveTo(cx - cubeSize * 0.85f, cy - cubeSize * 0.45f)
        lineTo(cx, cy)
        lineTo(cx, cy + cubeSize * 0.85f)
        lineTo(cx - cubeSize * 0.85f, cy + cubeSize * 0.4f)
        close()
    }
    val rightPath = Path().apply {
        moveTo(cx + cubeSize * 0.85f, cy - cubeSize * 0.45f)
        lineTo(cx, cy)
        lineTo(cx, cy + cubeSize * 0.85f)
        lineTo(cx + cubeSize * 0.85f, cy + cubeSize * 0.4f)
        close()
    }

    drawPath(topPath, Color(0xFFFF5252))
    drawPath(leftPath, Color(0xFFD32F2F))
    drawPath(rightPath, Color(0xFFB71C1C))

    drawPath(topPath, Color.White.copy(alpha = 0.5f), style = Stroke(width = 2f))
    drawPath(leftPath, Color.White.copy(alpha = 0.4f), style = Stroke(width = 2f))
    drawPath(rightPath, Color.White.copy(alpha = 0.4f), style = Stroke(width = 2f))

    drawCircle(Color.White, radius = cubeSize * 0.28f, center = Offset(cx, cy - cubeSize * 0.4f))
    drawCircle(Color(0xFFD32F2F), radius = cubeSize * 0.16f, center = Offset(cx, cy - cubeSize * 0.4f))
    drawCircle(Color.Black, radius = cubeSize * 0.08f, center = Offset(cx, cy - cubeSize * 0.4f))
}

private fun DrawScope.drawTenThousandMegaBlock(
    data: NumberblockData,
    w: Float,
    h: Float,
    bounce: Float
) {
    val cx = w / 2
    val cy = h / 2 + bounce
    val baseR = min(w, h) * 0.40f

    drawStar(cx, cy, baseR * 1.25f, Color(0xFFFFD54F).copy(alpha = 0.75f))

    val blockW = baseR * 1.5f
    val blockH = baseR * 1.4f
    drawRoundRect(
        color = Color(0xFFB71C1C),
        topLeft = Offset(cx - blockW / 2, cy - blockH / 2),
        size = Size(blockW, blockH),
        cornerRadius = CornerRadius(24f, 24f)
    )
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(cx - blockW * 0.46f, cy - blockH * 0.46f),
        size = Size(blockW * 0.92f, blockH * 0.92f),
        cornerRadius = CornerRadius(20f, 20f)
    )

    val eyeOffset = blockW * 0.22f
    drawCircle(Color(0xFFFFB300), radius = blockW * 0.12f, center = Offset(cx - eyeOffset, cy - blockH * 0.12f))
    drawCircle(Color(0xFFFFB300), radius = blockW * 0.12f, center = Offset(cx + eyeOffset, cy - blockH * 0.12f))
    drawCircle(Color.Black, radius = blockW * 0.05f, center = Offset(cx - eyeOffset, cy - blockH * 0.12f))
    drawCircle(Color.Black, radius = blockW * 0.05f, center = Offset(cx + eyeOffset, cy - blockH * 0.12f))

    val smilePath = Path().apply {
        moveTo(cx - blockW * 0.25f, cy + blockH * 0.18f)
        quadraticBezierTo(cx, cy + blockH * 0.35f, cx + blockW * 0.25f, cy + blockH * 0.18f)
    }
    drawPath(smilePath, Color(0xFFB71C1C), style = Stroke(width = blockW * 0.04f))
}

private fun getRainbowColor(index: Int): Color {
    val rainbow = listOf(
        Color(0xFFE53935), Color(0xFFFF9800), Color(0xFFFFEB3B),
        Color(0xFF4CAF50), Color(0xFF03A9F4), Color(0xFF3F51B5),
        Color(0xFF8E24AA)
    )
    return rainbow[index % rainbow.size]
}
