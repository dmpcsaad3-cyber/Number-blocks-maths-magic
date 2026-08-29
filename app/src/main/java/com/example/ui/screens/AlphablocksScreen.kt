package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlphablocksRegistry
import com.example.ui.components.RealisticAlphablockView
import com.example.ui.components.RealisticNumberblockView
import com.example.viewmodel.MainViewModel

@Composable
fun AlphablocksScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val wordLetters by viewModel.alphablocksWord.collectAsState()
    val isBlended by viewModel.isAlphablocksBlended.collectAsState()
    val currentWordString = wordLetters.joinToString("")
    val matchedNumber = AlphablocksRegistry.wordToNumber[currentWordString.uppercase()]
    val alphabet = AlphablocksRegistry.letters

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF1D4ED8),
                        Color(0xFF0F172A)
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("alphablocks_back_button")
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Alphablocks Word Magic 🔤",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEC4899)
                ) {
                    Text(
                        text = "Phonics",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // 2. Active Word Construction Stage
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Build Words & Transform into Numberblocks!",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(Modifier.height(14.dp))

                    if (wordLetters.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tap letter blocks below (e.g. O-N-E, T-E-N, S-I-X)",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(wordLetters) { char ->
                                RealisticAlphablockView(
                                    letter = char,
                                    sizeDp = 64.dp,
                                    showGlow = isBlended || matchedNumber != null,
                                    onClick = { viewModel.removeLetterFromWord(char) }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Matched Magic Transformation
                    AnimatedVisibility(visible = matchedNumber != null) {
                        matchedNumber?.let { number ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "✨ WORD MAGIC UNLOCKED! ✨",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                                Spacer(Modifier.height(8.dp))
                                RealisticNumberblockView(
                                    number = number,
                                    blockSize = 28.dp,
                                    showNumberTag = true,
                                    showCatchphrase = true
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Numberblock $number appeared!",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.blendAlphablocks() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Blend", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Blend & Sound Out!", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.clearAlphablocksWord() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Clear", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 3. Quick Word Suggestions
        item {
            Text(
                text = "Magic Number Words to Spell:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            val suggestions = listOf("ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(suggestions) { word ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable {
                                viewModel.setAlphablocksWordDirect(word)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(word, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 4. Letter Keyboard Grid
        item {
            Text(
                text = "Alphablocks Keyboard (A to Z):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            val alphabetChunks = alphabet.chunked(6)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                alphabetChunks.forEach { rowLetters ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowLetters.forEach { alpha ->
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable { viewModel.addLetterToWord(alpha.letter) }
                            ) {
                                RealisticAlphablockView(
                                    letter = alpha.letter,
                                    sizeDp = 50.dp,
                                    interactive = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
