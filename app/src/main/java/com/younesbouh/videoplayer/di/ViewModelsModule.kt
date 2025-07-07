package com.younesbouh.videoplayer.di

import android.annotation.SuppressLint
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.younesbouh.videoplayer.main.presentation.viewmodel.MainVM
import com.younesbouh.videoplayer.main.presentation.viewmodel.NavigationVM
import com.younesbouh.videoplayer.player.presentation.PlayerVM
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val viewModelsModule = module {
    viewModelOf(::MainVM)
    viewModelOf(::NavigationVM)
    viewModelOf(::PlayerVM)
}