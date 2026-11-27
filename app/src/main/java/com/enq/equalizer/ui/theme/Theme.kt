package com.enq.equalizer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun EnqTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = EnqColors.Amber,
            background = EnqColors.Bg,
            surface = EnqColors.Face,
            onBackground = EnqColors.Ink,
            onSurface = EnqColors.Ink
        ),
        content = content
    )
}
