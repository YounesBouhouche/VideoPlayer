package com.younesbouh.videoplayer.main.presentation.states

import com.younesbouh.videoplayer.main.domain.models.VideoCard

data class SearchState(
    val query: String = "",
    val result: List<VideoCard> = emptyList(),
    val expanded: Boolean = false,
)
