package com.younesbouh.videoplayer.core.data.models

import java.io.Serializable

data class Item(
    val id: Long,
    val title: String,
    val path: String,
    val album: String,
    val artist: String,
    val duration: Long,
): Serializable