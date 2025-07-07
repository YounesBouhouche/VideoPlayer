package com.younesbouh.videoplayer.main.presentation.states

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector
import com.younesbouh.videoplayer.R

data class SortState<T>(
    val sortType: T,
    val expanded: Boolean = false,
    val ascending: Boolean = true,
)

enum class SortType(val label: Int, val icon: ImageVector) {
    Title(R.string.title, Icons.Default.Title),
    Filename(R.string.file_name, Icons.AutoMirrored.Default.InsertDriveFile),
    Duration(R.string.duration, Icons.Default.Timer),
    Size(R.string.size, Icons.Default.Straighten),
    Date(R.string.date_modified, Icons.Default.CalendarMonth),
}

enum class FoldersSortType(val label: Int, val icon: ImageVector) {
    Name(R.string.name, Icons.Default.Title),
    Size(R.string.size, Icons.Default.Straighten),
    ItemsCount(R.string.items_count, Icons.Default.FileCopy)
}

enum class HistorySortType(val label: Int, val icon: ImageVector) {
    LastPlayed(R.string.last_played, Icons.Default.History),
    Title(R.string.title, Icons.Default.Title),
    Filename(R.string.file_name, Icons.AutoMirrored.Default.InsertDriveFile),
    Duration(R.string.duration, Icons.Default.Timer),
    Size(R.string.size, Icons.Default.Straighten),
    Date(R.string.date_modified, Icons.Default.CalendarMonth),
}