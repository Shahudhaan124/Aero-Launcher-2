package com.example.appsorter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    // UI placeholder so it compiles cleanly
                    androidx.compose.material3.Text(
                        text = "Launcher Active", 
                        color = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {}
}
