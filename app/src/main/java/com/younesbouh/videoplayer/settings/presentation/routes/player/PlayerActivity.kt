package com.younesbouh.videoplayer.settings.presentation.routes.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.younesbouh.videoplayer.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

class PlayerActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = koinViewModel<PlayerSettingsViewModel>()
            AppTheme {
                PlayerSettingsScreen(viewModel, onBack = { finish() })
            }
        }
    }
}