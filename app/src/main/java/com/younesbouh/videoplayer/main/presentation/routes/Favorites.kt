package com.younesbouh.videoplayer.main.presentation.routes

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.younesbouh.videoplayer.main.domain.models.VideoCard

@Composable
fun Favorites(
    files: List<VideoCard>,
    state: LazyListState,
    onLongClick: (VideoCard) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (VideoCard) -> Unit,
) = Library(files, state, onLongClick, modifier, onClick)