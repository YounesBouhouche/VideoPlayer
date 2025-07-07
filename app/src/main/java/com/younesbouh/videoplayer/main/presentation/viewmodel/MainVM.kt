package com.younesbouh.videoplayer.main.presentation.viewmodel

import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.younesbouh.videoplayer.core.data.PlayerDataStore
import com.younesbouh.videoplayer.core.data.db.AppDatabase
import com.younesbouh.videoplayer.core.presentation.util.search
import com.younesbouh.videoplayer.main.domain.events.FilesEvent
import com.younesbouh.videoplayer.main.domain.events.FilesEvent.AddFile
import com.younesbouh.videoplayer.main.domain.events.FilesEvent.LoadFiles
import com.younesbouh.videoplayer.main.domain.events.FilesEvent.RemoveFile
import com.younesbouh.videoplayer.main.domain.events.SearchEvent
import com.younesbouh.videoplayer.main.domain.events.SortEvent
import com.younesbouh.videoplayer.main.domain.events.UiEvent
import com.younesbouh.videoplayer.main.domain.models.Folder
import com.younesbouh.videoplayer.main.domain.models.ItemData
import com.younesbouh.videoplayer.main.domain.models.VideoCard
import com.younesbouh.videoplayer.main.presentation.states.FoldersSortType
import com.younesbouh.videoplayer.main.presentation.states.HistorySortType
import com.younesbouh.videoplayer.main.presentation.states.SearchState
import com.younesbouh.videoplayer.main.presentation.states.SortState
import com.younesbouh.videoplayer.main.presentation.states.SortType
import com.younesbouh.videoplayer.main.presentation.states.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.collections.firstOrNull
import kotlin.collections.map
import kotlin.collections.reduce
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString

@OptIn(ExperimentalCoroutinesApi::class)
class MainVM(
    val context: Context,
    playerDataStore: PlayerDataStore,
    permissions: Array<String>,
    db: AppDatabase,
) : ViewModel() {
    private val dao = db.dao

    private val _itemsData = dao.getItemsData()

    private fun <T> Flow<T>.stateInVM(initialValue: T) =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), initialValue)

    fun setLastPlayed(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            if (dao.getItem(id) == null)
                dao.upsertItem(ItemData(id, false, System.currentTimeMillis(), 0f))
            else
                dao.updateLastPlayed(id, System.currentTimeMillis())
        }
    }

    fun sortFiles(files: List<VideoCard>, sortState: SortState<SortType>) =
        if (sortState.ascending) {
            when (sortState.sortType) {
                SortType.Title -> files.sortedBy { it.title }
                SortType.Filename -> files.sortedBy { it.path }
                SortType.Duration -> files.sortedBy { it.duration }
                SortType.Date -> files.sortedBy { it.date }
                SortType.Size -> files.sortedBy { it.size }
            }
        } else {
            when (sortState.sortType) {
                SortType.Title -> files.sortedByDescending { it.title }
                SortType.Filename -> files.sortedByDescending { it.path }
                SortType.Duration -> files.sortedByDescending { it.duration }
                SortType.Date -> files.sortedByDescending { it.date }
                SortType.Size -> files.sortedByDescending { it.size }
            }
        }

    private val isGranted =
        permissions.map {
            ContextCompat
                .checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }.reduce { old, current -> old and current }

    private val _granted = MutableStateFlow(isGranted)
    val granted = _granted.stateInVM(isGranted)

    private var initialized = false
    private val _files = MutableStateFlow(emptyList<VideoCard>())

    private val _folder = MutableStateFlow<Folder?>(null)

    private val _sortState = MutableStateFlow(SortState<SortType>(SortType.Title))
    val librarySortState = _sortState.stateInVM(SortState<SortType>(SortType.Title))

    private val _favoritesSortState = MutableStateFlow(SortState<SortType>(SortType.Title))
    val favoritesSortState = _favoritesSortState.stateInVM(SortState<SortType>(SortType.Title))

    private val _historySortState =
        MutableStateFlow(SortState<HistorySortType>(HistorySortType.LastPlayed, ascending = false))
    val historySortState = _historySortState.stateInVM(
        SortState<HistorySortType>(
            HistorySortType.LastPlayed,
            ascending = false
        )
    )

    private val _foldersSortState =
        MutableStateFlow(SortState<FoldersSortType>(FoldersSortType.Name))
    val foldersSortState =
        _foldersSortState.stateInVM(SortState<FoldersSortType>(FoldersSortType.Name))

    private val _folderSortState = MutableStateFlow(SortState<SortType>(SortType.Title))
    val folderSortState = _folderSortState.stateInVM(SortState<SortType>(SortType.Title))

    private val _folders = _files.map { files ->
        files.groupBy { Paths.get(it.path).parent.pathString }.map {
            Folder(0, Paths.get(it.key).nameWithoutExtension, it.value)
        }
    }
    val folders = _folders.map {
        it.mapIndexed { index, folder -> folder.copy(id = index) }
    }
    val foldersState = combine(folders, _foldersSortState) { list, sortState ->
        if (sortState.ascending) {
            when (sortState.sortType) {
                FoldersSortType.Name -> list.sortedBy { it.name }
                FoldersSortType.Size -> list.sortedBy { it.files.sumOf { it.size } }
                FoldersSortType.ItemsCount -> list.sortedBy { it.files.size }
            }
        } else {
            when (sortState.sortType) {
                FoldersSortType.Name -> list.sortedByDescending { it.name }
                FoldersSortType.Size -> list.sortedByDescending { it.files.sumOf { it.size } }
                FoldersSortType.ItemsCount -> list.sortedByDescending { it.files.size }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _filesWithData = combine(_files, _itemsData) { files, data ->
        files.map { file ->
            file.copy(
                favorite = data.firstOrNull { it.id == file.id }?.favorite == true,
                progress = data.firstOrNull { it.id == file.id }?.progress ?: 0f,
                lastPlayed = data.firstOrNull { it.id == file.id }?.lastPlayed,
            )
        }
    }

    val files = combine(_filesWithData, _sortState) { files, sortState ->
        sortFiles(files, sortState)
    }.stateInVM(emptyList())

    private val _favorites = dao.getFavorites()
    val favorites =
        combine(_filesWithData, _favorites, _favoritesSortState) { files, favorites, sortState ->
            val list = favorites.mapNotNull { id -> files.firstOrNull { it.id == id.id } }
            sortFiles(list, sortState)
        }.stateInVM(emptyList())

    private val _history = dao.getHistory()
    val history = combine(_filesWithData, _historySortState, _history) { files, sortState, history ->
        val list = history.mapNotNull { id -> files.firstOrNull { it.id == id.id } }
        if (sortState.ascending)
            when (sortState.sortType) {
                HistorySortType.LastPlayed -> list.sortedBy { it.lastPlayed }
                HistorySortType.Title -> list.sortedBy { it.title }
                HistorySortType.Filename -> list.sortedBy { it.path }
                HistorySortType.Duration -> list.sortedBy { it.duration }
                HistorySortType.Size -> list.sortedBy { it.size }
                HistorySortType.Date -> list.sortedBy { it.date }
            }
        else
            when (sortState.sortType) {
                HistorySortType.LastPlayed -> list.sortedByDescending { it.lastPlayed }
                HistorySortType.Title -> list.sortedByDescending { it.title }
                HistorySortType.Filename -> list.sortedByDescending { it.path }
                HistorySortType.Duration -> list.sortedByDescending { it.duration }
                HistorySortType.Size -> list.sortedByDescending { it.size }
                HistorySortType.Date -> list.sortedByDescending { it.date }
            }
    }.stateInVM(emptyList())

    val folderSorted =
        combine(_folder, _folderSortState) { folder, sortState ->
            folder?.let { it.copy(files = sortFiles(it.files, sortState)) }
        }.stateInVM(null)

    private val _searchState = MutableStateFlow(SearchState())
    val searchState =
        combine(_searchState, _filesWithData) { state, files ->
            val results =
                if (state.query.isNotBlank()) {
                    files.filter { it.search(state.query) }
                } else {
                    emptyList()
                }
            state.copy(result = results)
        }.stateInVM(SearchState())

    private val rememberRepeat = playerDataStore.rememberRepeat

    private val rememberShuffle = playerDataStore.rememberShuffle

    private val rememberSpeed = playerDataStore.rememberSpeed

    private val rememberPitch = playerDataStore.rememberPitch

    private val repeatMode = playerDataStore.repeatMode

    private val shuffle = playerDataStore.shuffle

    private val speed = playerDataStore.speed

    private val pitch = playerDataStore.pitch

    fun setGranted() {
        viewModelScope.launch {
            _granted.value = true
            if (!initialized) loadFiles()
        }
    }

    fun onSearchEvent(event: SearchEvent) {
        when (event) {
            SearchEvent.ClearQuery -> {
                _searchState.update {
                    it.copy(query = "")
                }
            }

            is SearchEvent.UpdateQuery -> {
                _searchState.update {
                    it.copy(query = event.query)
                }
            }

            SearchEvent.Collapse -> {
                _searchState.update {
                    it.copy(expanded = false, query = "")
                }
            }

            SearchEvent.Expand -> {
                _searchState.update {
                    it.copy(expanded = true)
                }
            }

            is SearchEvent.UpdateExpanded -> {
                _searchState.update {
                    it.copy(expanded = event.expanded, query = "")
                }
            }
        }
    }

    fun onFilesEvent(event: FilesEvent) {
        when (event) {
            LoadFiles -> {
                viewModelScope.launch {
                    loadFiles()
                }
            }

            is AddFile -> {
                viewModelScope.launch {
                    _files.value += event.file
                }
            }

            is RemoveFile -> {
                viewModelScope.launch {
                    _files.value -= event.file
                }
            }
        }
    }

    fun onSortEvent(event: SortEvent) {
        when (event) {
            is SortEvent.UpdateSortState -> {
                _sortState.value = event.sortState
            }

            is SortEvent.UpdateFoldersSortState -> {
                _foldersSortState.value = event.sortState
            }

            is SortEvent.UpdateFolderSortState -> {
                _folderSortState.value = event.sortState
            }

            is SortEvent.UpdateFavoritesSortState -> {
                _favoritesSortState.value = event.sortState
            }

            is SortEvent.UpdateHistorySortState -> {
                _historySortState.value = event.sortState
            }
        }
    }

    private fun loadFiles() {
        _files.value = emptyList()
        _uiState.update {
            it.copy(loading = true)
        }
        val list = mutableListOf<VideoCard>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val cursor =
                    context.contentResolver.query(
                        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
                        arrayOf(
                            MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.DISPLAY_NAME,
                            MediaStore.Video.Media.DURATION,
                            MediaStore.Video.Media.TITLE,
                            MediaStore.Video.Media.ALBUM,
                            MediaStore.Video.Media.ARTIST,
                            MediaStore.Video.Media.DATA,
                            MediaStore.Video.Media.SIZE,
                        ),
                        "",
                        null,
                        "${MediaStore.Video.VideoColumns.DISPLAY_NAME} ASC",
                    )
                cursor?.use { crs ->
                    val idColumn = crs.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val durationColumn = crs.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    val titleColumn = crs.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                    val artistColumn = crs.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)
                    val albumColumn = crs.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)
                    val pathColumn = crs.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    val sizeColumn = crs.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                    while (crs.moveToNext()) {
                        val id = crs.getLong(idColumn)
                        val duration = crs.getLong(durationColumn)
                        val title = crs.getString(titleColumn)
                        val artist = crs.getString(artistColumn)
                        val album = crs.getString(albumColumn)
                        val path = crs.getString(pathColumn)
                        val date = Files.getLastModifiedTime(Paths.get(path)).toMillis()
                        val size = crs.getLong(sizeColumn)
                        val contentUri: Uri =
                            ContentUris.withAppendedId(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                id,
                            )
                        list.add(
                            VideoCard
                                .Builder()
                                .setContentUri(contentUri)
                                .setId(id)
                                .setTitle(title)
                                .setArtist(artist)
                                .setAlbum(album)
                                .setPath(path)
                                .setDate(date)
                                .setSize(size)
                                .setDuration(duration)
                                .build(),
                        )
//                        _folders.update {
//                            it.toMutableMap().apply {
//                                this[album] = (this[album] ?: emptyList()) + id
//                            }
//                        }
                    }
                    crs.close()
                    initialized = true
                }
            }
            _files.value = list
            _uiState.update {
                it.copy(loading = false)
            }
            withContext(Dispatchers.IO) {
                list.forEachIndexed { index, file ->
                    try {
                        list[index].apply {
                            cover = ThumbnailUtils.createVideoThumbnail(
                                File(file.path),
                                Size(640, 480),
                                null
                            )
                        }
                    } catch (_: Exception) {

                    }
                }
                _files.value = list
            }
        }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.stateInVM(UiState())

    fun onUiEvent(event: UiEvent) {
        when (event) {
            UiEvent.HideBottomSheet -> _uiState.update {
                it.copy(bottomSheetVisible = false)
            }

            UiEvent.ShowBottomSheet -> _uiState.update {
                it.copy(bottomSheetVisible = true)
            }

            UiEvent.HideInfoSheet -> _uiState.update {
                it.copy(infoSheetVisible = false)
            }

            UiEvent.ToggleFavorite -> viewModelScope.launch(Dispatchers.IO) {
                val card = _uiState.value.infoSheetCard
                val favorite = dao.suspendGetFavorite(card.id)
                if (favorite == null)
                    dao.upsertItem(ItemData(card.id, true, null))
                else {
                    Timber.tag("Favorite").d("Favorite state: $favorite")
                    dao.updateFavorite(card.id, !favorite)
                }
                withContext(Dispatchers.Main) {
                    onUiEvent(UiEvent.HideInfoSheet)
                }
            }

            is UiEvent.ShowInfoSheet -> _uiState.update {
                it.copy(infoSheetVisible = true, infoSheetCard = event.card)
            }

            UiEvent.HideFoldersBottomSheet -> _uiState.update {
                it.copy(foldersBottomSheetVisible = false)
            }

            UiEvent.ShowFoldersBottomSheet -> _uiState.update {
                it.copy(foldersBottomSheetVisible = true)
            }

            UiEvent.HideFolderBottomSheet -> _uiState.update {
                it.copy(folderBottomSheetVisible = false)
            }

            UiEvent.ShowFolderBottomSheet -> _uiState.update {
                it.copy(folderBottomSheetVisible = true)
            }

            is UiEvent.LoadFolder -> viewModelScope.launch(Dispatchers.Main) {
                _folder.value = folders.first().firstOrNull { folder -> event.id == folder.id }
            }

            UiEvent.HideFavoritesBottomSheet -> _uiState.update {
                it.copy(favoritesBottomSheetVisible = false)
            }

            UiEvent.ShowFavoritesBottomSheet -> _uiState.update {
                it.copy(favoritesBottomSheetVisible = true)
            }

            UiEvent.HideHistoryBottomSheet -> _uiState.update {
                it.copy(historyBottomSheetVisible = false)
            }

            UiEvent.ShowHistoryBottomSheet -> _uiState.update {
                it.copy(historyBottomSheetVisible = true)
            }
        }
    }
}
