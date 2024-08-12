package com.example.kongsikeretariders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.kongsikeretariders.ui.theme.KongsiKeretaRiderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KongsiKeretaRiderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KongsiKeretaRiderApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
