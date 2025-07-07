package com.younesbouh.videoplayer.di

import android.annotation.SuppressLint
import com.younesbouh.videoplayer.main.presentation.viewmodel.MainVM
import com.younesbouh.videoplayer.main.presentation.viewmodel.NavigationVM
import com.younesbouh.videoplayer.player.presentation.PlayerVM
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val viewModelsModule = module {
    viewModelOf(::MainVM)
    viewModelOf(::NavigationVM)
    viewModelOf(::PlayerVM)
}