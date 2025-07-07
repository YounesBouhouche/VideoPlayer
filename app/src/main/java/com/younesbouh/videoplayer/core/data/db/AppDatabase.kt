package com.younesbouh.videoplayer.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.younesbouh.videoplayer.core.data.dao.AppDao
import com.younesbouh.videoplayer.main.domain.models.ItemData

@Database(
    entities = [ItemData::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val dao: AppDao
}
