package com.younesbouh.videoplayer.settings.presentation.routes.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.younesbouh.videoplayer.main.presentation.util.stateInVM
import com.younesbouh.videoplayer.settings.data.SettingsDataStore
import com.younesbouh.videoplayer.settings.domain.models.Language

class LanguageViewModel(
    val dataStore: SettingsDataStore
): ViewModel() {
    val language = dataStore.language.stateInVM(Language.SYSTEM, viewModelScope)

    fun saveLanguage(language: Language) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.saveSettings(language = language.toString())
        }
    }
}