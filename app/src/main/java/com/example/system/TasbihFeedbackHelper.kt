package com.example.system

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.R

class TasbihFeedbackHelper(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var tapSoundId: Int = 0
    private var completeSoundId: Int = 0
    private var tapLoaded: Boolean = false
    private var completeLoaded: Boolean = false
    private var fallbackToneGenerator: ToneGenerator? = null

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        runCatching {
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val pool = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(attrs)
                .build()

            pool.setOnLoadCompleteListener { _, sampleId, status ->
                if (status == 0) {
                    if (sampleId == tapSoundId) tapLoaded = true
                    if (sampleId == completeSoundId) completeLoaded = true
                }
            }

            tapSoundId = pool.load(context, R.raw.tasbih_tap, 1)
            completeSoundId = pool.load(context, R.raw.tasbih_complete, 1)
            soundPool = pool
        }
    }

    private fun getFallbackToneGenerator(): ToneGenerator? {
        if (fallbackToneGenerator == null) {
            fallbackToneGenerator = runCatching {
                ToneGenerator(AudioManager.STREAM_MUSIC, 65)
            }.getOrNull()
        }
        return fallbackToneGenerator
    }

    fun playFeedback(soundEnabled: Boolean, vibrationEnabled: Boolean, isTargetCompleted: Boolean) {
        if (soundEnabled) {
            playTapSound(isTargetCompleted)
        }
        if (vibrationEnabled) {
            vibrate(isTargetCompleted)
        }
    }

    private fun playTapSound(isTargetCompleted: Boolean) {
        val pool = soundPool
        if (pool != null && tapLoaded) {
            if (isTargetCompleted && completeLoaded) {
                pool.play(completeSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
            } else {
                pool.play(tapSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
            }
            return
        }

        // Fallback if SoundPool is still loading
        runCatching {
            val tone = if (isTargetCompleted) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_BEEP
            val durationMs = if (isTargetCompleted) 140 else 38
            getFallbackToneGenerator()?.startTone(tone, durationMs)
        }
    }

    private fun vibrate(isTargetCompleted: Boolean) {
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        if (vibrator == null || !vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isTargetCompleted) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 55, 55, 100),
                        intArrayOf(0, 180, 0, 255),
                        -1,
                    ),
                )
            } else {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(22L, 110),
                )
            }
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(if (isTargetCompleted) 130L else 22L)
        }
    }

    fun release() {
        runCatching { soundPool?.release() }
        soundPool = null
        runCatching { fallbackToneGenerator?.release() }
        fallbackToneGenerator = null
    }
}
