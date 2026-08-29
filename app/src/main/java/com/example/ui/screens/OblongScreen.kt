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
import com.example.data.model.NumberblocksRepository
import com.example.viewmodel.MainViewModel

@Composable
fun OblongScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedNumber by viewModel.oblongNumber.collectAsState()
    val currentRows by viewModel.oblongRows.collectAsState()
    val currentCols by viewModel.oblongCols.collectAsState()
    val availableFactorPairs by viewModel.oblongFactorPairs.collectAsState()

    val character = NumberblocksRepository.getCharacter(selectedNumber)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF064E3B),
                        Color(0xFF065F46),
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
                        .testTag("oblong_back_btn")
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "🟩 Super Rectangles & Oblongs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF10B981)
                ) {
                    Text(
                        text = "Arrays",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Oblong Target Selector
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Pick a Super Rectangle Number:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf(4, 6, 8, 9, 10, 12, 16, 18, 20, 24, 36, 100)) { num ->
                            val isSelected = num == selectedNumber
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF059669) else Color(0xFF334155),
                                modifier = Modifier.clickable { viewModel.setOblongNumber(num) }
                            ) {
                                Text(
                                    text = "$num",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Oblong Array Grid Stage
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
                        text = "${character.name} as a $currentRows × $currentCols Rectangle!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Block Array Rendering
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F172A).copy(alpha = 0.6f))
                            .border(2.dp, character.primaryColor, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            for (r in 0 until currentRows) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (c in 0 until currentCols) {
                                        val isHead = (r == 0 && c == currentCols / 2)
                                        Box(
                                            modifier = Modifier
                                                .size(if (currentCols > 8 || currentRows > 8) 18.dp else 28.dp)
                                                .shadow(3.dp, RoundedCornerShape(4.dp))
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(character.secondaryColor, character.primaryColor)
                                                    )
                                                )
                                                .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(4.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isHead) {
                                                // Face
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    repeat(character.eyeCount) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(Color.Black)
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

                    Spacer(modifier = Modifier.height(18.dp))

                    // Numberblock Numberling Tag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "$selectedNumber",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Formula: $currentRows rows of $currentCols = ${currentRows * currentCols}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }

        // Factor Pairs Buttons
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Discovered Rectangles (Factors of $selectedNumber):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableFactorPairs.forEach { pair ->
                            val isSelected = currentRows == pair.first && currentCols == pair.second
                            Button(
                                onClick = { viewModel.setOblongDimensions(pair.first, pair.second) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("oblong_${pair.first}x${pair.second}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF10B981) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "${pair.first}×${pair.second}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
