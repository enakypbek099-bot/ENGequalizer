package com.enq.equalizer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enq.equalizer.eq.DB_RANGE
import com.enq.equalizer.eq.formatDb
import com.enq.equalizer.ui.theme.EnqColors
import com.enq.equalizer.ui.theme.EnqFonts
import kotlin.math.abs
import kotlin.math.roundToInt

val FaderTrackHeight = 220.dp
private val Pad = 17.dp

private fun yToValue(y: Float, height: Float, pad: Float): Float {
    val t = (1f - (y - pad) / (height - 2f * pad)).coerceIn(0f, 1f)
    val raw = -DB_RANGE + t * 2f * DB_RANGE
    return if (abs(raw) < 0.3f) 0f else (raw * 2f).roundToInt() / 2f
}

@Composable
fun Fader(
    value: Float,
    onValueChange: (Float) -> Unit,
    onTouch: () -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit
) {
    val change by rememberUpdatedState(onValueChange)
    val touch by rememberUpdatedState(onTouch)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.height(18.dp), contentAlignment = Alignment.Center) {
            Text(
                text = if (value == 0f) "0" else formatDb(value),
                color = if (value != 0f) EnqColors.Amber else EnqColors.Dim,
                fontFamily = EnqFonts.Mono,
                fontSize = 9.5.sp,
                maxLines = 1,
                softWrap = false
            )
        }
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(FaderTrackHeight)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { touch() },
                        onDoubleTap = { change(0f) },
                        onTap = { o -> change(yToValue(o.y, size.height.toFloat(), Pad.toPx())) }
                    )
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { touch() },
                        onVerticalDrag = { c, _ ->
                            c.consume()
                            change(yToValue(c.position.y, size.height.toFloat(), Pad.toPx()))
                        }
                    )
                }
        ) {
            val pad = Pad.toPx()
            val cx = size.width / 2f
            val top = pad
            val span = size.height - 2f * pad
            val railW = 5.dp.toPx()
            val railTop = top - 6.dp.toPx()
            val railH = span + 12.dp.toPx()
            drawRoundRect(Color(0xFF050506), Offset(cx - railW / 2f, railTop), Size(railW, railH), CornerRadius(2.dp.toPx()))
            drawRoundRect(Color.Black, Offset(cx - railW / 2f, railTop), Size(railW, railH), CornerRadius(2.dp.toPx()), style = Stroke(1.dp.toPx()))

            val levels = listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
            val cols = listOf(Color(0xFF6D6E74), Color(0xFF55565B), Color(0xFFC8C8C2), Color(0xFF55565B), Color(0xFF6D6E74))
            levels.forEachIndexed { i, l ->
                val y = top + span * l
                drawLine(cols[i], Offset(cx - 19.dp.toPx(), y), Offset(cx - 12.dp.toPx(), y), 1.dp.toPx())
                drawLine(cols[i], Offset(cx + 12.dp.toPx(), y), Offset(cx + 19.dp.toPx(), y), 1.dp.toPx())
            }

            val ky = top + span * (1f - (value + DB_RANGE) / (2f * DB_RANGE))
            val kw = 26.dp.toPx()
            val kh = 36.dp.toPx()
            val kx = cx - kw / 2f
            val kt = ky - kh / 2f
            val corner = CornerRadius(2.dp.toPx())
            drawRoundRect(Color(0xB3000000), Offset(kx, kt + 4.dp.toPx()), Size(kw, kh), corner)
            drawRoundRect(Color(0xFF1C1D21), Offset(kx, kt), Size(kw, kh), corner)
            var yy = kt + 1.dp.toPx()
            val end = kt + kh - 1.dp.toPx()
            while (yy < end) {
                drawRect(Color(0xFF26272C), Offset(kx + 1.dp.toPx(), yy), Size(kw - 2.dp.toPx(), 3.dp.toPx()))
                yy += 4.dp.toPx()
            }
            drawRect(
                Brush.horizontalGradient(
                    listOf(Color(0x8C000000), Color(0x12FFFFFF), Color(0x05FFFFFF), Color(0x59000000)),
                    startX = kx,
                    endX = kx + kw
                ),
                Offset(kx, kt),
                Size(kw, kh)
            )
            drawRoundRect(Color.Black, Offset(kx, kt), Size(kw, kh), corner, style = Stroke(1.dp.toPx()))
            drawRect(Color(0xFFECECE6), Offset(kx + 2.dp.toPx(), ky - 1.5.dp.toPx()), Size(kw - 4.dp.toPx(), 3.dp.toPx()))
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.height(28.dp), contentAlignment = Alignment.TopCenter) { label() }
    }
}

@Composable
fun FaderScale(modifier: Modifier = Modifier) {
    Column(modifier.width(28.dp)) {
        Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth().height(FaderTrackHeight)) {
            val span = FaderTrackHeight - Pad * 2
            listOf("+12", "+6", "0", "−6", "−12").forEachIndexed { k, t ->
                Text(
                    text = t,
                    color = EnqColors.Dim,
                    fontFamily = EnqFonts.Mono,
                    fontSize = 9.sp,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = Pad + span * (k * 0.25f) - 6.dp)
                        .padding(end = 4.dp)
                )
            }
        }
    }
}
