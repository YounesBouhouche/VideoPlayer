package com.younesbouh.videoplayer.player.presentation

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.media.session.PlaybackState
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.TrackSelector
import com.younesbouh.videoplayer.core.data.db.AppDatabase
import com.younesbouh.videoplayer.main.domain.events.PlayerEvent
import com.younesbouh.videoplayer.main.domain.models.Track
import com.younesbouh.videoplayer.main.domain.models.VideoCard
import com.younesbouh.videoplayer.main.presentation.states.PlayerState
import com.younesbouh.videoplayer.main.presentation.viewmodel.Task
import com.younesbouh.videoplayer.player.domain.PlayState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.map
import kotlin.math.roundToLong

@UnstableApi
class PlayerVM@OptIn(UnstableApi::class)
constructor(
    val app: Application,
    val context: Context,
    val trackSelector: TrackSelector,
    val player: Player,
    db: AppDatabase,
) : ViewModel() {
    private val dao = db.dao
    private val _items = MutableStateFlow(emptyList<VideoCard>())
    val items = _items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private var timeTask = Task(viewModelScope)

    private fun startTimeUpdate() =
        timeTask.startRepeating(100L) {
            _playerState.update {
                it.copy(time = player.currentPosition)
            }
            val progress = player.currentPosition.toFloat() / player.duration
            val index = player.currentMediaItemIndex
            viewModelScope.launch(Dispatchers.IO) {
                _items.value.getOrNull(index)?.let {
                    dao.updateProgress(it.id, progress)
                }
            }
        }

    init {
        player.prepare()
        player.addListener(object: Player.Listener {
            @OptIn(UnstableApi::class)
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                _playerState.update {
                    it.copy(
                        index = player.currentMediaItemIndex,
                        time = player.currentPosition,
                        duration = _items.value.getOrNull(player.currentMediaItemIndex)?.duration ?: 0L
                    )
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                _playerState.update {
                    it.copy(
                        playState =
                            if (isPlaying) PlayState.PLAYING
                            else if (player.playbackState == Player.STATE_ENDED) PlayState.STOP
                            else PlayState.PAUSED
                    )
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                _playerState.update {
                    it.copy(isLoading = playbackState == Player.STATE_BUFFERING)
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)
                _playerState.update {
                    it.copy(isLooping = repeatMode != Player.REPEAT_MODE_OFF)
                }
            }
        })
    }

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PlayerState())

    fun loadFile(
        uri: Uri,
        selection: String? = null,
        selectionArgs: Array<String> = emptyArray(),
        onFinish: () -> Unit
    ) {
        var cards = emptyList<VideoCard>()
        viewModelScope.launch(Dispatchers.IO) {
            app.contentResolver.query(
                uri, arrayOf(
                    MediaStore.Video.VideoColumns._ID,
                    MediaStore.Video.VideoColumns.DISPLAY_NAME,
                    MediaStore.Video.VideoColumns.DURATION,
                    MediaStore.Video.VideoColumns.TITLE,
                    MediaStore.Video.VideoColumns.ALBUM,
                    MediaStore.Video.VideoColumns.ARTIST,
                    MediaStore.Video.VideoColumns.DATA,
                ), selection, selectionArgs, ""
            )?.use { crs ->crs.moveToFirst()
                val idColumn = crs.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID)
                val durationColumn =
                    crs.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)
                val titleColumn = crs.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.TITLE)
                val artistColumn = crs.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.ARTIST)
                val albumColumn = crs.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.ALBUM)
                val pathColumn = crs.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA)
                val id = crs.getLong(idColumn)
                val duration = crs.getLong(durationColumn)
                val title = crs.getString(titleColumn)
                val artist = crs.getString(artistColumn)
                val album = crs.getString(albumColumn)
                val path = crs.getString(pathColumn)
                val uri: Uri =
                    ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id,
                    )
                crs.close()
                cards = listOf(
                    VideoCard
                        .Builder()
                        .setContentUri(uri)
                        .setId(id)
                        .setTitle(title)
                        .setArtist(artist)
                        .setAlbum(album)
                        .setPath(path)
                        .setDuration(duration)
                        .build()
                )
            }
            withContext(Dispatchers.Main) {
                _items.value = cards
                player.setMediaItems(cards.map { MediaItem.fromUri(it.contentUri) })
                onFinish()
            }
        }
    }

    fun setItem(item: Uri, onFinish: () -> Unit) = loadFile(item, onFinish = onFinish)

    fun setItem(item: String, onFinish: () -> Unit) =
        loadFile(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            MediaStore.Video.VideoColumns.DATA + "=? ",
            arrayOf(item),
            onFinish
        )

    fun onPlayerEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.Play -> {
                player.seekTo(
                    event.index,
                    if (event.time >= player.duration - 1000L) 0L else event.time
                )
                player.play()
                player.repeatMode = Player.REPEAT_MODE_OFF
                val audioTracks = mutableListOf<Track>()
                val subtitlesTracks = mutableListOf<Track>()
                for (group in player.currentTracks.groups) {
                    val groupInfo = group.mediaTrackGroup
                    for (i in 0 until groupInfo.length) {
                        val track =
                            Track(
                                groupInfo.getFormat(i).id ?: "${subtitlesTracks.size + 1}",
                                groupInfo.getFormat(i).language.toString(),
                                groupInfo.getFormat(i).label ?: "Track",
                            )
                        when(group.type) {
                            C.TRACK_TYPE_AUDIO -> audioTracks.add(track)
                            C.TRACK_TYPE_TEXT -> subtitlesTracks.add(track)
                        }
                    }
                }
                _playerState.update {
                    it.copy(
                        index = event.index,
                        time = 0,
                        duration = _items.value.getOrNull(event.index)?.duration ?: 0L,
                        isLooping = false,
                        audioTracks = audioTracks,
                        subtitlesTracks = subtitlesTracks
                    )
                }
                startTimeUpdate()
            }
            is PlayerEvent.Seek -> {
                _playerState.update {
                    it.copy(
                        time = event.time,
                        index = event.index,
                        duration = _items.value.getOrNull(event.index)?.duration ?: 0L
                    )
                }
                player.seekTo(event.index, event.time)
            }
            is PlayerEvent.SeekTime -> {
                _playerState.update { it.copy(time = event.time) }
                player.seekTo(event.time)
            }
            PlayerEvent.PlayPause -> {
                when(_playerState.value.playState) {
                    PlayState.PLAYING -> player.pause()
                    PlayState.PAUSED -> player.play()
                    PlayState.STOP -> {
                        player.seekToDefaultPosition()
                        player.play()
                    }
                }
            }
            PlayerEvent.ToggleControls -> {
                _playerState.update { it.copy(controlsVisible = !it.controlsVisible) }
            }
            is PlayerEvent.Backward -> player.seekTo(player.currentPosition - event.ms)
            is PlayerEvent.Forward -> player.seekTo(player.currentPosition + event.ms)
            is PlayerEvent.SetBrightness -> {
                _playerState.update { it.copy(brightness = event.brightness) }
            }
            is PlayerEvent.SetVolume -> {
                _playerState.update { it.copy(volume = event.volume) }
            }
            PlayerEvent.HideBrightnessOverlay -> {
                _playerState.update { it.copy(brightnessOverlayVisible = false) }
            }
            PlayerEvent.HideVolumeOverlay -> {
                _playerState.update { it.copy(volumeOverlayVisible = false) }
            }
            PlayerEvent.ShowBrightnessOverlay -> {
                _playerState.update { it.copy(brightnessOverlayVisible = true) }
            }
            PlayerEvent.ShowVolumeOverlay -> {
                _playerState.update { it.copy(volumeOverlayVisible = true) }
            }
            PlayerEvent.HideResizeMode -> {
                _playerState.update { it.copy(resizeModeVisible = false) }
            }
            is PlayerEvent.SetResizeMode -> {
                _playerState.update { it.copy(resizeMode = event.mode) }
            }
            PlayerEvent.ShowResizeMode -> {
                _playerState.update { it.copy(resizeModeVisible = true) }
            }
            PlayerEvent.HideMoreSheet -> {
                _playerState.update { it.copy(moreSheetVisible = false) }
            }
            PlayerEvent.ShowMoreSheet -> {
                _playerState.update { it.copy(moreSheetVisible = true) }
            }
            PlayerEvent.HideSpeedDialog -> {
                _playerState.update { it.copy(speedDialogVisible = false) }
            }
            PlayerEvent.ShowSpeedDialog -> {
                _playerState.update { it.copy(speedDialogVisible = true) }
            }
            is PlayerEvent.SetSpeed -> {
                _playerState.update { it.copy(speed = event.speed) }
                player.setPlaybackSpeed(event.speed)
            }
            is PlayerEvent.ToggleLooping -> {
                player.repeatMode =
                    if (_playerState.value.isLooping) Player.REPEAT_MODE_OFF
                    else Player.REPEAT_MODE_ONE
            }

            PlayerEvent.HideSubtitleSheet -> _playerState.update { it.copy(subtitleSheetVisible = false) }
            is PlayerEvent.SetSubtitlesEnabled -> {
                _playerState.update { it.copy(subtitlesEnabled = event.enabled) }
                if (event.enabled) {
                    trackSelector.parameters =
                        trackSelector.parameters
                            .buildUpon()
                            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                            .build()
                } else {
                    trackSelector.parameters =
                        trackSelector.parameters
                            .buildUpon()
                            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                            .build()
                }
            }
            PlayerEvent.ShowSubtitleSheet -> _playerState.update { it.copy(subtitleSheetVisible = true) }
            is PlayerEvent.SetSelectedSubtitles -> {
                _playerState.update {
                    player.trackSelectionParameters =
                        trackSelector.parameters
                            .buildUpon()
                            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                            .setPreferredTextLanguage(it.subtitlesTracks[event.selected].language)
                            .build()
                    it.copy(selectedAudioTrack = event.selected)
                }
            }
        }
    }

    fun updatePictureInPicture(pip: Boolean) {
        _playerState.update { it.copy(pictureInPicture = pip) }
    }

    fun resumePlayer() {
        if (player.playbackState == PlaybackState.STATE_STOPPED) {
            player.setMediaItems(_items.value.map { MediaItem.fromUri(it.contentUri) })
            player.prepare()
            val item = _items.value.getOrNull(_playerState.value.index)
            viewModelScope.launch(Dispatchers.IO) {
                (item?.let {
                    ((dao.getItem(item.id)?.progress ?: 0f) * item.duration).roundToLong()
                } ?: 0L).let { time ->
                    withContext(Dispatchers.Main) {
                        player.seekTo(_playerState.value.index, time)
                    }
                }
            }
            player.play()
        }
    }
}