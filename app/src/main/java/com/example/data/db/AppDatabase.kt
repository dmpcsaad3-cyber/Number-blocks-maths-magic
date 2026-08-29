package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET currentXp = :newXp, level = :newLevel, targetXp = :targetXp, totalScore = totalScore + :scoreGained, problemsSolved = problemsSolved + 1, highestNumberCalculated = max(highestNumberCalculated, :maxCalculated) WHERE id = 1")
    suspend fun addMathProgress(newXp: Int, newLevel: Int, targetXp: Int, scoreGained: Int, maxCalculated: Int)

    @Query("UPDATE user_profile SET magicMirrorsUsed = magicMirrorsUsed + 1 WHERE id = 1")
    suspend fun incrementMagicMirror()

    @Query("UPDATE user_profile SET oblongsDiscovered = oblongsDiscovered + 1 WHERE id = 1")
    suspend fun incrementOblongs()

    @Query("UPDATE user_profile SET wonderQuestsCompleted = wonderQuestsCompleted + 1 WHERE id = 1")
    suspend fun incrementWonderQuests()

    @Query("UPDATE user_profile SET wordsBuilt = wordsBuilt + 1 WHERE id = 1")
    suspend fun incrementWordsBuilt()
}

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard_entries ORDER BY score DESC LIMIT 50")
    fun getTopScores(): Flow<List<LeaderboardEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(entry: LeaderboardEntry)
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<Badge>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialBadges(badges: List<Badge>)

    @Query("UPDATE badges SET unlocked = 1 WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: String)
}

@Database(
    entities = [UserProfile::class, LeaderboardEntry::class, Badge::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "numberblocks_math_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
