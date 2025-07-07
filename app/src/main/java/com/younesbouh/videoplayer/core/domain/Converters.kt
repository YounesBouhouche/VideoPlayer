package com.younesbouh.videoplayer.core.domain

import com.younesbouh.videoplayer.core.data.models.Item
import com.younesbouh.videoplayer.main.domain.models.VideoCard

fun VideoCard.toItem() = Item(
    id = id,
    title = title,
    path = path,
    album = album,
    artist = artist,
    duration = duration,
)