package com.younesbouh.videoplayer.main.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import com.younesbouh.videoplayer.R

enum class Routes(val title: Int, val filledIcon: ImageVector, val icon: ImageVector, val destination: NavRoutes, val index: Int) {
    Library(R.string.library, Icons.Default.VideoLibrary, Icons.Outlined.VideoLibrary, NavRoutes.Library, 0),
    Folders(R.string.folders, Icons.Default.Folder, Icons.Outlined.Folder, NavRoutes.Folders, 1),
    Favorites(R.string.favorites, Icons.Default.Favorite, Icons.Default.FavoriteBorder, NavRoutes.Favorites, 2),
    History(R.string.history, Icons.Default.History, Icons.Outlined.History, NavRoutes.History, 3),
}
