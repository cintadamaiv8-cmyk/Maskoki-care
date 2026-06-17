package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.MaskokiRepository
import com.example.ui.MaskokiApp
import com.example.ui.MaskokiViewModel
import com.example.ui.MaskokiViewModelFactory
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.DarkBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0)
        }

        val repository = MaskokiRepository.getInstance(applicationContext)
        val viewModel: MaskokiViewModel by viewModels { MaskokiViewModelFactory(repository, applicationContext) }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    MaskokiApp(viewModel)
                }
            }
        }
    }
}
