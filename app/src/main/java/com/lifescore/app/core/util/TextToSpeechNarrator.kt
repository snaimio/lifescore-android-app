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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isReady = true
            tts?.language = Locale.US
            tts?.setSpeechRate(currentSpeed)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    onCompletionCallback?.invoke()
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {}
                override fun onError(utteranceId: String?, errorCode: Int) {
                    onCompletionCallback?.invoke()
                }
            })
            queuedText?.let {
                speak(it, currentSpeed, onCompletionCallback)
                queuedText = null
            }
        }
    }

    fun speak(text: String, speed: Float = 1.0f, onComplete: (() -> Unit)? = null) {
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
        queuedText = null
        if (isReady) {
            tts?.stop()
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
        isReady = false
    }
}
