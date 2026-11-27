package com.enq.equalizer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.enq.equalizer.ui.theme.EnqColors

@Composable
fun RackUnit(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(5.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    0f to Color(0xFF2A2B30),
                    0.06f to EnqColors.Face,
                    1f to Color(0xFF1F2025)
                )
            )
            .drawBehind {
                val ear = 12.dp.toPx()
                val edge = 1.dp.toPx()
                drawRect(EnqColors.Ear, Offset.Zero, Size(ear, size.height))
                drawRect(EnqColors.Ear, Offset(size.width - ear, 0f), Size(ear, size.height))
                drawRect(Color.Black, Offset(ear - edge, 0f), Size(edge, size.height))
                drawRect(Color.Black, Offset(size.width - ear, 0f), Size(edge, size.height))
            }
            .border(1.dp, Color.Black, shape)
            .padding(horizontal = 20.dp),
        content = content
    )
}

@Composable
fun RackSection(
    modifier: Modifier = Modifier,
    first: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier.fillMaxWidth()) {
        if (!first) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF0B0B0D)))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.05f)))
        }
        content()
    }
}
