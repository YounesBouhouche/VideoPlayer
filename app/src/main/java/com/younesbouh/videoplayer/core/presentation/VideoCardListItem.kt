package com.younesbouh.videoplayer.core.presentation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import com.younesbouh.videoplayer.main.domain.models.VideoCard
import com.younesbouh.videoplayer.main.presentation.util.sizeString
import com.younesbouh.videoplayer.main.presentation.util.timeString

@Composable
fun VideoCardListItem(
    file: VideoCard,
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    trailingContent: @Composable RowScope.() -> Unit = {},
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    MyListItem(
        modifier = modifier,
        background = background,
        onClick = onClick,
        onLongClick = onLongClick,
        headline = file.title,
        supporting = file.size.sizeString,
        progress = file.progress,
        cover = file.cover?.asImageBitmap(),
        time = file.duration,
        trailingContent = trailingContent,
    )
}