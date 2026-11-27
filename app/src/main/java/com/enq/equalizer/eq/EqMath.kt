package com.enq.equalizer.eq

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sinh

fun bandQ(pro: Boolean): Float {
    val r = (HIGH_HZ / lowHz(pro)).pow(1f / (BAND_COUNT - 1))
    val bw = (ln(r) / ln(2f)) * 1.1f
    return (1.0 / (2.0 * sinh(ln(2.0) / 2.0 * bw))).toFloat()
}

fun responseCurve(
    gains: List<Float>,
    pro: Boolean,
    points: Int = 240,
    sampleRate: Float = 48000f
): FloatArray {
    val out = FloatArray(points)
    val lo = lowHz(pro)
    val q = bandQ(pro).toDouble()
    for (i in 0 until BAND_COUNT) {
        val g = gains[i]
        if (g == 0f) continue
        val f0 = bandFreq(i, pro).toDouble()
        val amp = 10.0.pow(g / 40.0)
        val w0 = 2 * PI * f0 / sampleRate
        val al = sin(w0) / (2 * q)
        val c = cos(w0)
        val b0 = 1 + al * amp
        val b1 = -2 * c
        val b2 = 1 - al * amp
        val a0 = 1 + al / amp
        val a1 = -2 * c
        val a2 = 1 - al / amp
        for (j in 0 until points) {
            val f = min(lo * (HIGH_HZ / lo).pow(j / (points - 1f)), sampleRate * 0.495f).toDouble()
            val w = 2 * PI * f / sampleRate
            val cs = cos(w)
            val sn = sin(w)
            val c2 = cos(2 * w)
            val s2 = sin(2 * w)
            val nr = b0 + b1 * cs + b2 * c2
            val ni = -(b1 * sn + b2 * s2)
            val dr = a0 + a1 * cs + a2 * c2
            val di = -(a1 * sn + a2 * s2)
            out[j] += (10 * log10((nr * nr + ni * ni) / (dr * dr + di * di))).toFloat()
        }
    }
    return out
}
