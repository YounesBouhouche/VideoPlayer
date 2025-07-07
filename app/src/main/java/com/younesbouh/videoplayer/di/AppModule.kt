package com.younesbouh.videoplayer.di

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import androidx.room.Room
import com.younesbouh.videoplayer.core.data.PlayerDataStore
import com.younesbouh.videoplayer.core.data.db.AppDatabase
import com.younesbouh.videoplayer.settings.data.SettingsDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java, "database.db"
        ).build()
    }

    single {
        SettingsDataStore(androidContext())
    }

    single {
        PlayerDataStore(androidContext())
    }

    single<Array<String>> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            arrayOf(READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(READ_MEDIA_VIDEO)
        } else {
            arrayOf(READ_EXTERNAL_STORAGE)
        }
    }
}
