package com.example.lagvis_v1.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.lagvis_v1.ui.theme.LagVis_V1Theme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val wsc = calculateWindowSizeClass(this)

            LagVis_V1Theme(
                dynamicColor = false // para que no te pise la paleta en Android 12+
            ) {
                AppNavHostAdaptive(windowSizeClass = wsc)
            }
        }
    }
}
