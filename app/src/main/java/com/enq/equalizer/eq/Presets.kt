package com.enq.equalizer.eq

import kotlin.math.ln
import kotlin.math.roundToInt

data class Preset(
    val id: String,
    val name: String,
    val pro: Boolean,
    val points: List<Pair<Float, Float>>
)

private fun pts(vararg v: Pair<Int, Int>): List<Pair<Float, Float>> =
    v.map { it.first.toFloat() to it.second.toFloat() }

val STANDARD_PRESETS: List<Preset> = listOf(
    Preset("s0", "Плоский", false, pts(20 to 0)),
    Preset("s1", "Усиление баса", false, pts(20 to 6, 60 to 8, 120 to 6, 250 to 2, 500 to 0, 20000 to 0)),
    Preset("s2", "Усиление высоких", false, pts(20 to 0, 2000 to 0, 4000 to 2, 8000 to 5, 16000 to 7, 20000 to 7)),
    Preset("s3", "Вокал", false, pts(20 to -4, 80 to -3, 200 to -1, 500 to 1, 1000 to 3, 3000 to 4, 5000 to 2, 8000 to 0, 20000 to -2)),
    Preset("s4", "Рок", false, pts(20 to 4, 60 to 5, 150 to 3, 400 to -1, 1000 to -1, 2500 to 1, 5000 to 3, 10000 to 4, 20000 to 4)),
    Preset("s5", "Поп", false, pts(20 to -2, 100 to 2, 300 to 3, 1000 to 2, 3000 to 0, 6000 to -1, 10000 to 1, 20000 to 2)),
    Preset("s6", "Джаз", false, pts(20 to 3, 100 to 3, 300 to 1, 800 to -1, 2000 to 0, 5000 to 2, 10000 to 3, 20000 to 3)),
    Preset("s7", "Классика", false, pts(20 to 2, 100 to 1, 500 to 0, 2000 to 0, 5000 to -1, 8000 to -2, 12000 to 0, 20000 to 1)),
    Preset("s8", "Электроника", false, pts(20 to 6, 50 to 6, 120 to 3, 400 to -1, 1000 to -2, 3000 to 0, 6000 to 3, 12000 to 5, 20000 to 5)),
    Preset("s9", "Кино", false, pts(20 to 5, 60 to 6, 200 to 1, 1000 to 0, 3000 to 2, 6000 to 1, 12000 to 3, 20000 to 3))
)

val PRO_PRESETS: List<Preset> = listOf(
    Preset("p0", "Саб-бас", true, pts(5 to 6, 10 to 9, 20 to 9, 40 to 6, 80 to 2, 160 to 0, 20000 to 0)),
    Preset("p1", "Глубокий бас", true, pts(5 to 3, 15 to 7, 30 to 10, 60 to 8, 120 to 4, 250 to 1, 500 to 0, 20000 to 0)),
    Preset("p2", "Кинозал", true, pts(5 to 7, 12 to 8, 30 to 6, 80 to 3, 250 to 0, 1000 to 0, 3000 to 2, 8000 to 3, 16000 to 4, 20000 to 4)),
    Preset("p3", "Студийный", true, pts(5 to -12, 10 to -10, 18 to -5, 30 to 0, 20000 to 0)),
    Preset("p4", "Мастеринг", true, pts(5 to -10, 15 to -6, 30 to 0, 100 to 1, 400 to -1, 2000 to 0, 6000 to 1, 12000 to 2, 20000 to 2)),
    Preset("p5", "Hi-Fi", true, pts(5 to -6, 20 to 0, 60 to 2, 250 to 0, 1000 to 0, 4000 to 1, 10000 to 3, 20000 to 4)),
    Preset("p6", "Воздух", true, pts(5 to -6, 30 to 0, 1000 to 0, 5000 to 2, 10000 to 5, 16000 to 8, 20000 to 9)),
    Preset("p7", "V-образный", true, pts(5 to 5, 15 to 8, 40 to 7, 120 to 2, 400 to -3, 1000 to -4, 3000 to -1, 8000 to 5, 16000 to 8, 20000 to 8)),
    Preset("p8", "Клуб", true, pts(5 to 4, 10 to 6, 30 to 9, 60 to 8, 120 to 4, 400 to -1, 1500 to -2, 5000 to 2, 12000 to 4, 20000 to 4)),
    Preset("p9", "Наушники", true, pts(5 to -4, 20 to 2, 60 to 3, 200 to 0, 1000 to 0, 3000 to -2, 6000 to -3, 10000 to 0, 16000 to 3, 20000 to 4))
)

val ALL_PRESETS: List<Preset> = STANDARD_PRESETS + PRO_PRESETS

fun presetById(id: String?): Preset? = ALL_PRESETS.firstOrNull { it.id == id }

private fun gainAt(points: List<Pair<Float, Float>>, f: Float): Float {
    if (f <= points.first().first) return points.first().second
    for (k in 1 until points.size) {
        if (f <= points[k].first) {
            val (f0, g0) = points[k - 1]
            val (f1, g1) = points[k]
            return g0 + (g1 - g0) * ln(f / f0) / ln(f1 / f0)
        }
    }
    return points.last().second
}

fun Preset.toGains(pro: Boolean): List<Float> =
    List(BAND_COUNT) { i ->
        val raw = gainAt(points, bandFreq(i, pro))
        val q = (raw * 2f).roundToInt() / 2f
        q.coerceIn(-DB_RANGE, DB_RANGE) + 0f
    }
