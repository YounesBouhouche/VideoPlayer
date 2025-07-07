package com.younesbouh.videoplayer.main.domain.events

import com.younesbouh.videoplayer.main.domain.models.VideoCard

sealed interface FilesEvent {
    data object LoadFiles : FilesEvent

    data class AddFile(val file: VideoCard) : FilesEvent

    data class RemoveFile(val file: VideoCard) : FilesEvent
}
