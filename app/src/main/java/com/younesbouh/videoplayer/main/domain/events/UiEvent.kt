package com.younesbouh.videoplayer.main.domain.events

import com.younesbouh.videoplayer.main.domain.models.VideoCard

sealed interface UiEvent {
    data object ShowBottomSheet: UiEvent
    data object HideBottomSheet: UiEvent
    data class ShowInfoSheet(val card: VideoCard): UiEvent
    data object HideInfoSheet: UiEvent
    data object ToggleFavorite: UiEvent
    data object ShowFoldersBottomSheet: UiEvent
    data object HideFoldersBottomSheet: UiEvent
    data object ShowFolderBottomSheet: UiEvent
    data object HideFolderBottomSheet: UiEvent
    data object ShowFavoritesBottomSheet: UiEvent
    data object HideFavoritesBottomSheet: UiEvent
    data object ShowHistoryBottomSheet: UiEvent
    data object HideHistoryBottomSheet: UiEvent
    data class LoadFolder(val id: Int): UiEvent
}
