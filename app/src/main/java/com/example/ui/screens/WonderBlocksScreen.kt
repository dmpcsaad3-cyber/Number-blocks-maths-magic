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

@Composable
fun WonderBlocksScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentWonderNum by viewModel.wonderCurrentNumber.collectAsState()
    val targetWonderNum by viewModel.wonderTargetNumber.collectAsState()
    val commandHistory by viewModel.wonderCommandHistory.collectAsState()
    val isTargetReached by viewModel.wonderTargetReached.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF431407),
                        Color(0xFF7C2D12),
                        Color(0xFF0F172A)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("wonder_back_btn")
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "⚙️ Wonder Blocks",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF97316)
                ) {
                    Text(
                        text = "Build to 10K",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Target Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Target Mission Goal:",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                        Text(
                            text = "Construct $targetWonderNum!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD700)
                        )
                    }

                    // Target Selectors
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(100, 1000, 10000).forEach { tgt ->
                            val isSel = tgt == targetWonderNum
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFFF97316) else Color(0xFF334155),
                                modifier = Modifier.clickable { viewModel.setWonderTarget(tgt) }
                            ) {
                                Text(
                                    text = if (tgt >= 1000) "${tgt / 1000}k" else "$tgt",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stage: Current Numberblock & Success banner
        item {
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
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Current Assembly: $currentWonderNum / $targetWonderNum",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        RealisticNumberblockView(
                            number = currentWonderNum,
                            blockSize = if (currentWonderNum >= 1000) 24.dp else if (currentWonderNum >= 100) 28.dp else 36.dp,
                            showNumberTag = true,
                            showCatchphrase = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AnimatedVisibility(visible = isTargetReached) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.3f),
                            modifier = Modifier.border(2.dp, Color(0xFF10B981), RoundedCornerShape(14.dp))
                        ) {
                            Text(
                                text = "🏆 MISSION COMPLETE! Wonder Master! +500 XP!",
                                color = Color(0xFF34D399),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Command Palette
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💻 Logic Block Commands:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF97316)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 1: Additions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WonderCmdBtn("+1 Step", Color(0xFF3B82F6), Modifier.weight(1f)) { viewModel.applyWonderCommand("+1") }
                        WonderCmdBtn("+10 Tower", Color(0xFF10B981), Modifier.weight(1f)) { viewModel.applyWonderCommand("+10") }
                        WonderCmdBtn("+100 Square", Color(0xFF8B5CF6), Modifier.weight(1f)) { viewModel.applyWonderCommand("+100") }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 2: Multipliers & Mega
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WonderCmdBtn("+1,000 Cube", Color(0xFFEC4899), Modifier.weight(1f)) { viewModel.applyWonderCommand("+1000") }
                        WonderCmdBtn("×2 Double", Color(0xFFF59E0B), Modifier.weight(1f)) { viewModel.applyWonderCommand("×2") }
                        WonderCmdBtn("×10 Mega", Color(0xFFEF4444), Modifier.weight(1f)) { viewModel.applyWonderCommand("×10") }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reset & Undo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.resetWonderNumber() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Wonder Program")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WonderCmdBtn(
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
