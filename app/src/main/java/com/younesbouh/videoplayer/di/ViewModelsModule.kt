package com.younesbouh.videoplayer.di

import android.annotation.SuppressLint
import com.younesbouh.videoplayer.main.presentation.viewmodel.MainVM
import com.younesbouh.videoplayer.main.presentation.viewmodel.NavigationVM
import com.younesbouh.videoplayer.player.presentation.PlayerVM
import com.younesbouh.videoplayer.settings.presentation.routes.language.LanguageViewModel
import com.younesbouh.videoplayer.settings.presentation.routes.playback.PlaybackSettingsViewModel
import com.younesbouh.videoplayer.settings.presentation.routes.player.PlayerSettingsViewModel
import com.younesbouh.videoplayer.settings.presentation.routes.theme.ThemeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val viewModelsModule = module {
    viewModelOf(::MainVM)
    viewModelOf(::NavigationVM)
    viewModelOf(::PlayerVM)
    viewModelOf(::LanguageViewModel)
    viewModelOf(::PlaybackSettingsViewModel)
    viewModelOf(::PlayerSettingsViewModel)
    viewModelOf(::ThemeViewModel)
}