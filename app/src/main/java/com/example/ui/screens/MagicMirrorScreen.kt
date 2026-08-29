package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RealisticNumberblockView
import com.example.viewmodel.MainViewModel

@Composable
fun MagicMirrorScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inputNumber by viewModel.mirrorInputNumber.collectAsState()
    val multiplier by viewModel.mirrorMultiplier.collectAsState()
    val outputNumber by viewModel.mirrorOutputNumber.collectAsState()
    val isTransforming by viewModel.isMirrorActive.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "mirror_glow")
    val glowRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_rotation"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81),
                        Color(0xFF0F172A)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("mirror_back_btn")
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "🪞 Magic Mirror",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF8B5CF6)
                ) {
                    Text(
                        text = "Multiplication",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Mirror Stage
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Step into the Magic Mirror!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC084FC)
                    )
                    Text(
                        text = "$inputNumber × $multiplier = $outputNumber",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mirror Visual Display Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Input Character
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Before",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            RealisticNumberblockView(
                                number = inputNumber,
                                blockSize = if (inputNumber > 100) 22.dp else 32.dp,
                                showNumberTag = true,
                                showCatchphrase = false
                            )
                        }

                        // Glowing Magic Mirror Portal
                        Box(
                            modifier = Modifier
                                .size(90.dp, 130.dp)
                                .shadow(20.dp, RoundedCornerShape(45.dp), ambientColor = Color(0xFFC084FC), spotColor = Color(0xFF818CF8))
                                .clip(RoundedCornerShape(45.dp))
                                .background(
                                    Brush.sweepGradient(
                                        listOf(
                                            Color(0xFF8B5CF6),
                                            Color(0xFFEC4899),
                                            Color(0xFF3B82F6),
                                            Color(0xFF8B5CF6)
                                        )
                                    )
                                )
                                .border(3.dp, Color(0xFFFFD700), RoundedCornerShape(45.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(0.88f)
                                    .clip(RoundedCornerShape(40.dp))
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFFE0E7FF),
                                                Color(0xFF818CF8),
                                                Color(0xFF312E81)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Magic",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .rotate(glowRotation)
                                    )
                                    Text(
                                        text = "×$multiplier",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Transformed Output Character
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Magic Result",
                                color = Color(0xFFFFD700),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val scaleAnim by animateFloatAsState(
                                targetValue = if (isTransforming) 1.2f else 1.0f,
                                label = "scale"
                            )
                            Box(modifier = Modifier.scale(scaleAnim)) {
                                RealisticNumberblockView(
                                    number = outputNumber,
                                    blockSize = if (outputNumber > 100) 20.dp else 30.dp,
                                    showNumberTag = true,
                                    showCatchphrase = false
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Step into Mirror Trigger Button
                    Button(
                        onClick = { viewModel.stepIntoMirror() },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .testTag("step_mirror_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C3AED)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Mirror")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✨ STEP THROUGH MIRROR ✨",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Multiplier Selection (×2 Double, ×3 Triple, ×4 Quadruple, ×5 High-Five, ×10 Ten Times!)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Select Magic Multiplier:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2, 3, 4, 5, 10).forEach { mul ->
                            val isSelected = multiplier == mul
                            Button(
                                onClick = { viewModel.setMirrorMultiplier(mul) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF8B5CF6) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "×$mul",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Input Character Picker
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Choose Who Enters the Mirror:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 50, 100, 1000)) { num ->
                            val isSelected = inputNumber == num
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF3B82F6) else Color(0xFF334155),
                                modifier = Modifier.clickable { viewModel.setMirrorInputNumber(num) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (num >= 1000) "${num / 1000}k" else "$num",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
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
