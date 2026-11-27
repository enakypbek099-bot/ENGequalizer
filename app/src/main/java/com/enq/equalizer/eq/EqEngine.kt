package com.enq.equalizer.eq

import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

private interface Fx {
    val label: String
    fun apply(s: EqState)
    fun release()
}

private class DpFx(session: Int) : Fx {
    private val dp: DynamicsProcessing
    override val label: String

    init {
        val cfg = DynamicsProcessing.Config.Builder(
            DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
            2, true, BAND_COUNT, false, 0, false, 0, false
        ).build()
        dp = DynamicsProcessing(100, session, cfg)
        try {
            dp.setPreEqAllChannelsTo(DynamicsProcessing.Eq(true, true, BAND_COUNT))
            dp.enabled = true
            label = "DP упр=${dp.hasControl()}"
        } catch (e: Throwable) {
            dp.release()
            throw e
        }
    }

    override fun apply(s: EqState) {
        for (i in 0 until BAND_COUNT) {
            val f = bandFreq(i, s.pro)
            val cutoff = if (i < BAND_COUNT - 1) sqrt(f * bandFreq(i + 1, s.pro)) else 22000f
            val gain = s.gains[i].coerceIn(-DB_RANGE, DB_RANGE)
            dp.setPreEqBandAllChannelsTo(i, DynamicsProcessing.EqBand(true, cutoff, gain))
        }
        dp.setInputGainAllChannelsTo(s.preampDb.coerceIn(-DB_RANGE, DB_RANGE))
        dp.enabled = s.enabled
    }

    override fun release() {
        try { dp.release() } catch (_: Throwable) {}
    }
}

private class ClassicFx(session: Int) : Fx {
    private val eq = Equalizer(100, session)
    private val le: LoudnessEnhancer? = try { LoudnessEnhancer(session) } catch (_: Throwable) { null }
    override val label: String

    init {
        eq.enabled = true
        le?.enabled = true
        label = "EQ полос=${eq.numberOfBands}"
    }

    private fun gainAt(s: EqState, f: Float): Float {
        if (f <= bandFreq(0, s.pro)) return s.gains[0]
        for (i in 0 until BAND_COUNT - 1) {
            val a = bandFreq(i, s.pro)
            val b = bandFreq(i + 1, s.pro)
            if (f < b) {
                val t = ln(f / a) / ln(b / a)
                return s.gains[i] * (1f - t) + s.gains[i + 1] * t
            }
        }
        return s.gains[BAND_COUNT - 1]
    }

    override fun apply(s: EqState) {
        val range = eq.bandLevelRange
        val n = eq.numberOfBands.toInt()
        val pre = s.preampDb
        for (b in 0 until n) {
            val r = eq.getBandFreqRange(b.toShort())
            val lo = max(r[0] / 1000f, 20f)
            val hi = max(r[1] / 1000f, lo * 1.2f)
            var sum = 0f
            val steps = 6
            for (k in 0 until steps) {
                val f = lo * (hi / lo).pow(k / (steps - 1f))
                sum += gainAt(s, f)
            }
            val g = sum / steps + min(pre, 0f)
            val mb = (g * 100f).toInt().coerceIn(range[0].toInt(), range[1].toInt())
            eq.setBandLevel(b.toShort(), mb.toShort())
        }
        le?.setTargetGain((max(pre, 0f) * 100f).toInt())
        eq.enabled = s.enabled
    }

    override fun release() {
        try { eq.release() } catch (_: Throwable) {}
        try { le?.release() } catch (_: Throwable) {}
    }
}

class EqEngine {
    private val effects = HashMap<Int, Fx>()
    private var last = EqState()
    private var mode = 0

    fun has(session: Int) = effects.containsKey(session)

    fun setMode(m: Int) {
        if (m == mode) return
        val ids = effects.keys.toList()
        releaseAll()
        mode = m
        for (id in ids) attach(id)
    }

    fun attach(session: Int): Boolean {
        if (effects.containsKey(session)) return true
        return try {
            val fx: Fx = if (mode == 0) DpFx(session) else ClassicFx(session)
            effects[session] = fx
            fx.apply(last)
            if (session > 0) EqStore.info.value = "s$session ${fx.label}"
            true
        } catch (e: Throwable) {
            effects.remove(session)
            if (session > 0) {
                EqStore.info.value = "s$session ошибка: ${e.javaClass.simpleName} ${e.message}"
            }
            false
        }
    }

    fun detach(session: Int) {
        effects.remove(session)?.release()
    }

    fun update(state: EqState) {
        last = state
        for ((session, fx) in effects) {
            try {
                fx.apply(state)
            } catch (e: Throwable) {
                EqStore.info.value = "s$session запись: ${e.javaClass.simpleName} ${e.message}"
            }
        }
    }

    fun releaseAll() {
        for (k in effects.keys.toList()) detach(k)
    }
}
