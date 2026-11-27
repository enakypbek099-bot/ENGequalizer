package com.enq.equalizer.eq

import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow

const val BAND_COUNT = 32
const val DB_RANGE = 12f
const val HIGH_HZ = 20000f

fun lowHz(pro: Boolean): Float = if (pro) 5f else 20f

fun bandFreq(index: Int, pro: Boolean): Float {
    val lo = lowHz(pro)
    return lo * (HIGH_HZ / lo).pow(index / (BAND_COUNT - 1).toFloat())
}

private fun nf(x: Float, digits: Int): String {
    val s = String.format(Locale.US, "%.${digits}f", x)
    val t = if (s.contains('.')) s.trimEnd('0').trimEnd('.') else s
    return t.replace('.', ',')
}

fun formatFreq(hz: Float): Pair<String, String> {
    if (hz < 1000f) {
        return Pair(if (hz < 10f) nf(hz, 1) else nf(hz, 0), "Гц")
    }
    val k = hz / 1000f
    val text = when {
        k < 10f -> nf(k, 2)
        k < 100f -> nf(k, 1)
        else -> nf(k, 0)
    }
    return Pair(text, "кГц")
}

fun formatDb(v: Float): String {
    val sign = if (v > 0f) "+" else if (v < 0f) "−" else ""
    return sign + nf(abs(v), 1)
}

data class EqState(
    val gains: List<Float> = List(BAND_COUNT) { 0f },
    val preampDb: Float = 0f,
    val pro: Boolean = false,
    val activePresetId: String? = null,
    val enabled: Boolean = true
)
