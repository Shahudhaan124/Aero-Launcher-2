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
    private lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        appRepository = AppRepository(this)
        
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    PremiumHomeScreen(appRepository = appRepository)
                }
            }
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {}
}
