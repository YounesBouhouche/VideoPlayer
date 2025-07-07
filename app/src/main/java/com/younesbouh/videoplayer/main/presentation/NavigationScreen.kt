package com.younesbouh.videoplayer.main.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.younesbouh.videoplayer.main.domain.events.UiEvent
import com.younesbouh.videoplayer.main.domain.models.NavRoutes
import com.younesbouh.videoplayer.main.domain.models.VideoCard
import com.younesbouh.videoplayer.main.presentation.routes.Favorites
import com.younesbouh.videoplayer.main.presentation.routes.FolderScreen
import com.younesbouh.videoplayer.main.presentation.routes.Folders
import com.younesbouh.videoplayer.main.presentation.routes.History
import com.younesbouh.videoplayer.main.presentation.routes.Library
import com.younesbouh.videoplayer.main.presentation.viewmodel.MainVM
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut

@Composable
fun NavigationScreen(
    navController: NavHostController,
    mainVM: MainVM,
    onLongClick: (VideoCard) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (VideoCard) -> Unit,
) {
    val files = mainVM.files.collectAsState().value
    val folders = mainVM.foldersState.collectAsState().value
    val folder = mainVM.folderSorted.collectAsState().value
    val favorites = mainVM.favorites.collectAsState().value
    val history = mainVM.history.collectAsState().value
    val libraryListState = rememberLazyListState()
    val foldersListState = rememberLazyListState()
    val folderListState = rememberLazyListState()
    val favoritesListState = rememberLazyListState()
    val historyListState = rememberLazyListState()
    val librarySortState = mainVM.librarySortState.collectAsState().value
    LaunchedEffect(librarySortState) {
        libraryListState.scrollToItem(0)
    }
    LaunchedEffect(folders) {
        foldersListState.scrollToItem(0)
    }
    LaunchedEffect(folder) {
        folderListState.scrollToItem(0)
    }
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Library,
        enterTransition = { materialFadeThroughIn() },
        exitTransition = { materialFadeThroughOut() },
        modifier = modifier.fillMaxSize(),
    ) {
        composable<NavRoutes.Library> {
            Library(
                files,
                libraryListState,
                onLongClick,
                onClick = onClick
            )
        }
        composable<NavRoutes.Folders> {
            Folders(
                folders,
                foldersListState,
                {}
            ) {
                navController.navigate(NavRoutes.Folder(it))
            }
        }
        composable<NavRoutes.Folder> { entry ->
            LaunchedEffect(entry) {
                mainVM.onUiEvent(UiEvent.LoadFolder(entry.toRoute<NavRoutes.Folder>().id))
            }
            folder?.let {
                FolderScreen(
                    it,
                    folderListState,
                    navController::navigateUp,
                    { mainVM.onUiEvent(UiEvent.ShowFolderBottomSheet) },
                    onLongClick,
                    onClick = onClick
                )
            } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(120.dp))
            }
        }
        composable<NavRoutes.Favorites> {
            Favorites(
                favorites,
                favoritesListState,
                onLongClick,
                onClick = onClick
            )
        }
        composable<NavRoutes.History> {
            History(
                history,
                historyListState,
                onLongClick,
                onClick = onClick
            )
        }
    }
}
