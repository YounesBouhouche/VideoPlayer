package com.younesbouh.videoplayer.settings.presentation.routes.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.younesbouh.videoplayer.core.data.PlayerDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.younesbouh.videoplayer.main.presentation.util.stateInVM

class PlaybackSettingsViewModel(
    private val dataStore: PlayerDataStore
): ViewModel() {
    val rememberSpeed = dataStore.rememberSpeed.stateInVM(false, viewModelScope)
    val rememberPitch = dataStore.rememberPitch.stateInVM(false, viewModelScope)

    fun saveSettings(
        rememberSpeed: Boolean?  = null,
        rememberPitch: Boolean?  = null,
        ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.saveSettings(
                rememberSpeed = rememberSpeed,
                rememberPitch = rememberPitch
            )
        }
    }
}