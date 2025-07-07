package com.younesbouh.videoplayer.main.presentation.states

import com.younesbouh.videoplayer.main.domain.models.VideoCard

data class UiState(
    val loading: Boolean = false,
    val bottomSheetVisible: Boolean = false,
    val foldersBottomSheetVisible: Boolean = false,
    val folderBottomSheetVisible: Boolean = false,
    val favoritesBottomSheetVisible: Boolean = false,
    val historyBottomSheetVisible: Boolean = false,
    val infoSheetVisible: Boolean = false,
    val infoSheetCard: VideoCard = VideoCard.Builder().build(),
)
