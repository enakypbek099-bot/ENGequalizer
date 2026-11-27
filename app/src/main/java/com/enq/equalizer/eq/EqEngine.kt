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
            val dp = DynamicsProcessing(0, session, cfg)
            effects[session] = dp
            write(dp, last)
            dp.enabled = last.enabled
            true
        } catch (e: Throwable) {
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
        for (dp in effects.values) {
            try {
                write(dp, state)
                dp.enabled = state.enabled
            } catch (_: Throwable) {}
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
