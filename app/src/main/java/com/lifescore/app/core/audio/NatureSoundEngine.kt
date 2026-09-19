package com.lifescore.app.core.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.*
import kotlin.random.Random

/**
 * High-performance, offline procedural DSP Nature Sound Engine.
 * Synthesizes pure acoustic physics for nature sounds in real time
 * using Android's native AudioTrack with 0 MB audio asset overhead.
 */
class NatureSoundEngine {

    companion object {
        const val SAMPLE_RATE = 22050
        const val CHUNK_SIZE = 2048
    }

    private var audioTrack: AudioTrack? = null
    private var playbackThread: Thread? = null
    private val isRunning = AtomicBoolean(false)
    private val activeVolumes = ConcurrentHashMap<String, Float>()

    // DSP Synthesizers
    private val rainSynth = RainDsp()
    private val oceanSynth = OceanDsp()
    private val streamSynth = StreamDsp()
    private val forestSynth = ForestWindDsp()
    private val cricketsSynth = CricketsDsp()
    private val thunderSynth = ThunderDsp()
    private val waterfallSynth = WaterfallDsp()
    private val fireSynth = CampfireDsp()

    fun setTrackVolume(trackId: String, volume: Float) {
        if (volume <= 0.001f) {
            activeVolumes.remove(trackId)
        } else {
            activeVolumes[trackId] = volume.coerceIn(0f, 1f)
        }

        if (activeVolumes.isNotEmpty()) {
            startEngineIfNeeded()
        } else {
            stopEngineIfEmpty()
        }
    }

    fun removeTrack(trackId: String) {
        activeVolumes.remove(trackId)
        if (activeVolumes.isEmpty()) {
            stopEngineIfEmpty()
        }
    }

    fun stopAll() {
        activeVolumes.clear()
        stopEngineIfEmpty()
    }

    fun isPlaying(): Boolean = isRunning.get() && activeVolumes.isNotEmpty()

    fun getActiveVolumes(): Map<String, Float> = activeVolumes.toMap()

    @Synchronized
    private fun startEngineIfNeeded() {
        if (isRunning.get()) return

        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = max(minBufferSize, CHUNK_SIZE * 4)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            isRunning.set(true)

            playbackThread = Thread({
                renderLoop()
            }, "NatureSoundEngine-DSP").apply {
                priority = Thread.NORM_PRIORITY
                isDaemon = true
                start()
            }
        } catch (_: Throwable) {
            isRunning.set(false)
        }
    }

    @Synchronized
    private fun stopEngineIfEmpty() {
        if (!isRunning.get()) return
        isRunning.set(false)

        try {
            playbackThread?.interrupt()
            playbackThread = null
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (_: Exception) {}
    }

    private fun renderLoop() {
        val shortBuffer = ShortArray(CHUNK_SIZE * 2) // Stereo interleaved
        val leftMix = FloatArray(CHUNK_SIZE)
        val rightMix = FloatArray(CHUNK_SIZE)

        while (isRunning.get() && !Thread.currentThread().isInterrupted) {
            if (activeVolumes.isEmpty()) {
                // Sleep briefly to avoid spinning when idle
                try {
                    Thread.sleep(30)
                } catch (_: InterruptedException) {
                    break
                }
                continue
            }

            leftMix.fill(0f)
            rightMix.fill(0f)

            // Render active nature DSP streams
            activeVolumes.forEach { (trackId, volume) ->
                if (volume > 0.001f) {
                    when (trackId) {
                        "rain" -> rainSynth.render(leftMix, rightMix, volume, CHUNK_SIZE)
                        "ocean" -> oceanSynth.render(leftMix, rightMix, volume, CHUNK_SIZE)
                        "stream" -> streamSynth.render(leftMix, rightMix, volume, CHUNK_SIZE)
                        "forest" -> forestSynth.render(leftMix, rightMix, volume, CHUNK_SIZE)
                        "crickets" -> cricketsSynth.render(leftMix, rightMix, volume, CHUNK_SIZE)
                        "thunder" -> thunderSynth.render(leftMix, rightMix, volume, CHUNK_SIZE)
                        "waterfall" -> waterfallSynth.render(leftMix, rightMix, volume, CHUNK_SIZE)
                        "fire" -> fireSynth.render(leftMix, rightMix, volume, CHUNK_SIZE)
                    }
                }
            }

            // Master soft clip and write 16-bit PCM stereo
            var pcmIdx = 0
            for (i in 0 until CHUNK_SIZE) {
                val l = tanh(leftMix[i]).coerceIn(-1f, 1f)
                val r = tanh(rightMix[i]).coerceIn(-1f, 1f)
                shortBuffer[pcmIdx++] = (l * 32767.0f).toInt().toShort()
                shortBuffer[pcmIdx++] = (r * 32767.0f).toInt().toShort()
            }

            val written = audioTrack?.write(shortBuffer, 0, shortBuffer.size) ?: -1
            if (written < 0) {
                break
            }
        }
    }

    fun release() {
        stopAll()
    }

    // ==========================================
    // DSP Synthesizer Classes for Nature Sounds
    // ==========================================

    /**
     * Rain Synthesizer: Continuous pink noise foliage bed + randomized droplet clicks.
     */
    private class RainDsp {
        private var b0 = 0f; private var b1 = 0f; private var b2 = 0f
        private var dropTimer = 0

        fun render(left: FloatArray, right: FloatArray, gain: Float, samples: Int) {
            val random = Random
            for (i in 0 until samples) {
                val white = random.nextFloat() * 2f - 1f
                b0 = 0.99886f * b0 + white * 0.0555179f
                b1 = 0.99332f * b1 + white * 0.0750759f
                b2 = 0.96900f * b2 + white * 0.1538520f
                val pink = (b0 + b1 + b2 + white * 0.5362f) * 0.11f

                // Droplet transient
                var droplet = 0f
                if (dropTimer <= 0) {
                    if (random.nextFloat() < 0.04f) {
                        droplet = (random.nextFloat() * 0.4f + 0.1f)
                        dropTimer = random.nextInt(20, 100)
                    }
                } else {
                    dropTimer--
                }

                val sample = (pink + droplet) * gain * 0.7f
                left[i] += sample * 0.95f
                right[i] += sample * 1.05f
            }
        }
    }

    /**
     * Ocean Synthesizer: Rhythmic 8-second wave swells with foam surf resonance.
     */
    private class OceanDsp {
        private var brown = 0f
        private var lfoPhase = 0.0

        fun render(left: FloatArray, right: FloatArray, gain: Float, samples: Int) {
            val random = Random
            for (i in 0 until samples) {
                lfoPhase += (2.0 * Math.PI * 0.11) / SAMPLE_RATE
                if (lfoPhase > 2.0 * Math.PI) lfoPhase -= 2.0 * Math.PI

                val waveSurge = ((sin(lfoPhase) + 1.0) * 0.5).pow(2.2).toFloat()
                val white = random.nextFloat() * 2f - 1f
                brown = (brown + (0.02f * white)) / 1.02f

                val sample = (brown * 4.5f * waveSurge) * gain * 0.85f
                left[i] += sample
                right[i] += sample * (0.8f + 0.4f * waveSurge)
            }
        }
    }

    /**
     * Mountain Brook Stream Synthesizer: Gurgling resonant filters over crisp water.
     */
    private class StreamDsp {
        private var p0 = 0f; private var p1 = 0f
        private var bubblePhase = 0.0

        fun render(left: FloatArray, right: FloatArray, gain: Float, samples: Int) {
            val random = Random
            for (i in 0 until samples) {
                val white = random.nextFloat() * 2f - 1f
                p0 = 0.95f * p0 + white * 0.05f
                p1 = 0.92f * p1 + white * 0.08f

                bubblePhase += 0.015
                val bubbleMod = (sin(bubblePhase) * 0.3f + sin(bubblePhase * 2.3) * 0.2f).toFloat()

                val sample = (p0 + p1 * (1f + bubbleMod)) * 0.6f * gain
                left[i] += sample * 1.1f
                right[i] += sample * 0.9f
            }
        }
    }

    /**
     * Forest Pine Wind Synthesizer: Modulated swaying breeze through tree canopy.
     */
    private class ForestWindDsp {
        private var windFilter = 0f
        private var gustPhase = 0.0

        fun render(left: FloatArray, right: FloatArray, gain: Float, samples: Int) {
            val random = Random
            for (i in 0 until samples) {
                gustPhase += (2.0 * Math.PI * 0.05) / SAMPLE_RATE
                val gust = ((sin(gustPhase) + sin(gustPhase * 1.7) * 0.5 + 1.5) / 3.0).toFloat()

                val white = random.nextFloat() * 2f - 1f
                windFilter = 0.985f * windFilter + white * (0.015f * gust)

                val sample = windFilter * 3.2f * gain
                left[i] += sample * 1.05f
                right[i] += sample * 0.95f
            }
        }
    }

    /**
     * Night Meadow Crickets Synthesizer: Natural sinusoidal chirp bursts and gentle night air.
     */
    private class CricketsDsp {
        private var chirpPhase = 0.0
        private var chirpTimer = 0
        private var isChirping = false
        private var chirpEnvelope = 0f

        fun render(left: FloatArray, right: FloatArray, gain: Float, samples: Int) {
            val random = Random
            for (i in 0 until samples) {
                if (chirpTimer <= 0) {
                    if (!isChirping && random.nextFloat() < 0.005f) {
                        isChirping = true
                        chirpTimer = random.nextInt(1200, 2500)
                        chirpEnvelope = 1f
                    } else if (isChirping) {
                        isChirping = false
                        chirpTimer = random.nextInt(3000, 9000)
                    }
                } else {
                    chirpTimer--
                }

                var chirpSample = 0f
                if (isChirping) {
                    chirpPhase += (2.0 * Math.PI * 4750.0) / SAMPLE_RATE
                    if (chirpPhase > 2.0 * Math.PI) chirpPhase -= 2.0 * Math.PI
                    val pulse = (sin(chirpPhase * 0.03) > 0.0)
                    if (pulse) {
                        chirpSample = sin(chirpPhase).toFloat() * 0.25f * chirpEnvelope
                    }
                    chirpEnvelope *= 0.9998f
                }

                val sample = chirpSample * gain
                left[i] += sample * 0.85f
                right[i] += sample * 1.15f
            }
        }
    }

    /**
     * Distant Rolling Thunder Synthesizer: Sub-bass resonance rolling over soft rain.
     */
    private class ThunderDsp {
        private var thunderTimer = Random.nextInt(10000, 40000)
        private var thunderEnergy = 0f
        private var rumble = 0f

        fun render(left: FloatArray, right: FloatArray, gain: Float, samples: Int) {
            val random = Random
            for (i in 0 until samples) {
                if (thunderTimer <= 0) {
                    thunderEnergy = 1.0f
                    thunderTimer = random.nextInt(30000, 90000) // Next thunder in 1.5 - 4 seconds
                } else {
                    thunderTimer--
                }

                if (thunderEnergy > 0.001f) {
                    val white = random.nextFloat() * 2f - 1f
                    rumble = (rumble * 0.96f) + (white * 0.04f * thunderEnergy)
                    thunderEnergy *= 0.9997f
                } else {
                    rumble = 0f
                }

                val sample = rumble * 5.0f * gain
                left[i] += sample * 1.2f
                right[i] += sample * 0.8f
            }
        }
    }

    /**
     * Woodland Waterfall Synthesizer: Continuous broad-spectrum cascading hydro spray.
     */
    private class WaterfallDsp {
        private var f0 = 0f; private var f1 = 0f

        fun render(left: FloatArray, right: FloatArray, gain: Float, samples: Int) {
            val random = Random
            for (i in 0 until samples) {
                val white = random.nextFloat() * 2f - 1f
                f0 = 0.97f * f0 + white * 0.03f
                f1 = 0.90f * f1 + white * 0.10f

                val sample = (f0 * 1.8f + f1 * 0.8f) * 0.55f * gain
                left[i] += sample
                right[i] += sample
            }
        }
    }

    /**
     * Wilderness Campfire Synthesizer: Warm low-frequency embers with randomized wood pops.
     */
    private class CampfireDsp {
        private var emberHiss = 0f
        private var popTimer = 0

        fun render(left: FloatArray, right: FloatArray, gain: Float, samples: Int) {
            val random = Random
            for (i in 0 until samples) {
                val white = random.nextFloat() * 2f - 1f
                emberHiss = 0.96f * emberHiss + white * 0.04f

                var pop = 0f
                if (popTimer <= 0) {
                    if (random.nextFloat() < 0.008f) {
                        pop = (random.nextFloat() * 0.6f + 0.2f) * (if (random.nextBoolean()) 1f else -1f)
                        popTimer = random.nextInt(100, 800)
                    }
                } else {
                    popTimer--
                }

                val sample = (emberHiss * 0.7f + pop) * gain * 0.65f
                left[i] += sample * 0.9f
                right[i] += sample * 1.1f
            }
        }
    }
}
