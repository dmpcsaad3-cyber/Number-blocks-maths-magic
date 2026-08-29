package com.example.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundFxPlayer {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 44100

    private fun playTone(frequencies: List<Float>, durationsMs: List<Int>, waveType: Int = 0) {
        scope.launch {
            try {
                var totalSamples = 0
                for (d in durationsMs) {
                    totalSamples += (SAMPLE_RATE * (d / 1000f)).toInt()
                }
                if (totalSamples <= 0) return@launch

                val buffer = ShortArray(totalSamples)
                var currentIdx = 0

                for (i in frequencies.indices) {
                    val freq = frequencies[i]
                    val durMs = durationsMs.getOrElse(i) { durationsMs.last() }
                    val segSamples = (SAMPLE_RATE * (durMs / 1000f)).toInt()

                    for (s in 0 until segSamples) {
                        if (currentIdx >= totalSamples) break
                        val t = s.toDouble() / SAMPLE_RATE
                        val angle = 2.0 * Math.PI * freq * t
                        val rawWave = when (waveType) {
                            1 -> if (sin(angle) >= 0) 1.0 else -1.0 // Square wave
                            2 -> 2.0 * (t * freq - Math.floor(t * freq + 0.5)) // Sawtooth
                            else -> sin(angle) // Sine wave
                        }
                        // Envelope to prevent clipping clicks
                        val fadeEnvelope = if (s < 200) s / 200.0 else if (s > segSamples - 300) (segSamples - s) / 300.0 else 1.0
                        buffer[currentIdx++] = (rawWave * fadeEnvelope * 16000).toInt().toShort()
                    }
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                kotlinx.coroutines.delay(durationsMs.sum().toLong() + 50)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                // Ignore audio errors safely
            }
        }
    }

    fun playPop() {
        playTone(listOf(400f, 650f), listOf(35, 45))
    }

    fun playSnap() {
        playTone(listOf(800f, 1200f), listOf(20, 30))
    }

    fun playCorrect() {
        // Cheerful major arpeggio
        playTone(listOf(523.25f, 659.25f, 783.99f, 1046.50f), listOf(70, 70, 70, 160))
    }

    fun playLevelUp() {
        // Fanfare
        playTone(listOf(440f, 554f, 659f, 880f, 1108f), listOf(80, 80, 80, 100, 250))
    }

    fun playMagicGlow() {
        // Mystical ascending sweep
        playTone(listOf(300f, 450f, 600f, 750f, 900f, 1200f), listOf(50, 50, 50, 50, 60, 150))
    }

    fun playPhonics(letter: Char) {
        val baseFreq = 260f + ((letter.uppercaseChar() - 'A') * 24f)
        playTone(listOf(baseFreq, baseFreq * 1.25f), listOf(80, 120))
    }

    fun playNumberblockPop(number: Int) {
        val freq = (300f + (number % 20) * 35f).coerceIn(200f, 1500f)
        playTone(listOf(freq, freq * 1.33f), listOf(40, 60))
    }
}
