package com.younesbouh.videoplayer.settings.presentation.routes.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.younesbouh.videoplayer.core.data.PlayerDataStore
import kotlinx.coroutines.launch
import com.younesbouh.videoplayer.main.presentation.util.stateInVM

class PlayerSettingsViewModel(val dataStore: PlayerDataStore): ViewModel() {
    val showVolumeSlider = dataStore.showVolumeSlider.stateInVM(false, viewModelScope)
    val showPitchButton = dataStore.showPitch.stateInVM(false, viewModelScope)

    fun saveSettings(
        rememberRepeat: Boolean? = null,
        rememberShuffle: Boolean? = null,
        rememberSpeed: Boolean? = null,
        rememberPitch: Boolean? = null,
        showVolumeSlider: Boolean? = null,
        showPitchButton: Boolean? = null,
    ) {
        viewModelScope.launch {
            dataStore.saveSettings(
                rememberRepeat,
                rememberShuffle,
                rememberSpeed,
                rememberPitch,
                showVolumeSlider,
                showPitchButton,
            )
        }
    }
}