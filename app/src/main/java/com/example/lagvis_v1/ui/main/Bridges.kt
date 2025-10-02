package com.example.lagvis_v1.ui.main

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class Bridges {

    @Composable
    fun LaunchActivityButton(label: String, makeIntent: () -> Intent) {
        val ctx = LocalContext.current
        androidx.compose.material3.Button(onClick = { ctx.startActivity(makeIntent()) }) {
            androidx.compose.material3.Text(label)
        }
    }
}