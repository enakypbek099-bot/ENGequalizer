package com.enq.equalizer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enq.equalizer.eq.DB_RANGE
import com.enq.equalizer.eq.HIGH_HZ
import com.enq.equalizer.eq.lowHz
import com.enq.equalizer.ui.theme.EnqColors
import com.enq.equalizer.ui.theme.EnqFonts
import kotlin.math.ln

private val TICKS = listOf(5f, 10f, 20f, 50f, 100f, 200f, 500f, 1000f, 2000f, 5000f, 10000f, 20000f)

private fun tickLabel(f: Float): String =
    if (f >= 1000f) "${(f / 1000f).toInt()}к" else "${f.toInt()}"

@Composable
fun LcdView(
    curve: FloatArray,
    pro: Boolean,
    readoutTitle: String?,
    readoutValue: String?,
    modifier: Modifier = Modifier
) {
    val measurer = rememberTextMeasurer()
    val tickStyle = TextStyle(
        fontFamily = EnqFonts.Mono,
        fontSize = 9.sp,
        color = EnqColors.Amber.copy(alpha = 0.55f)
    )
    val shape = RoundedCornerShape(3.dp)
    Box(
        modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(shape)
            .background(EnqColors.Lcd)
            .border(3.dp, EnqColors.Ear, shape)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val hTotal = size.height
            val lo = lowHz(pro)
            val padT = 30.dp.toPx()
            val padB = 16.dp.toPx()
            val h = hTotal - padT - padB
            fun xOf(f: Float) = ln(f / lo) / ln(HIGH_HZ / lo) * w
            fun yOf(d: Float) = padT + (1f - (d.coerceIn(-DB_RANGE, DB_RANGE) + DB_RANGE) / (2f * DB_RANGE)) * h

            for (d in listOf(-12f, -6f, 0f, 6f, 12f)) {
                val a = if (d == 0f) 0.30f else 0.10f
                drawLine(EnqColors.Amber.copy(alpha = a), Offset(0f, yOf(d)), Offset(w, yOf(d)), 1.dp.toPx())
            }
            for (f in TICKS) {
                if (f < lo || f > HIGH_HZ) continue
                val xx = xOf(f)
                drawLine(EnqColors.Amber.copy(alpha = 0.08f), Offset(xx, padT), Offset(xx, padT + h), 1.dp.toPx())
                val lay = measurer.measure(tickLabel(f), tickStyle)
                val tx = (xx - lay.size.width / 2f).coerceIn(6.dp.toPx(), w - lay.size.width - 6.dp.toPx())
                drawText(lay, topLeft = Offset(tx, hTotal - padB + 2.dp.toPx()))
            }

            val path = Path()
            for (j in curve.indices) {
                val xx = j / (curve.size - 1f) * w
                val yy = yOf(curve[j])
                if (j == 0) path.moveTo(xx, yy) else path.lineTo(xx, yy)
            }
            drawPath(path, EnqColors.White, style = Stroke(1.6.dp.toPx(), join = StrokeJoin.Round))
        }
        if (readoutTitle != null) {
            Row(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 8.dp)
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, EnqColors.AmberFaint)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(readoutTitle, color = EnqColors.Amber, fontFamily = EnqFonts.Mono, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                if (readoutValue != null) {
                    Text("  $readoutValue", color = EnqColors.Amber, fontFamily = EnqFonts.Mono, fontSize = 11.sp)
                }
            }
        }
        Text(
            text = if (pro) "5 Гц – 20 кГц" else "20 Гц – 20 кГц",
            color = EnqColors.Amber,
            fontFamily = EnqFonts.Mono,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 10.dp, top = 8.dp)
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f))
                .border(1.dp, EnqColors.AmberFaint)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
