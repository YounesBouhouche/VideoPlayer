package com.younesbouh.videoplayer.main.presentation.routes

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.younesbouh.videoplayer.core.presentation.FolderListItem
import com.younesbouh.videoplayer.main.domain.models.Folder

@Composable
fun Folders(
    folders: List<Folder>,
    state: LazyListState,
    onLongClick: (Folder) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    val view = LocalView.current
    val isCompact =
        currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    val insets = if (isCompact) WindowInsets.statusBars else WindowInsets.systemBars
    LazyColumn(
        modifier,
        state = state,
        contentPadding = insets.add(WindowInsets(top = 84.dp, left = 8.dp, right = 8.dp)).asPaddingValues()
    ) {
        items(folders, { it.id }) {
            FolderListItem(
                folder = it,
                onLongClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onLongClick(it)
                },
                modifier = Modifier.animateItem()
            ) {
                onClick(it.id)
            }
        }
    }
}
