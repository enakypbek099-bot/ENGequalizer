package com.enq.equalizer.eq

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

object TestNoise {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null
    val playing = MutableStateFlow(false)

    fun toggle() {
        if (playing.value) job?.cancel() else start()
    }

    private fun start() {
        playing.value = true
        job = scope.launch {
            val rate = 48000
            val min = AudioTrack.getMinBufferSize(
                rate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT
            )
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(rate)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                )
                .setBufferSizeInBytes(maxOf(min, 8192) * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            val engine = EqEngine()
            engine.attach(track.audioSessionId)
            val upd = launch { EqStore.state.collect { engine.update(it) } }
            try {
                track.play()
                val buf = ShortArray(4096)
                while (isActive) {
                    for (i in buf.indices) buf[i] = Random.nextInt(-6000, 6000).toShort()
                    track.write(buf, 0, buf.size)
                }
            } finally {
                upd.cancel()
                try { track.stop() } catch (_: Throwable) {}
                track.release()
                engine.releaseAll()
                playing.value = false
            }
        }
    }
}
