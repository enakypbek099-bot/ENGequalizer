package com.enq.equalizer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enq.equalizer.eq.EqStore
import com.enq.equalizer.eq.TestNoise
import com.enq.equalizer.ui.theme.EnqColors
import com.enq.equalizer.ui.theme.EnqFonts

@Composable
fun SettingsPanel(onClose: () -> Unit) {
    val mode by EqStore.mode.collectAsStateWithLifecycle()
    val sessions by EqStore.sessions.collectAsStateWithLifecycle()
    val global by EqStore.global.collectAsStateWithLifecycle()
    val info by EqStore.info.collectAsStateWithLifecycle()
    val playing by TestNoise.playing.collectAsStateWithLifecycle()

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xCC000000))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {}
    ) {
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .background(EnqColors.Face)
                .padding(20.dp)
        ) {
            Text("НАСТРОЙКИ", color = EnqColors.White, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)

            Text("РЕЖИМ ЗВУКА", color = EnqColors.Dim, fontSize = 11.sp, letterSpacing = 2.sp, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
            ModeRow("Классический (полосы устройства)", mode == 1) { EqStore.mode.value = 1 }
            ModeRow("Динамический (32 полосы)", mode == 0) { EqStore.mode.value = 0 }
            ModeRow("Выключено", mode == 2) { EqStore.mode.value = 2 }

            Text(
                if (playing) "СТОП ТЕСТ" else "ТЕСТ ШУМ",
                color = EnqColors.Amber,
                fontSize = 14.sp,
                fontFamily = EnqFonts.Mono,
                modifier = Modifier
                    .padding(top = 18.dp)
                    .clickable { TestNoise.toggle() }
                    .padding(vertical = 8.dp)
            )

            Text(
                "Сессии: ${sessions.size} · Глобальный: ${if (global) "да" else "нет"}\n$info",
                color = EnqColors.Dim,
                fontSize = 10.sp,
                fontFamily = EnqFonts.Mono,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                "ЗАКРЫТЬ",
                color = EnqColors.Ink,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .padding(top = 14.dp)
                    .clickable { onClose() }
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ModeRow(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        (if (selected) "●  " else "○  ") + text,
        color = if (selected) EnqColors.Amber else EnqColors.Ink,
        fontSize = 13.sp,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 7.dp)
    )
}
