package com.younesbouh.videoplayer.main.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.younesbouh.videoplayer.core.presentation.util.composables.statusBarHeight
import com.younesbouh.videoplayer.main.domain.events.FilesEvent
import com.younesbouh.videoplayer.main.domain.events.SortEvent
import com.younesbouh.videoplayer.main.domain.events.UiEvent
import com.younesbouh.videoplayer.main.domain.models.Routes
import com.younesbouh.videoplayer.main.domain.models.VideoCard
import com.younesbouh.videoplayer.main.presentation.viewmodel.MainVM
import com.younesbouh.videoplayer.welcome.presentation.WelcomeScreen

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun AppScreen(
    granted: Boolean,
    onPermissionRequest: () -> Unit,
    mainVM: MainVM,
    navController: NavHostController,
    isParent: Boolean,
    navigationState: Int,
    play: (VideoCard) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.let { route ->
        Routes.entries
            .firstOrNull { it.destination.javaClass.kotlin.qualifiedName?.contains(route) == true }
    } ?: Routes.Library
    val uiState = mainVM.uiState.collectAsState().value
    val sortState = mainVM.librarySortState.collectAsState().value
    val foldersSortState = mainVM.foldersSortState.collectAsState().value
    val folderSortState = mainVM.folderSortState.collectAsState().value
    val favoritesSortState = mainVM.favoritesSortState.collectAsState().value
    val historySortState = mainVM.historySortState.collectAsState().value
    val searchState = mainVM.searchState.collectAsState().value
    val pullToRefreshState = rememberPullToRefreshState()
    val threshold = statusBarHeight + 24.dp
    AnimatedContent(targetState = granted, label = "") { isGranted ->
        if (isGranted) {
            Surface(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .semantics {
                            testTagsAsResourceId = true
                        }
                        .pullToRefresh(
                            state = pullToRefreshState,
                            enabled = isParent,
                            isRefreshing = uiState.loading,
                            onRefresh = {
                                mainVM.onFilesEvent(FilesEvent.LoadFiles)
                            },
                            threshold = threshold
                        )
            ) {
                Box(Modifier.fillMaxSize()) {
                    NavigationContainer(
                        navigationBarVisible = isParent and !searchState.expanded,
                        navigationBar = {
                            NavBar(state = navigationState) {
                                navController.navigate(it) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            NavigationScreen(
                                navController,
                                mainVM,
                                { mainVM.onUiEvent(UiEvent.ShowInfoSheet(it)) },
                                Modifier.fillMaxSize(),
                                play
                            )
                            AnimatedVisibility(
                                visible = isParent,
                                enter = slideInVertically { -it },
                                exit = slideOutVertically { -it }
                            ) {
                                SearchScreen(
                                    searchState,
                                    false,
                                    mainVM::onSearchEvent,
                                    {
                                        IconButton({
                                            mainVM.onUiEvent(
                                                when (currentRoute) {
                                                    Routes.Library -> UiEvent.ShowBottomSheet
                                                    Routes.Folders -> UiEvent.ShowFoldersBottomSheet
                                                    Routes.Favorites -> UiEvent.ShowFavoritesBottomSheet
                                                    Routes.History -> UiEvent.ShowHistoryBottomSheet
                                                }
                                            )
                                        }) {
                                            Icon(Icons.AutoMirrored.Default.Sort, null)
                                        }
                                    },
                                    {
                                        mainVM.onUiEvent(UiEvent.ShowInfoSheet(it))
                                    },
                                    play
                                )
                            }
                        }
                    }
                    Indicator(
                        pullToRefreshState,
                        uiState.loading,
                        Modifier.align(Alignment.TopCenter),
                        threshold = threshold
                    )
                }
            }
        } else {
            WelcomeScreen(onPermissionRequest)
        }
    }
    LibraryBottomSheet(
        uiState.bottomSheetVisible,
        { mainVM.onUiEvent(UiEvent.HideBottomSheet) },
        sortState,
        { mainVM.onSortEvent(SortEvent.UpdateSortState(it)) }
    )
    FoldersBottomSheet(
        uiState.foldersBottomSheetVisible,
        { mainVM.onUiEvent(UiEvent.HideFoldersBottomSheet) },
        foldersSortState,
        { mainVM.onSortEvent(SortEvent.UpdateFoldersSortState(it)) }
    )
    FolderBottomSheet(
        uiState.folderBottomSheetVisible,
        { mainVM.onUiEvent(UiEvent.HideFolderBottomSheet) },
        folderSortState,
        { mainVM.onSortEvent(SortEvent.UpdateFolderSortState(it)) }
    )
    FavoritesBottomSheet(
        uiState.favoritesBottomSheetVisible,
        { mainVM.onUiEvent(UiEvent.HideFavoritesBottomSheet) },
        favoritesSortState,
        { mainVM.onSortEvent(SortEvent.UpdateFavoritesSortState(it)) }
    )
    HistoryBottomSheet(
        uiState.historyBottomSheetVisible,
        { mainVM.onUiEvent(UiEvent.HideHistoryBottomSheet) },
        historySortState,
        { mainVM.onSortEvent(SortEvent.UpdateHistorySortState(it)) }
    )
    InfoBottomSheet(
        uiState.infoSheetVisible,
        { mainVM.onUiEvent(UiEvent.HideInfoSheet) },
        uiState.infoSheetCard,
        { mainVM.onUiEvent(UiEvent.ToggleFavorite) }
    )
}
