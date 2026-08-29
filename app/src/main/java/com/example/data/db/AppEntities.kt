package com.example.data.db

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "Number Cadet",
    val level: Int = 1,
    val currentXp: Int = 0,
    val targetXp: Int = 100,
    val totalScore: Int = 0,
    val problemsSolved: Int = 0,
    val magicMirrorsUsed: Int = 0,
    val oblongsDiscovered: Int = 0,
    val wonderQuestsCompleted: Int = 0,
    val wordsBuilt: Int = 0,
    val highestNumberCalculated: Int = 10
)

@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playerName: String,
    val score: Int,
    val mode: String,
    val streak: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val avatarNumber: Int = 1
) {
    @get:Ignore
    val gameMode: String get() = mode

    @get:Ignore
    val streakCount: Int get() = streak
}

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val unlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1
) {
    @get:Ignore
    val isUnlocked: Boolean get() = unlocked
}
