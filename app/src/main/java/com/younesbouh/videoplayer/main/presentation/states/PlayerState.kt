package com.younesbouh.videoplayer.main.presentation.states

import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.younesbouh.videoplayer.main.domain.models.Track
import com.younesbouh.videoplayer.player.domain.PlayState

@UnstableApi
data class PlayerState(
    val controlsVisible: Boolean = false,
    val index: Int = 0,
    val playState: PlayState = PlayState.STOP,
    val isLoading: Boolean = false,
    val isLooping: Boolean = false,
    val time: Long = 0,
    val duration: Long = 0,
    val pictureInPicture: Boolean = false,
    val volume: Float = 0f,
    val brightness: Float = 0f,
    val volumeOverlayVisible: Boolean = false,
    val brightnessOverlayVisible: Boolean = false,
    val resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    val resizeModeVisible: Boolean = false,
    val moreSheetVisible: Boolean = false,
    val subtitleSheetVisible: Boolean = false,
    val subtitlesEnabled: Boolean = true,
    val speed: Float = 1f,
    val speedDialogVisible: Boolean = false,
    val audioTracksVisible: Boolean = false,
    val audioTracks: List<Track> = emptyList(),
    val subtitlesTracks: List<Track> = emptyList(),
    val selectedAudioTrack: Int = 0
)