package com.lifescore.app.core.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TextToSpeechNarrator(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false
    private var queuedText: String? = null
    private var currentSpeed: Float = 1.0f
    private var onCompletionCallback: (() -> Unit)? = null
    private var isManuallyStopped = false

    companion object {
        @Volatile
        private var activeNarrator: TextToSpeechNarrator? = null

        fun stopActive() {
            activeNarrator?.stop()
            activeNarrator = null
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isReady = true
            tts?.language = Locale.US
            tts?.setSpeechRate(currentSpeed)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    if (!isManuallyStopped) {
                        onCompletionCallback?.invoke()
                    }
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    if (!isManuallyStopped) {
                        onCompletionCallback?.invoke()
                    }
                }
                override fun onError(utteranceId: String?, errorCode: Int) {
                    if (!isManuallyStopped) {
                        onCompletionCallback?.invoke()
                    }
                }
            })
            queuedText?.let {
                speak(it, currentSpeed, onCompletionCallback)
                queuedText = null
            }
        }
    }

    fun speak(text: String, speed: Float = 1.0f, onComplete: (() -> Unit)? = null) {
        if (activeNarrator != null && activeNarrator != this) {
            activeNarrator?.stop()
        }
        activeNarrator = this
        isManuallyStopped = false
        currentSpeed = speed
        onCompletionCallback = onComplete
        if (!isReady) {
            queuedText = text
            return
        }
        tts?.setSpeechRate(speed)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_BOOK_SUMMARY_UTTERANCE")
    }

    fun setSpeed(speed: Float) {
        currentSpeed = speed
        if (isReady) {
            tts?.setSpeechRate(speed)
        }
    }

    fun stop() {
        isManuallyStopped = true
        queuedText = null
        onCompletionCallback = null
        try {
            if (isReady) {
                tts?.stop()
            }
        } catch (_: Exception) {}
        if (activeNarrator == this) {
            activeNarrator = null
        }
    }

    fun shutdown() {
        stop()
        try {
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
        isReady = false
    }
}
