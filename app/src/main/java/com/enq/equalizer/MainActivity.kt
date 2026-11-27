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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.enq.equalizer.eq.EqService
import com.enq.equalizer.eq.EqViewModel
import com.enq.equalizer.ui.screens.EqScreen
import com.enq.equalizer.ui.screens.SettingsPanel
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
                var settingsOpen by rememberSaveable { mutableStateOf(false) }
                Box(Modifier.fillMaxSize()) {
                    EqScreen(vm)
                    Text(
                        text = "НАСТРОЙКИ",
                        color = EnqColors.Dim,
                        fontSize = 10.sp,
                        fontFamily = EnqFonts.Mono,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .navigationBarsPadding()
                            .clickable { settingsOpen = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                    if (settingsOpen) SettingsPanel { settingsOpen = false }
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
