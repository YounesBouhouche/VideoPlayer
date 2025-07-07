package com.younesbouh.videoplayer.main.domain.models

data class Folder(
    val id: Int,
    val name: String,
    val files: List<VideoCard>
)
