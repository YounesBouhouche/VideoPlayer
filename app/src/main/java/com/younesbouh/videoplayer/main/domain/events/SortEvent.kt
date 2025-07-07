package com.younesbouh.videoplayer.main.domain.events

import com.younesbouh.videoplayer.main.presentation.states.FoldersSortType
import com.younesbouh.videoplayer.main.presentation.states.HistorySortType
import com.younesbouh.videoplayer.main.presentation.states.SortState
import com.younesbouh.videoplayer.main.presentation.states.SortType

sealed interface SortEvent {
    data class UpdateSortState(val sortState: SortState<SortType>) : SortEvent
    data class UpdateFoldersSortState(val sortState: SortState<FoldersSortType>) : SortEvent
    data class UpdateFolderSortState(val sortState: SortState<SortType>) : SortEvent
    data class UpdateFavoritesSortState(val sortState: SortState<SortType>) : SortEvent
    data class UpdateHistorySortState(val sortState: SortState<HistorySortType>) : SortEvent
}
