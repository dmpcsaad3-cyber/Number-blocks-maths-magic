package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.db.LeaderboardEntry
import com.example.viewmodel.MainViewModel

@Composable
fun LeaderboardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val leaderboard by viewModel.leaderboardList.collectAsState()
    val badges by viewModel.badgeList.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Scores, 1: Badges

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF451A03),
                        Color(0xFF78350F),
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
                        .testTag("leaderboard_back_btn")
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "🏆 Hall of Fame",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF59E0B)
                ) {
                    Text(
                        text = "Level ${profile.level}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Profile Showcase Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = profile.playerName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Total Score: ${profile.totalScore} pts",
                                fontSize = 13.sp,
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD97706),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👑", fontSize = 22.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val progress = (profile.currentXp.toFloat() / profile.targetXp.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color.Black.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "XP: ${profile.currentXp} / ${profile.targetXp} to Level ${profile.level + 1}",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }
        }

        // Tab Selector (Leaderboard vs Badges)
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("High Scores", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Badges (${badges.count { it.isUnlocked }}/${badges.size})", fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Tab Content
        if (selectedTab == 0) {
            if (leaderboard.isEmpty()) {
                item {
                    Text(
                        text = "No records yet! Solve math problems to enter the leaderboard!",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                items(leaderboard) { entry ->
                    LeaderboardRow(entry = entry)
                }
            }
        } else {
            items(badges) { badge ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (badge.isUnlocked) Color(0xFF1E293B) else Color(0xFF0F172A).copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (badge.isUnlocked) Color(0xFFF59E0B) else Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (badge.isUnlocked) "🏅" else "🔒",
                                    fontSize = 20.sp
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = badge.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (badge.isUnlocked) Color.White else Color.Gray
                            )
                            Text(
                                text = badge.description,
                                fontSize = 12.sp,
                                color = if (badge.isUnlocked) Color.LightGray else Color.DarkGray
                            )
                        }

                        if (badge.isUnlocked) {
                            Text(
                                text = "UNLOCKED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(entry: LeaderboardEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFD97706),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "⭐",
                            fontSize = 14.sp
                        )
                    }
                }

                Column {
                    Text(
                        text = entry.playerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${entry.gameMode} • Streak: ${entry.streakCount}",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }

            Text(
                text = "${entry.score} pts",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD700)
            )
        }
    }
}
