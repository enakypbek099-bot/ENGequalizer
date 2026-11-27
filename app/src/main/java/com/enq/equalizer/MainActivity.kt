package com.enq.equalizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.enq.equalizer.ui.theme.EnqColors
import com.enq.equalizer.ui.theme.EnqTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnqTheme {
                Box(
                    modifier = Modifier.fillMaxSize().background(EnqColors.Bg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "ENQ", color = EnqColors.White, fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp)
                }
            }
        }
    }
}
