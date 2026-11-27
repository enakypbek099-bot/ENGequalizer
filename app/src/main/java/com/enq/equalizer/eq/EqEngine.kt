package com.enq.equalizer.eq

import android.media.audiofx.DynamicsProcessing
import kotlin.math.sqrt

class EqEngine {
    private val effects = HashMap<Int, DynamicsProcessing>()
    private var last = EqState()

    fun has(session: Int) = effects.containsKey(session)

    fun attach(session: Int): Boolean {
        if (effects.containsKey(session)) return true
        return try {
            val cfg = DynamicsProcessing.Config.Builder(
                DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                2, true, BAND_COUNT, false, 0, false, 0, false
            ).build()
            val dp = DynamicsProcessing(100, session, cfg)
            effects[session] = dp
            dp.setPreEqAllChannelsTo(DynamicsProcessing.Eq(true, true, BAND_COUNT))
            write(dp, last)
            dp.enabled = true
            if (session > 0) {
                EqStore.info.value = "s$session вкл=${dp.enabled} упр=${dp.hasControl()}"
            }
            true
        } catch (e: Throwable) {
            if (session > 0) {
                EqStore.info.value = "s$session ошибка: ${e.javaClass.simpleName} ${e.message}"
            }
            effects.remove(session)
            false
        }
    }

    fun detach(session: Int) {
        effects.remove(session)?.let {
            try { it.release() } catch (_: Throwable) {}
        }
    }

    fun update(state: EqState) {
        last = state
        for ((session, dp) in effects) {
            try {
                write(dp, state)
                dp.enabled = state.enabled
            } catch (e: Throwable) {
                EqStore.info.value = "s$session запись: ${e.javaClass.simpleName} ${e.message}"
            }
        }
    }

    fun releaseAll() {
        for (k in effects.keys.toList()) detach(k)
    }

    private fun write(dp: DynamicsProcessing, s: EqState) {
        for (i in 0 until BAND_COUNT) {
            val f = bandFreq(i, s.pro)
            val cutoff = if (i < BAND_COUNT - 1) sqrt(f * bandFreq(i + 1, s.pro)) else 22000f
            val gain = s.gains[i].coerceIn(-DB_RANGE, DB_RANGE)
            dp.setPreEqBandAllChannelsTo(i, DynamicsProcessing.EqBand(true, cutoff, gain))
        }
        dp.setInputGainAllChannelsTo(s.preampDb.coerceIn(-DB_RANGE, DB_RANGE))
    }
}
