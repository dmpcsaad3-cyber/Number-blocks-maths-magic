package com.example.model

import androidx.compose.ui.graphics.Color

enum class EyeStyle {
    SINGLE_BIG,       // One (Red, 1 eye)
    PURPLE_GLASSES,   // Two (Orange, glasses)
    YELLOW_CLOWN,     // Three (Yellow, performer buttons)
    SQUARE_GLASSES,   // Four (Green, square)
    BLUE_STAR,        // Five (Blue, star face)
    DICE_SPOTS,       // Six (Indigo, dice spots)
    RAINBOW_SEVEN,    // Seven (Rainbow colors)
    OCTO_MASK,        // Eight (Magenta superhero mask)
    SQUARE_NINE,      // Nine (Grey square)
    TWO_EYES,         // Generic friendly 2 eyes
    WHITE_TEN,        // Ten (White with red outline)
    MEGA_STAR         // 1000, 10000 Mega Titan
}

data class NumberblockData(
    val value: Int,
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val eyeStyle: EyeStyle,
    val defaultRows: Int,
    val defaultCols: Int,
    val catchphrase: String,
    val isSquare: Boolean = false,
    val isSuperRectangle: Boolean = false
)

object NumberblocksRegistry {
    private val canonicalMap = mapOf(
        1 to NumberblockData(
            value = 1,
            name = "One",
            primaryColor = Color(0xFFE53935),
            secondaryColor = Color(0xFFFF8A80),
            accentColor = Color(0xFFB71C1C),
            eyeStyle = EyeStyle.SINGLE_BIG,
            defaultRows = 1,
            defaultCols = 1,
            catchphrase = "One! Just me!"
        ),
        2 to NumberblockData(
            value = 2,
            name = "Two",
            primaryColor = Color(0xFFFF9800),
            secondaryColor = Color(0xFFFFCC80),
            accentColor = Color(0xFFE65100),
            eyeStyle = EyeStyle.PURPLE_GLASSES,
            defaultRows = 2,
            defaultCols = 1,
            catchphrase = "Two! Shoes for two!"
        ),
        3 to NumberblockData(
            value = 3,
            name = "Three",
            primaryColor = Color(0xFFFFEB3B),
            secondaryColor = Color(0xFFFFF9C4),
            accentColor = Color(0xFFFBC02D),
            eyeStyle = EyeStyle.YELLOW_CLOWN,
            defaultRows = 3,
            defaultCols = 1,
            catchphrase = "Three! Look at me!"
        ),
        4 to NumberblockData(
            value = 4,
            name = "Four",
            primaryColor = Color(0xFF4CAF50),
            secondaryColor = Color(0xFFA5D6A7),
            accentColor = Color(0xFF1B5E20),
            eyeStyle = EyeStyle.SQUARE_GLASSES,
            defaultRows = 2,
            defaultCols = 2,
            catchphrase = "Four! I'm a square!",
            isSquare = true
        ),
        5 to NumberblockData(
            value = 5,
            name = "Five",
            primaryColor = Color(0xFF03A9F4),
            secondaryColor = Color(0xFF81D4FA),
            accentColor = Color(0xFF01579B),
            eyeStyle = EyeStyle.BLUE_STAR,
            defaultRows = 5,
            defaultCols = 1,
            catchphrase = "Five! High Five!"
        ),
        6 to NumberblockData(
            value = 6,
            name = "Six",
            primaryColor = Color(0xFF5E35B1),
            secondaryColor = Color(0xFFB39DDB),
            accentColor = Color(0xFF311B92),
            eyeStyle = EyeStyle.DICE_SPOTS,
            defaultRows = 3,
            defaultCols = 2,
            catchphrase = "Six! Ready for games!"
        ),
        7 to NumberblockData(
            value = 7,
            name = "Seven",
            primaryColor = Color(0xFF8E24AA),
            secondaryColor = Color(0xFFE1BEE7),
            accentColor = Color(0xFF4A148C),
            eyeStyle = EyeStyle.RAINBOW_SEVEN,
            defaultRows = 7,
            defaultCols = 1,
            catchphrase = "Seven! Lucky rainbow!"
        ),
        8 to NumberblockData(
            value = 8,
            name = "Eight",
            primaryColor = Color(0xFFD81B60),
            secondaryColor = Color(0xFFF48FB1),
            accentColor = Color(0xFF880E4F),
            eyeStyle = EyeStyle.OCTO_MASK,
            defaultRows = 4,
            defaultCols = 2,
            catchphrase = "Eight! Octoblock to the rescue!"
        ),
        9 to NumberblockData(
            value = 9,
            name = "Nine",
            primaryColor = Color(0xFF9E9E9E),
            secondaryColor = Color(0xFFE0E0E0),
            accentColor = Color(0xFF616161),
            eyeStyle = EyeStyle.SQUARE_NINE,
            defaultRows = 3,
            defaultCols = 3,
            catchphrase = "Nine! 3 by 3 square!",
            isSquare = true
        ),
        10 to NumberblockData(
            value = 10,
            name = "Ten",
            primaryColor = Color(0xFFFFFFFF),
            secondaryColor = Color(0xFFFFCDD2),
            accentColor = Color(0xFFD32F2F),
            eyeStyle = EyeStyle.WHITE_TEN,
            defaultRows = 5,
            defaultCols = 2,
            catchphrase = "Ten! Perfect Ten!"
        ),
        12 to NumberblockData(
            value = 12,
            name = "Twelve",
            primaryColor = Color(0xFFFF7043),
            secondaryColor = Color(0xFFFFCCBC),
            accentColor = Color(0xFFD84315),
            eyeStyle = EyeStyle.TWO_EYES,
            defaultRows = 4,
            defaultCols = 3,
            catchphrase = "Twelve! Super Rectangle!",
            isSuperRectangle = true
        ),
        16 to NumberblockData(
            value = 16,
            name = "Sixteen",
            primaryColor = Color(0xFF81C784),
            secondaryColor = Color(0xFFC8E6C9),
            accentColor = Color(0xFF2E7D32),
            eyeStyle = EyeStyle.SQUARE_GLASSES,
            defaultRows = 4,
            defaultCols = 4,
            catchphrase = "Sixteen! 4 by 4 Square!",
            isSquare = true
        ),
        20 to NumberblockData(
            value = 20,
            name = "Twenty",
            primaryColor = Color(0xFFFFB74D),
            secondaryColor = Color(0xFFFFE0B2),
            accentColor = Color(0xFFF57C00),
            eyeStyle = EyeStyle.PURPLE_GLASSES,
            defaultRows = 5,
            defaultCols = 4,
            catchphrase = "Twenty! Two tens together!"
        ),
        100 to NumberblockData(
            value = 100,
            name = "One Hundred",
            primaryColor = Color(0xFFFF5252),
            secondaryColor = Color(0xFFFFCDD2),
            accentColor = Color(0xFF880E4F),
            eyeStyle = EyeStyle.SINGLE_BIG,
            defaultRows = 10,
            defaultCols = 10,
            catchphrase = "One Hundred! Giant 10x10!",
            isSquare = true
        ),
        1000 to NumberblockData(
            value = 1000,
            name = "One Thousand",
            primaryColor = Color(0xFFFF3D00),
            secondaryColor = Color(0xFFFF9E80),
            accentColor = Color(0xFFBF360C),
            eyeStyle = EyeStyle.MEGA_STAR,
            defaultRows = 10,
            defaultCols = 10,
            catchphrase = "One Thousand! 10x10x10 Cube!"
        ),
        10000 to NumberblockData(
            value = 10000,
            name = "Ten Thousand",
            primaryColor = Color(0xFFFFD700),
            secondaryColor = Color(0xFFFFF9C4),
            accentColor = Color(0xFFFFA000),
            eyeStyle = EyeStyle.MEGA_STAR,
            defaultRows = 10,
            defaultCols = 10,
            catchphrase = "Ten Thousand! Golden Titan!"
        )
    )

    fun getBlockForValue(num: Int): NumberblockData {
        canonicalMap[num]?.let { return it }

        // Procedural generator for any arbitrary number up to 10,000!
        val sqrt = Math.sqrt(num.toDouble()).toInt()
        val isSquare = (sqrt * sqrt == num)

        val cols = when {
            isSquare -> sqrt
            num >= 1000 -> 10
            num >= 100 -> 10
            num % 10 == 0 -> 10
            num % 5 == 0 -> 5
            num % 4 == 0 -> 4
            num % 3 == 0 -> 3
            num % 2 == 0 -> 2
            else -> 1
        }
        val rows = Math.ceil(num.toDouble() / cols).toInt().coerceAtLeast(1)

        val baseColors = listOf(
            Color(0xFFE53935), Color(0xFFFF9800), Color(0xFFFFEB3B),
            Color(0xFF4CAF50), Color(0xFF03A9F4), Color(0xFF3F51B5),
            Color(0xFF8E24AA), Color(0xFF009688), Color(0xFFFF5722)
        )
        val pColor = baseColors[Math.abs(num) % baseColors.size]

        return NumberblockData(
            value = num,
            name = "$num",
            primaryColor = pColor,
            secondaryColor = pColor.copy(alpha = 0.6f),
            accentColor = Color.Black.copy(alpha = 0.5f),
            eyeStyle = if (num == 1) EyeStyle.SINGLE_BIG else if (isSquare) EyeStyle.SQUARE_GLASSES else EyeStyle.TWO_EYES,
            defaultRows = rows,
            defaultCols = cols,
            catchphrase = "I am $num!",
            isSquare = isSquare,
            isSuperRectangle = (num % 6 == 0 || num % 12 == 0)
        )
    }
}

data class AlphablockData(
    val letter: Char,
    val sound: String,
    val color: Color,
    val trait: String
)

object AlphablocksRegistry {
    val letters = listOf(
        AlphablockData('A', "ah", Color(0xFFE53935), "Apple acrobat"),
        AlphablockData('B', "buh", Color(0xFF1E88E5), "Bouncing bass player"),
        AlphablockData('C', "cuh", Color(0xFFFFB300), "Crunchy crack"),
        AlphablockData('D', "duh", Color(0xFF43A047), "Drumming dynamic"),
        AlphablockData('E', "eh", Color(0xFF8E24AA), "Energetic echo"),
        AlphablockData('F', "fff", Color(0xFF00ACC1), "Flying feather"),
        AlphablockData('G', "guh", Color(0xFF6D4C41), "Green grower"),
        AlphablockData('H', "huh", Color(0xFFFB8C00), "Happy breather"),
        AlphablockData('I', "ih", Color(0xFF00897B), "Incredible insect"),
        AlphablockData('J', "juh", Color(0xFFD81B60), "Jumping joy"),
        AlphablockData('K', "kuh", Color(0xFF3949AB), "Kick king"),
        AlphablockData('L', "lll", Color(0xFF7CB342), "Lollipop singer"),
        AlphablockData('M', "mmm", Color(0xFF5E35B1), "Munching monster"),
        AlphablockData('N', "nnn", Color(0xFFF4511E), "Noisy nose"),
        AlphablockData('O', "oh", Color(0xFFFFD600), "Orange opera"),
        AlphablockData('P', "puh", Color(0xFF039BE5), "Popping party"),
        AlphablockData('Q', "kw", Color(0xFFC2185B), "Quiet queen"),
        AlphablockData('R', "rrr", Color(0xFFE53935), "Roaring rocket"),
        AlphablockData('S', "sss", Color(0xFF43A047), "Sizzling snake"),
        AlphablockData('T', "tuh", Color(0xFF1E88E5), "Ticking clock"),
        AlphablockData('U', "uh", Color(0xFF8E24AA), "Umbrella upside"),
        AlphablockData('V', "vvv", Color(0xFF00ACC1), "Vrooming van"),
        AlphablockData('W', "wuh", Color(0xFFFFB300), "Waving wind"),
        AlphablockData('X', "ks", Color(0xFF6D4C41), "X marks the spot"),
        AlphablockData('Y', "yuh", Color(0xFFFB8C00), "Yawning yellow"),
        AlphablockData('Z', "zzz", Color(0xFF3949AB), "Zipping zebra")
    )

    private val map = letters.associateBy { it.letter }

    fun get(char: Char): AlphablockData {
        return map[char.uppercaseChar()] ?: AlphablockData(char.uppercaseChar(), "$char", Color(0xFF0288D1), "Letter")
    }

    fun getBlockForLetter(char: Char): AlphablockData = get(char)

    val wordToNumber = mapOf(
        "ONE" to 1,
        "TWO" to 2,
        "THREE" to 3,
        "FOUR" to 4,
        "FIVE" to 5,
        "SIX" to 6,
        "SEVEN" to 7,
        "EIGHT" to 8,
        "NINE" to 9,
        "TEN" to 10,
        "CAT" to 3,
        "SUN" to 1,
        "STAR" to 5,
        "DOG" to 4,
        "BOX" to 6,
        "OCTO" to 8,
        "HUNDRED" to 100
    )

    val numberWords: Map<String, Int> get() = wordToNumber
}
