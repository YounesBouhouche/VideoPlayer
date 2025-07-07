package com.younesbouh.videoplayer.main.presentation.util

import com.younesbouh.videoplayer.main.domain.models.VideoCard

fun List<VideoCard>.toMediaItems() = map { it.toMediaItem() }
