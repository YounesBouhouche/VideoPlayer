package com.younesbouh.videoplayer.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.younesbouh.videoplayer.main.domain.models.ItemData
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Upsert
    suspend fun upsertItem(item: ItemData)

    @Query("UPDATE ItemData SET lastPlayed=:lastPlayed WHERE id=:id")
    suspend fun updateLastPlayed(id: Long, lastPlayed: Long)

    // Suspend function to check if the item is already in the database
    @Query("SELECT * from ItemData WHERE id=:id")
    suspend fun getItem(id: Long): ItemData?

    @Query("UPDATE ItemData SET favorite=:favorite WHERE id=:id")
    suspend fun updateFavorite(id: Long, favorite: Boolean)

    @Query("UPDATE ItemData SET progress=:progress WHERE id=:id")
    suspend fun updateProgress(id: Long, progress: Float?)

    @Query("SELECT * from ItemData")
    fun getItemsData(): Flow<List<ItemData>>

    @Query("SELECT * from ItemData WHERE lastPlayed IS NOT NULL")
    fun getHistory(): Flow<List<ItemData>>

    @Query("SELECT * from ItemData WHERE favorite=true")
    fun getFavorites(): Flow<List<ItemData>>

    @Query("SELECT * from ItemData WHERE favorite=true")
    suspend fun suspendGetFavorites(): List<ItemData>

    @Query("SELECT favorite from ItemData WHERE id=:id")
    fun getFavorite(id: Long): Flow<Boolean?>

    @Query("SELECT favorite from ItemData WHERE id=:id")
    suspend fun suspendGetFavorite(id: Long): Boolean?
}
