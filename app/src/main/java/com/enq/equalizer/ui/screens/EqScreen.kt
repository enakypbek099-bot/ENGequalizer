package com.enq.equalizer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enq.equalizer.eq.BAND_COUNT
import com.enq.equalizer.eq.EqViewModel
import com.enq.equalizer.eq.PRO_PRESETS
import com.enq.equalizer.eq.Preset
import com.enq.equalizer.eq.STANDARD_PRESETS
import com.enq.equalizer.eq.bandFreq
import com.enq.equalizer.eq.formatDb
import com.enq.equalizer.eq.formatFreq
import com.enq.equalizer.eq.responseCurve
import com.enq.equalizer.ui.components.BrandMark
import com.enq.equalizer.ui.components.Fader
import com.enq.equalizer.ui.components.FaderScale
import com.enq.equalizer.ui.components.HwButton
import com.enq.equalizer.ui.components.LcdView
import com.enq.equalizer.ui.components.Led
import com.enq.equalizer.ui.components.MenuIcon
import com.enq.equalizer.ui.components.RackSection
import com.enq.equalizer.ui.components.RackUnit
import com.enq.equalizer.ui.components.ResetIcon
import com.enq.equalizer.ui.theme.EnqColors
import com.enq.equalizer.ui.theme.EnqFonts
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EqScreen(vm: EqViewModel) {
    val s by vm.state.collectAsStateWithLifecycle()
    var menuOpen by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }
    var roTitle by remember { mutableStateOf<String?>(null) }
    var roValue by remember { mutableStateOf<String?>(null) }
    val curve = remember(s.gains, s.pro) { responseCurve(s.gains, s.pro) }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(3200)
            toast = null
        }
    }

    Box(Modifier.fillMaxSize().background(EnqColors.Bg).systemBarsPadding()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp)) {
            RackUnit(Modifier.fillMaxWidth()) {
                RackSection(first = true) {
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BrandMark(38.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("ENQ", color = EnqColors.White, fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 4.4.sp)
                            Text("ЭКВАЛАЙЗЕР", color = EnqColors.Dim, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 3.2.sp)
                        }
                    }
                }

                RackSection {
                    FlowRow(
                        Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HwButton("PRO", onClick = { vm.setPro(!s.pro) }, led = s.pro, selected = s.pro)
                        HwButton("Пресеты", onClick = { menuOpen = !menuOpen }, selected = menuOpen, icon = { MenuIcon() })
                        HwButton(
                            "Сбросить",
                            onClick = {
                                vm.reset()
                                roTitle = null
                                roValue = null
                                menuOpen = false
                            },
                            icon = { ResetIcon() }
                        )
                    }
                    if (menuOpen) {
                        PresetMenu(
                            pro = s.pro,
                            activeId = s.activePresetId,
                            onPick = { p ->
                                if (p.pro && !s.pro) {
                                    toast = "Пресет работает только в режиме PRO. Нажмите кнопку PRO."
                                } else {
                                    vm.applyPreset(p)
                                    roTitle = p.name
                                    roValue = null
                                    menuOpen = false
                                }
                            }
                        )
                    }
                }

                RackSection {
                    Box(Modifier.padding(vertical = 12.dp)) {
                        LcdView(curve = curve, pro = s.pro, readoutTitle = roTitle, readoutValue = roValue)
                    }
                }

                RackSection {
                    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Box(
                            Modifier
                                .width(84.dp)
                                .drawBehind {
                                    val x = size.width - 1.dp.toPx()
                                    drawLine(Color.Black, Offset(x, 0f), Offset(x, size.height), 1.dp.toPx())
                                }
                                .padding(end = 8.dp)
                        ) {
                            Fader(
                                value = s.preampDb,
                                onValueChange = { v ->
                                    vm.setPreamp(v)
                                    roTitle = "Предусилитель"
                                    roValue = formatDb(v) + " дБ"
                                },
                                onTouch = {
                                    roTitle = "Предусилитель"
                                    roValue = formatDb(s.preampDb) + " дБ"
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "ПРЕДУСИЛИТЕЛЬ",
                                    color = EnqColors.Ink,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                        Spacer(Modifier.width(6.dp))
                        FaderScale()
                        Row(Modifier.weight(1f).horizontalScroll(rememberScrollState())) {
                            for (i in 0 until BAND_COUNT) {
                                val (n, u) = formatFreq(bandFreq(i, s.pro))
                                Fader(
                                    value = s.gains[i],
                                    onValueChange = { v ->
                                        vm.setGain(i, v)
                                        roTitle = "$n $u"
                                        roValue = formatDb(v) + " дБ"
                                    },
                                    onTouch = {
                                        roTitle = "$n $u"
                                        roValue = formatDb(s.gains[i]) + " дБ"
                                    },
                                    modifier = Modifier.width(34.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(n, color = EnqColors.Ink, fontSize = 10.sp, lineHeight = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                        Text(u.uppercase(), color = EnqColors.Dim, fontSize = 8.sp, lineHeight = 9.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val t = toast
        if (t != null) {
            val shape = RoundedCornerShape(3.dp)
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .clip(shape)
                    .background(Color(0xFF141518))
                    .border(1.dp, EnqColors.Amber, shape)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(t, color = EnqColors.Amber, fontFamily = EnqFonts.Mono, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun PresetMenu(pro: Boolean, activeId: String?, onPick: (Preset) -> Unit) {
    val shape = RoundedCornerShape(3.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .heightIn(max = 320.dp)
            .clip(shape)
            .background(Color(0xFF141518))
            .border(1.dp, Color.Black, shape)
            .verticalScroll(rememberScrollState())
            .padding(6.dp)
    ) {
        MenuHeader("Стандартные")
        STANDARD_PRESETS.forEach { MenuRow(it, pro, activeId, onPick) }
        MenuHeader("PRO")
        PRO_PRESETS.forEach { MenuRow(it, pro, activeId, onPick) }
    }
}

@Composable
private fun MenuHeader(text: String) {
    Text(
        text.uppercase(),
        color = EnqColors.Dim,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(start = 8.dp, top = 10.dp, bottom = 6.dp)
    )
}

@Composable
private fun MenuRow(p: Preset, pro: Boolean, activeId: String?, onPick: (Preset) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable { onPick(p) }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Led(p.id == activeId)
        Spacer(Modifier.width(10.dp))
        Text(
            p.name,
            color = if (p.pro && !pro) EnqColors.Dim else EnqColors.Ink,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        if (p.pro) {
            Text(
                "PRO",
                color = EnqColors.Amber,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.3.sp,
                modifier = Modifier
                    .border(1.dp, EnqColors.Amber.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            )
        }
    }
}
