package com.enq.equalizer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enq.equalizer.ui.theme.EnqColors
import com.enq.equalizer.ui.theme.EnqFonts

@Composable
fun Led(on: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier.size(9.dp)) {
        val r = size.minDimension / 2f
        if (on) drawCircle(EnqColors.Amber.copy(alpha = 0.35f), radius = r * 1.9f)
        drawCircle(Color.Black, radius = r)
        drawCircle(if (on) EnqColors.Amber else Color(0xFF3A2410), radius = r - 1.dp.toPx())
    }
}

@Composable
fun HwButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    led: Boolean? = null,
    selected: Boolean = false,
    icon: (@Composable () -> Unit)? = null
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val inset = pressed || selected
    val shape = RoundedCornerShape(3.dp)
    val brush = if (inset) {
        Brush.verticalGradient(listOf(Color(0xFF1C1D21), Color(0xFF202126)))
    } else {
        Brush.verticalGradient(listOf(EnqColors.Key, Color(0xFF2A2B30)))
    }
    Box(
        modifier = modifier
            .offset(y = if (pressed) 2.dp else 0.dp)
            .height(34.dp)
            .drawBehind {
                if (!inset) {
                    drawRoundRect(
                        Color.Black,
                        topLeft = Offset(0f, 2.dp.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(3.dp.toPx())
                    )
                }
            }
            .clip(shape)
            .background(brush)
            .border(1.dp, Color.Black, shape)
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (led != null) Led(led)
            if (icon != null) icon()
            Text(
                text = text.uppercase(),
                color = if (selected) EnqColors.White else EnqColors.Ink,
                fontFamily = EnqFonts.Sans,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}
