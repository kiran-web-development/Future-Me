package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundSynthesizer {

    private const val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.IO)

    fun playClickSound() {
        scope.launch {
            // Sleek Tick Feedback: High frequency, very short duration
            playSineWave(frequency = 1200.0, durationMs = 15, volume = 0.3f)
        }
    }

    fun playTimelineChime() {
        scope.launch {
            // Ascending Triad Chime: C, E, G
            playSineWave(523.25, 100, 0.4f)
            Thread.sleep(100)
            playSineWave(659.25, 100, 0.4f)
            Thread.sleep(100)
            playSineWave(783.99, 300, 0.4f)
        }
    }

    fun playChatSendSound() {
        scope.launch {
            // Chat Up-Link Ping: Quick ascending slide
            playFrequencySlide(600.0, 900.0, 100, 0.4f)
        }
    }

    fun playChatReceiveSound() {
        scope.launch {
            // Resonant Cascade Chime: Descending bell-like
            playSineWave(880.0, 100, 0.5f)
            Thread.sleep(50)
            playSineWave(659.25, 200, 0.4f)
        }
    }

    private fun playSineWave(frequency: Double, durationMs: Int, volume: Float) {
        val numSamples = (durationMs * sampleRate) / 1000
        if (numSamples <= 0) return
        val sample = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val dVal = sin(2.0 * Math.PI * i / (sampleRate / frequency))
            val norm = (dVal * 32767 * volume).toInt()
            sample[i] = norm.toShort()
        }

        playAudioTrack(sample, durationMs)
    }
    
    private fun playFrequencySlide(startFreq: Double, endFreq: Double, durationMs: Int, volume: Float) {
        val numSamples = (durationMs * sampleRate) / 1000
        if (numSamples <= 0) return
        val sample = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val ratio = i.toDouble() / numSamples.toDouble()
            val currentFreq = startFreq + (endFreq - startFreq) * ratio
            val dVal = sin(2.0 * Math.PI * i / (sampleRate / currentFreq))
            val norm = (dVal * 32767 * volume).toInt()
            sample[i] = norm.toShort()
        }

        playAudioTrack(sample, durationMs)
    }

    private fun playAudioTrack(sample: ShortArray, durationMs: Int) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(sample.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(sample, 0, sample.size)
            audioTrack.play()

            Thread.sleep(durationMs.toLong() + 50)
            audioTrack.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
