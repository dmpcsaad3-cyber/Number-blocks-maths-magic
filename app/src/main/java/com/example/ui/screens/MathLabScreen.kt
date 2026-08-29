package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RealisticNumberblockView
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MathOperation

@Composable
fun MathLabScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val problem by viewModel.currentProblem.collectAsState()
    val selectedOp by viewModel.selectedOperation.collectAsState()
    val maxRange by viewModel.maxRange.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val lastCorrect by viewModel.lastAnswerCorrect.collectAsState()
    val customNumber by viewModel.customBuilderNumber.collectAsState()

    var showCustomExplorer by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("back_button")
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "🧪 Math Lab",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFF9800),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🔥", fontSize = 14.sp)
                        Text(
                            text = "$streak",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Operation Selector (+, -, ×, ÷)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MathOperation.values().forEach { op ->
                    val isSelected = op == selectedOp
                    Button(
                        onClick = { viewModel.setOperation(op) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("op_${op.name.lowercase()}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF3B82F6) else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text(
                            text = op.symbol,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color.White else Color.LightGray
                        )
                    }
                }
            }
        }

        // Range Scale Selector (up to 10, 20, 100, 1,000, 10,000!)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Max Number Range: up to $maxRange",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFD700)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listOf(10, 20, 50, 100, 1000, 10000)) { range ->
                            val active = range == maxRange
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (active) Color(0xFF10B981) else Color(0xFF475569),
                                modifier = Modifier.clickable { viewModel.setMaxRange(range) }
                            ) {
                                Text(
                                    text = if (range >= 1000) "${range / 1000}K" else "$range",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Math Problem Stage
        item {
            problem?.let { prob ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Formula Header
                        Text(
                            text = "${prob.num1}  ${prob.operation.symbol}  ${prob.num2}  =  ?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Realistic Visual Block Characters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RealisticNumberblockView(
                                number = prob.num1,
                                blockSize = if (prob.num1 > 100) 20.dp else if (prob.num1 > 20) 24.dp else 34.dp,
                                showNumberTag = true,
                                showCatchphrase = false
                            )

                            Text(
                                text = prob.operation.symbol,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFD700)
                            )

                            RealisticNumberblockView(
                                number = prob.num2,
                                blockSize = if (prob.num2 > 100) 20.dp else if (prob.num2 > 20) 24.dp else 34.dp,
                                showNumberTag = true,
                                showCatchphrase = false
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Answer Feedback Indicator
                        AnimatedVisibility(visible = lastCorrect != null) {
                            if (lastCorrect == true) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                                    modifier = Modifier.border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                                ) {
                                    Text(
                                        text = "🎉 Excellent! +XP & Streak Bonus!",
                                        color = Color(0xFF34D399),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            } else if (lastCorrect == false) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFEF4444).copy(alpha = 0.2f),
                                    modifier = Modifier.border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                                ) {
                                    Text(
                                        text = "⚡ Try again! Count the blocks!",
                                        color = Color(0xFFF87171),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Multiple Choice Option Buttons
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                prob.options.take(2).forEach { opt ->
                                    AnswerOptionButton(
                                        value = opt,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.submitAnswer(opt) }
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                prob.options.drop(2).forEach { opt ->
                                    AnswerOptionButton(
                                        value = opt,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.submitAnswer(opt) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.generateMathProblem() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Next Problem", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Next Math Problem")
                        }
                    }
                }
            }
        }

        // Custom Number Explorer (Up to 10,000)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔍 Numberblock Inspector (up to 10,000)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(onClick = { showCustomExplorer = !showCustomExplorer }) {
                            Icon(
                                imageVector = if (showCustomExplorer) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle Explorer",
                                tint = Color.White
                            )
                        }
                    }

                    if (showCustomExplorer) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 100, 1000, 10000).forEach { num ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (customNumber == num) Color(0xFF3B82F6) else Color(0xFF334155),
                                    modifier = Modifier.clickable { viewModel.setCustomBuilderNumber(num) }
                                ) {
                                    Text(
                                        text = if (num >= 1000) "${num / 1000}k" else "$num",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            RealisticNumberblockView(
                                number = customNumber,
                                blockSize = if (customNumber > 100) 24.dp else 36.dp,
                                showNumberTag = true,
                                showCatchphrase = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerOptionButton(
    value: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(54.dp)
            .testTag("ans_btn_$value"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
    ) {
        Text(
            text = "$value",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
