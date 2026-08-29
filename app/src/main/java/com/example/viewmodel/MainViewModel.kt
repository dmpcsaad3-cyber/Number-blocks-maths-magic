package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.sound.SoundFxPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class MathOperation(val symbol: String, val displayName: String) {
    ADDITION("+", "Addition"),
    SUBTRACTION("-", "Subtraction"),
    MULTIPLICATION("×", "Multiplication"),
    DIVISION("÷", "Division")
}

data class MathProblem(
    val num1: Int,
    val num2: Int,
    val operation: MathOperation,
    val answer: Int,
    val options: List<Int>
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val userDao = db.userDao()
    private val leaderboardDao = db.leaderboardDao()
    private val badgeDao = db.badgeDao()

    val userProfile: StateFlow<UserProfile> = userDao.getUserProfile()
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val leaderboardList: StateFlow<List<LeaderboardEntry>> = leaderboardDao.getTopScores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topScores: StateFlow<List<LeaderboardEntry>> = leaderboardList

    val badgeList: StateFlow<List<Badge>> = badgeDao.getAllBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badges: StateFlow<List<Badge>> = badgeList

    // -------------------------------------------------------------
    // Math Lab State (supports up to 10,000)
    // -------------------------------------------------------------
    private val _currentProblem = MutableStateFlow<MathProblem?>(null)
    val currentProblem: StateFlow<MathProblem?> = _currentProblem.asStateFlow()

    private val _selectedOperation = MutableStateFlow(MathOperation.ADDITION)
    val selectedOperation: StateFlow<MathOperation> = _selectedOperation.asStateFlow()

    private val _maxRange = MutableStateFlow(20) // Can scale to 10, 20, 50, 100, 1000, 10000
    val maxRange: StateFlow<Int> = _maxRange.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _lastAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val lastAnswerCorrect: StateFlow<Boolean?> = _lastAnswerCorrect.asStateFlow()

    // Inspector / Sandbox
    private val _customBuilderNumber = MutableStateFlow(10)
    val customBuilderNumber: StateFlow<Int> = _customBuilderNumber.asStateFlow()

    // -------------------------------------------------------------
    // Magic Mirror State
    // -------------------------------------------------------------
    private val _mirrorInputNumber = MutableStateFlow(4)
    val mirrorInputNumber: StateFlow<Int> = _mirrorInputNumber.asStateFlow()

    private val _mirrorMultiplier = MutableStateFlow(2)
    val mirrorMultiplier: StateFlow<Int> = _mirrorMultiplier.asStateFlow()

    val mirrorOutputNumber: StateFlow<Int> = combine(_mirrorInputNumber, _mirrorMultiplier) { num, mul ->
        (num * mul).coerceAtMost(10000)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8)

    private val _isMirrorActive = MutableStateFlow(false)
    val isMirrorActive: StateFlow<Boolean> = _isMirrorActive.asStateFlow()

    // -------------------------------------------------------------
    // Oblong & Super Rectangles State
    // -------------------------------------------------------------
    private val _oblongNumber = MutableStateFlow(12)
    val oblongNumber: StateFlow<Int> = _oblongNumber.asStateFlow()
    val oblongTarget: StateFlow<Int> = _oblongNumber.asStateFlow()

    private val _oblongRows = MutableStateFlow(3)
    val oblongRows: StateFlow<Int> = _oblongRows.asStateFlow()

    private val _oblongCols = MutableStateFlow(4)
    val oblongCols: StateFlow<Int> = _oblongCols.asStateFlow()

    val oblongFactorPairs: StateFlow<List<Pair<Int, Int>>> = _oblongNumber.map { num ->
        val pairs = mutableListOf<Pair<Int, Int>>()
        for (i in 1..num) {
            if (num % i == 0 && i <= 20 && (num / i) <= 20) {
                pairs.add(Pair(i, num / i))
            }
        }
        pairs
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(Pair(1, 12), Pair(2, 6), Pair(3, 4), Pair(4, 3), Pair(6, 2), Pair(12, 1)))

    // -------------------------------------------------------------
    // Wonder Blocks (Logic Code Quest to 10,000)
    // -------------------------------------------------------------
    private val _wonderTargetNumber = MutableStateFlow(100)
    val wonderTargetNumber: StateFlow<Int> = _wonderTargetNumber.asStateFlow()
    val wonderTargetStars: StateFlow<Int> = _wonderTargetNumber.asStateFlow()

    private val _wonderCurrentNumber = MutableStateFlow(0)
    val wonderCurrentNumber: StateFlow<Int> = _wonderCurrentNumber.asStateFlow()
    val wonderCollectedStars: StateFlow<Int> = _wonderCurrentNumber.asStateFlow()

    private val _wonderCommandHistory = MutableStateFlow<List<String>>(emptyList())
    val wonderCommandHistory: StateFlow<List<String>> = _wonderCommandHistory.asStateFlow()
    val wonderCommands: StateFlow<List<String>> = _wonderCommandHistory.asStateFlow()

    val wonderTargetReached: StateFlow<Boolean> = combine(_wonderCurrentNumber, _wonderTargetNumber) { cur, tgt ->
        cur >= tgt
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // -------------------------------------------------------------
    // Alphablocks Word Blending State
    // -------------------------------------------------------------
    private val _alphablocksWord = MutableStateFlow<List<Char>>(listOf('C', 'A', 'T'))
    val alphablocksWord: StateFlow<List<Char>> = _alphablocksWord.asStateFlow()
    val selectedLetters: StateFlow<List<Char>> = _alphablocksWord.asStateFlow()

    private val _isAlphablocksBlended = MutableStateFlow(false)
    val isAlphablocksBlended: StateFlow<Boolean> = _isAlphablocksBlended.asStateFlow()

    private val _blendedWordResult = MutableStateFlow("CAT")
    val blendedWordResult: StateFlow<String> = _blendedWordResult.asStateFlow()
    val activeWord: StateFlow<String> = _blendedWordResult.asStateFlow()

    init {
        initInitialData()
        generateMathProblem()
    }

    private fun initInitialData() {
        viewModelScope.launch {
            userDao.insertOrUpdateProfile(
                UserProfile(
                    id = 1,
                    playerName = "Numberblock Hero",
                    level = 1,
                    currentXp = 0,
                    targetXp = 100
                )
            )

            val initialBadges = listOf(
                Badge("b1", "Single Block Spark", "Solved your first Math Problem", "🔴", true, 1, 1),
                Badge("b2", "Magic Mirror Master", "Used the Magic Mirror to multiply numbers", "🪞", false, 0, 5),
                Badge("b3", "Super Rectangle Scout", "Discovered Oblong rectangular arrays", "📐", false, 0, 5),
                Badge("b4", "Century Champion", "Calculated numbers past 100", "💯", false, 0, 1),
                Badge("b5", "Mega Titan 10K", "Reached 10,000 with Wonder Blocks", "👑", false, 0, 1),
                Badge("b6", "Alpha Blending Star", "Built phonetic words with Alphablocks", "🔤", false, 0, 5)
            )
            badgeDao.insertInitialBadges(initialBadges)

            val initialLeaderboards = listOf(
                LeaderboardEntry(playerName = "Octoblock Hero", score = 2500, mode = "Math Lab", streak = 15),
                LeaderboardEntry(playerName = "Magic Seven", score = 1850, mode = "Magic Mirror", streak = 12),
                LeaderboardEntry(playerName = "Square Super Nine", score = 1420, mode = "Oblong", streak = 9),
                LeaderboardEntry(playerName = "Alpha Champion", score = 1100, mode = "Alphablocks", streak = 7)
            )
            for (entry in initialLeaderboards) {
                leaderboardDao.insertScore(entry)
            }
        }
    }

    // -------------------------------------------------------------
    // Math Lab Operations
    // -------------------------------------------------------------
    fun setOperation(op: MathOperation) {
        _selectedOperation.value = op
        generateMathProblem()
    }

    fun setMaxRange(range: Int) {
        _maxRange.value = range
        generateMathProblem()
    }

    fun setCustomBuilderNumber(number: Int) {
        _customBuilderNumber.value = number.coerceIn(1, 10000)
    }

    fun generateMathProblem() {
        val op = _selectedOperation.value
        val max = _maxRange.value

        var n1: Int
        var n2: Int
        var ans: Int

        when (op) {
            MathOperation.ADDITION -> {
                n1 = Random.nextInt(1, (max * 0.7).toInt().coerceAtLeast(2))
                n2 = Random.nextInt(1, (max - n1).coerceAtLeast(2))
                ans = n1 + n2
            }
            MathOperation.SUBTRACTION -> {
                n1 = Random.nextInt(2, max.coerceAtLeast(3))
                n2 = Random.nextInt(1, n1)
                ans = n1 - n2
            }
            MathOperation.MULTIPLICATION -> {
                val factorLimit = when {
                    max >= 10000 -> 100
                    max >= 1000 -> 50
                    max >= 100 -> 12
                    else -> 5
                }
                n1 = Random.nextInt(1, factorLimit)
                n2 = Random.nextInt(1, factorLimit)
                ans = n1 * n2
            }
            MathOperation.DIVISION -> {
                val divisorLimit = if (max > 100) 12 else 6
                n2 = Random.nextInt(1, divisorLimit)
                ans = Random.nextInt(1, (max / n2).coerceIn(1, 20))
                n1 = n2 * ans
            }
        }

        val opts = mutableSetOf(ans)
        while (opts.size < 4) {
            val delta = Random.nextInt(-5, 6)
            val fake = (ans + delta).coerceAtLeast(0)
            if (fake != ans) {
                opts.add(fake)
            }
        }

        _currentProblem.value = MathProblem(
            num1 = n1,
            num2 = n2,
            operation = op,
            answer = ans,
            options = opts.shuffled()
        )
        _lastAnswerCorrect.value = null
    }

    fun submitAnswer(chosen: Int) {
        val problem = _currentProblem.value ?: return
        if (chosen == problem.answer) {
            _lastAnswerCorrect.value = true
            _streak.value += 1
            SoundFxPlayer.playCorrect()

            val xpGained = 20 + (_streak.value * 5)
            val scoreGained = 50 * _streak.value

            grantXpAndScore(xpGained, scoreGained, problem.answer)

            if (_streak.value % 5 == 0) {
                SoundFxPlayer.playLevelUp()
            }
        } else {
            _lastAnswerCorrect.value = false
            _streak.value = 0
            SoundFxPlayer.playPop()
        }
    }

    private fun grantXpAndScore(xpGained: Int, scoreGained: Int, maxCalculated: Int) {
        viewModelScope.launch {
            val current = userProfile.value
            var newXp = current.currentXp + xpGained
            var newLevel = current.level
            var targetXp = current.targetXp

            while (newXp >= targetXp) {
                newXp -= targetXp
                newLevel += 1
                targetXp = (targetXp * 1.35).toInt()
                SoundFxPlayer.playLevelUp()
            }

            userDao.addMathProgress(newXp, newLevel, targetXp, scoreGained, maxCalculated)

            if (_streak.value >= 3) {
                leaderboardDao.insertScore(
                    LeaderboardEntry(
                        playerName = current.playerName,
                        score = current.totalScore + scoreGained,
                        mode = "Math Lab (${_selectedOperation.value.displayName})",
                        streak = _streak.value
                    )
                )
            }

            badgeDao.unlockBadge("b1")
            if (maxCalculated >= 100) {
                badgeDao.unlockBadge("b4")
            }
            if (maxCalculated >= 10000) {
                badgeDao.unlockBadge("b5")
            }
        }
    }

    // -------------------------------------------------------------
    // Magic Mirror Methods
    // -------------------------------------------------------------
    fun setMirrorInputNumber(num: Int) {
        _mirrorInputNumber.value = num.coerceIn(1, 1000)
    }

    fun setMirrorInput(number: Int) = setMirrorInputNumber(number)

    fun setMirrorMultiplier(mul: Int) {
        _mirrorMultiplier.value = mul.coerceIn(2, 10)
    }

    fun stepIntoMirror() {
        viewModelScope.launch {
            _isMirrorActive.value = true
            SoundFxPlayer.playMagicGlow()
            delay(500)
            _isMirrorActive.value = false

            userDao.incrementMagicMirror()
            val result = _mirrorInputNumber.value * _mirrorMultiplier.value
            grantXpAndScore(30, 80, result)
            badgeDao.unlockBadge("b2")
        }
    }

    fun activateMagicMirror() = stepIntoMirror()

    // -------------------------------------------------------------
    // Oblong Methods
    // -------------------------------------------------------------
    fun setOblongNumber(num: Int) {
        _oblongNumber.value = num
        val pairs = oblongFactorPairs.value
        if (pairs.isNotEmpty()) {
            val mid = pairs[pairs.size / 2]
            _oblongRows.value = mid.first
            _oblongCols.value = mid.second
        }
    }

    fun setOblongTarget(num: Int) = setOblongNumber(num)

    fun setOblongDimensions(rows: Int, cols: Int) {
        _oblongRows.value = rows
        _oblongCols.value = cols
        SoundFxPlayer.playSnap()
        if (rows * cols == _oblongNumber.value) {
            SoundFxPlayer.playCorrect()
            viewModelScope.launch {
                userDao.incrementOblongs()
                grantXpAndScore(25, 75, _oblongNumber.value)
                badgeDao.unlockBadge("b3")
            }
        }
    }

    fun updateOblongGrid(rows: Int, cols: Int) = setOblongDimensions(rows, cols)

    // -------------------------------------------------------------
    // Wonder Blocks Methods
    // -------------------------------------------------------------
    fun setWonderTarget(target: Int) {
        _wonderTargetNumber.value = target.coerceIn(10, 10000)
        resetWonderNumber()
    }

    fun applyWonderCommand(cmd: String) {
        val cur = _wonderCurrentNumber.value
        val next = when (cmd) {
            "+1", "STEP +1" -> cur + 1
            "+10", "BLOCK +10" -> cur + 10
            "+100", "SUPER +100" -> cur + 100
            "+1000", "MEGA +1,000" -> cur + 1000
            "×2", "REPEAT ×2" -> cur * 2
            "×10", "REPEAT ×10" -> cur * 10
            else -> cur
        }.coerceAtMost(10000)

        _wonderCurrentNumber.value = next
        _wonderCommandHistory.value = _wonderCommandHistory.value + cmd
        SoundFxPlayer.playPop()

        if (next >= _wonderTargetNumber.value) {
            SoundFxPlayer.playLevelUp()
            viewModelScope.launch {
                userDao.incrementWonderQuests()
                grantXpAndScore(50, 150, next)
                if (next >= 10000) {
                    badgeDao.unlockBadge("b5")
                }
            }
        }
    }

    fun addWonderCommand(cmd: String) = applyWonderCommand(cmd)

    fun resetWonderNumber() {
        _wonderCurrentNumber.value = 0
        _wonderCommandHistory.value = emptyList()
        SoundFxPlayer.playSnap()
    }

    fun clearWonderCommands() = resetWonderNumber()

    // -------------------------------------------------------------
    // Alphablocks Methods
    // -------------------------------------------------------------
    fun addLetterToWord(char: Char) {
        SoundFxPlayer.playPhonics(char)
        _alphablocksWord.value = _alphablocksWord.value + char
        _isAlphablocksBlended.value = false
    }

    fun selectLetter(char: Char) = addLetterToWord(char)

    fun removeLetterFromWord(char: Char) {
        val list = _alphablocksWord.value.toMutableList()
        list.remove(char)
        _alphablocksWord.value = list
        _isAlphablocksBlended.value = false
        SoundFxPlayer.playPop()
    }

    fun removeLastLetter() {
        if (_alphablocksWord.value.isNotEmpty()) {
            _alphablocksWord.value = _alphablocksWord.value.dropLast(1)
            _isAlphablocksBlended.value = false
            SoundFxPlayer.playPop()
        }
    }

    fun clearAlphablocksWord() {
        _alphablocksWord.value = emptyList()
        _isAlphablocksBlended.value = false
    }

    fun setAlphablocksWordDirect(word: String) {
        _alphablocksWord.value = word.uppercase().toList()
        _isAlphablocksBlended.value = false
        blendAlphablocks()
    }

    fun blendAlphablocks() {
        val letters = _alphablocksWord.value
        if (letters.isNotEmpty()) {
            val word = letters.joinToString("")
            _blendedWordResult.value = word
            _isAlphablocksBlended.value = true
            SoundFxPlayer.playCorrect()

            viewModelScope.launch {
                userDao.incrementWordsBuilt()
                grantXpAndScore(25, 60, word.length * 10)
                badgeDao.unlockBadge("b6")
            }
        }
    }

    fun testWordBlend() = blendAlphablocks()
}
