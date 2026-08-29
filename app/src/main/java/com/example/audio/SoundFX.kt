package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundFX {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 44100

    fun playTone(freq: Double, durationMs: Int = 100) {
        playToneSequence(listOf(freq.toFloat()), listOf(durationMs))
    }

    private fun playToneSequence(frequencies: List<Float>, durationsMs: List<Int>, waveType: Int = 0) {
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
                            1 -> if (sin(angle) >= 0) 1.0 else -1.0
                            2 -> 2.0 * (t * freq - Math.floor(t * freq + 0.5))
                            else -> sin(angle)
                        }
                        val fade = if (s < 150) s / 150.0 else if (s > segSamples - 200) (segSamples - s) / 200.0 else 1.0
                        buffer[currentIdx++] = (rawWave * fade * 15000).toInt().toShort()
                    }
                }

                val track = AudioTrack.Builder()
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

                track.write(buffer, 0, buffer.size)
                track.play()
                kotlinx.coroutines.delay(durationsMs.sum().toLong() + 40)
                track.stop()
                track.release()
            } catch (e: Exception) {
                // Silently handle any audio errors
            }
        }
    }

    fun playPop() {
        playToneSequence(listOf(450f, 700f), listOf(35, 45))
    }

    fun playSnap() {
        playToneSequence(listOf(750f, 1100f), listOf(25, 35))
    }

    fun playCorrect() {
        playToneSequence(listOf(523.25f, 659.25f, 783.99f, 1046.50f), listOf(70, 70, 70, 160))
    }

    fun playFanfare() {
        playToneSequence(listOf(523.25f, 659.25f, 783.99f, 1046.50f), listOf(70, 70, 70, 160))
    }

    fun playWrong() {
        playToneSequence(listOf(300f, 220f), listOf(100, 150), waveType = 1)
    }

    fun playFail() {
        playWrong()
    }

    fun playLevelUp() {
        playToneSequence(listOf(440f, 554f, 659f, 880f, 1108f), listOf(80, 80, 80, 100, 240))
    }

    fun playMagicMirror() {
        playToneSequence(listOf(400f, 600f, 800f, 1000f, 1200f, 1600f), listOf(40, 40, 40, 40, 50, 120))
    }

    fun playMirrorShimmer() {
        playMagicMirror()
    }

    fun playLetterSound(char: Char) {
        val freq = 260f + ((char.uppercaseChar() - 'A') * 24f)
        playToneSequence(listOf(freq, freq * 1.25f), listOf(70, 100))
    }

    fun playNumberblockPop(number: Int) {
        val freq = (300f + (number % 20) * 35f).coerceIn(200f, 1500f)
        playToneSequence(listOf(freq, freq * 1.33f), listOf(35, 55))
    }

    fun playOblongTransform() {
        playToneSequence(listOf(350f, 500f, 700f, 900f), listOf(50, 50, 50, 100))
    }

    fun playWonderAction() {
        playToneSequence(listOf(500f, 750f, 1000f), listOf(40, 40, 80))
    }
}
