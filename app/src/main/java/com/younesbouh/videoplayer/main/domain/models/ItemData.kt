package com.younesbouh.videoplayer.main.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemData(
    @PrimaryKey
    val id: Long,
    val favorite: Boolean = false,
    val lastPlayed: Long? = null,
    val progress: Float? = null,
)
