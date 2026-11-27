package com.enq.equalizer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.enq.equalizer.eq.EqService
import com.enq.equalizer.eq.EqStore
import com.enq.equalizer.eq.EqViewModel
import com.enq.equalizer.ui.screens.EqScreen
import com.enq.equalizer.ui.theme.EnqColors
import com.enq.equalizer.ui.theme.EnqFonts
import com.enq.equalizer.ui.theme.EnqTheme

class MainActivity : ComponentActivity() {

    private val notifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { startEngine() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnqTheme {
                val vm: EqViewModel = viewModel()
                val sessions by EqStore.sessions.collectAsStateWithLifecycle()
                val global by EqStore.global.collectAsStateWithLifecycle()
                val info by EqStore.info.collectAsStateWithLifecycle()
                Box(Modifier.fillMaxSize()) {
                    EqScreen(vm)
                    Text(
                        text = "Сессии: ${sessions.size} · Глобальный: ${if (global) "да" else "нет"}\n$info",
                        color = EnqColors.Amber,
                        fontSize = 10.sp,
                        fontFamily = EnqFonts.Mono,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .background(EnqColors.Lcd)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startEngine()
        }
    }

    private fun startEngine() {
        ContextCompat.startForegroundService(this, Intent(this, EqService::class.java))
    }
}
