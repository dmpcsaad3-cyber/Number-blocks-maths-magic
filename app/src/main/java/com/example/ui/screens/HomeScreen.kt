package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.RealisticNumberblockView
import com.example.viewmodel.MainViewModel

enum class AppScreen {
    HOME,
    MATH_LAB,
    MAGIC_MIRROR,
    OBLONG,
    WONDER_BLOCKS,
    ALPHABLOCKS,
    LEADERBOARD
}

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val streak by viewModel.streak.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & Player Level Status
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_level_card")
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFFEC4899))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = profile.playerName,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Rank: Level ${profile.level} Number Master",
                                    fontSize = 13.sp,
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Streak Bubble
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.Black.copy(alpha = 0.4f),
                                modifier = Modifier.clickable { onNavigate(AppScreen.LEADERBOARD) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🔥", fontSize = 16.sp)
                                    Text(
                                        text = "$streak Streak",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // XP Progress Bar
                        val progressFraction = (profile.currentXp.toFloat() / profile.targetXp.toFloat()).coerceIn(0f, 1f)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "XP Progress (${profile.currentXp}/${profile.targetXp})",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    text = "Total Score: ${profile.totalScore}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFFFE066),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = Color(0xFF10B981),
                                trackColor = Color.Black.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }

        // Hero Banner Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(8.dp, RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_numberblocks_hero),
                    contentDescription = "Numberland Hero",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.75f),
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = "NUMBERBLOCKS MATH WORLD",
                            color = Color(0xFFFFD54F),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Add, Subtract, Multiply & Divide up to 10,000!",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Meet the Numberblocks (Horizontal Carousel)
        item {
            Text(
                text = "🌟 Famous Numberblocks Characters",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 20, 100, 1000, 10000).forEach { num ->
                    Card(
                        modifier = Modifier
                            .width(100.dp)
                            .height(110.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            RealisticNumberblockView(
                                number = num,
                                blockSize = 20.dp,
                                showNumberTag = true,
                                showCatchphrase = false
                            )
                        }
                    }
                }
            }
        }

        // Game Mode Selectors
        item {
            Text(
                text = "🎮 Choose Game Mode",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            GameModeCard(
                title = "Math Lab (+, -, ×, ÷)",
                subtitle = "Addition, Subtraction, Multiplication & Division up to 10,000!",
                icon = Icons.Default.Calculate,
                gradient = listOf(Color(0xFF2563EB), Color(0xFF3B82F6)),
                tag = "math_lab_btn",
                onClick = { onNavigate(AppScreen.MATH_LAB) }
            )
        }

        item {
            GameModeCard(
                title = "Magic Mirror (Multiplication)",
                subtitle = "Step into the magical mirror to double and multiply blocks!",
                icon = Icons.Default.AutoAwesome,
                gradient = listOf(Color(0xFF7C3AED), Color(0xFFA855F7)),
                tag = "magic_mirror_btn",
                onClick = { onNavigate(AppScreen.MAGIC_MIRROR) }
            )
        }

        item {
            GameModeCard(
                title = "Oblong & Super Rectangles",
                subtitle = "Transform Numberblocks into rectangular arrays & learn factors!",
                icon = Icons.Default.GridView,
                gradient = listOf(Color(0xFF059669), Color(0xFF10B981)),
                tag = "oblong_btn",
                onClick = { onNavigate(AppScreen.OBLONG) }
            )
        }

        item {
            GameModeCard(
                title = "Wonder Blocks (Logic to 10,000)",
                subtitle = "Use coding commands (Step, Repeat, Mega) to reach huge numbers!",
                icon = Icons.Default.Extension,
                gradient = listOf(Color(0xFFEA580C), Color(0xFFF97316)),
                tag = "wonder_blocks_btn",
                onClick = { onNavigate(AppScreen.WONDER_BLOCKS) }
            )
        }

        item {
            GameModeCard(
                title = "Alphablocks Word Builder",
                subtitle = "Blend letters into words with cute phonetic sound animations!",
                icon = Icons.Default.TextFields,
                gradient = listOf(Color(0xFFDB2777), Color(0xFFEC4899)),
                tag = "alphablocks_btn",
                onClick = { onNavigate(AppScreen.ALPHABLOCKS) }
            )
        }

        item {
            GameModeCard(
                title = "Leaderboard & Badges",
                subtitle = "View high scores, top streaks, and unlocked hero badges!",
                icon = Icons.Default.EmojiEvents,
                gradient = listOf(Color(0xFFD97706), Color(0xFFF59E0B)),
                tag = "leaderboard_btn",
                onClick = { onNavigate(AppScreen.LEADERBOARD) }
            )
        }
    }
}

@Composable
fun GameModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
            .shadow(8.dp, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradient))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 15.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
