package com.younesbouh.videoplayer.di

import android.annotation.SuppressLint
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val playerModule = module {
    single<TrackSelector> {
        DefaultTrackSelector(androidContext())
    }

    single<Player> {
        ExoPlayer.Builder(androidApplication())
            .setHandleAudioBecomingNoisy(true)
            .setAudioAttributes(
                AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).build(),
                true,
            )
            .setTrackSelector(get())
            .build()
    }
}