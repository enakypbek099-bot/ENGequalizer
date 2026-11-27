package com.enq.equalizer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.enq.equalizer.ui.theme.EnqColors

@Composable
fun ResetIcon(color: Color = EnqColors.Ink) {
    Canvas(Modifier.size(15.dp)) {
        val sw = 2.2f / 24f * size.width
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val inset = size.width * 0.125f
        drawArc(
            color = color,
            startAngle = 220f,
            sweepAngle = 280f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = Size(size.width - 2 * inset, size.height - 2 * inset),
            style = stroke
        )
        val p = Path().apply {
            moveTo(size.width * 0.12f, size.height * 0.16f)
            lineTo(size.width * 0.12f, size.height * 0.38f)
            lineTo(size.width * 0.34f, size.height * 0.38f)
        }
        drawPath(p, color, style = stroke)
    }
}

@Composable
fun MenuIcon(color: Color = EnqColors.Ink) {
    Canvas(Modifier.size(15.dp)) {
        val sw = 2.2f / 24f * size.width
        val w = size.width
        val h = size.height
        drawLine(color, Offset(w * 0.17f, h * 0.25f), Offset(w * 0.83f, h * 0.25f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.17f, h * 0.5f), Offset(w * 0.83f, h * 0.5f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.17f, h * 0.75f), Offset(w * 0.58f, h * 0.75f), sw, StrokeCap.Round)
    }
}

@Composable
fun BrandMark(dimension: Dp) {
    Canvas(Modifier.size(dimension)) {
        val k = this.size.width / 48f
        drawRoundRect(EnqColors.Amber, Offset(k, k), Size(46f * k, 46f * k), CornerRadius(6f * k))
        drawRoundRect(Color.Black, Offset(k, k), Size(46f * k, 46f * k), CornerRadius(6f * k), style = Stroke(2f * k))
        val dark = Color(0xFF141416)
        listOf(9f, 18f, 27f, 36f).forEach { x ->
            drawRoundRect(dark, Offset(x * k, 8f * k), Size(4f * k, 32f * k), CornerRadius(1f * k))
        }
        val handles = listOf(5.5f to 26f, 14.5f to 13f, 23.5f to 20f, 32.5f to 29f)
        handles.forEach { (x, y) ->
            drawRoundRect(Color(0xFFF4F4EE), Offset(x * k, y * k), Size(11f * k, 6f * k), CornerRadius(1f * k))
            drawRoundRect(Color.Black, Offset(x * k, y * k), Size(11f * k, 6f * k), CornerRadius(1f * k), style = Stroke(1.2f * k))
        }
    }
}
