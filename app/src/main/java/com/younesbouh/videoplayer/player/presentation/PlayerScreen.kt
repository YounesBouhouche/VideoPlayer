package com.younesbouh.videoplayer.player.presentation

import android.graphics.Color
import android.media.AudioManager
import android.view.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.younesbouh.videoplayer.core.presentation.util.composables.navBarHeight
import com.younesbouh.videoplayer.core.presentation.util.composables.statusBarHeight
import com.younesbouh.videoplayer.main.domain.events.PlayerEvent
import com.younesbouh.videoplayer.main.domain.models.VideoCard
import com.younesbouh.videoplayer.main.presentation.dialogs.SpeedDialog
import com.younesbouh.videoplayer.main.presentation.states.PlayerState
import kotlin.math.roundToInt

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerScreen(
    items: List<VideoCard>,
    state: PlayerState,
    player: Player,
    window: Window,
    audioManager: AudioManager,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onPipRequest: (Boolean) -> Unit,
    onFinishRequest: () -> Unit
) {
    val statusBarHeight = statusBarHeight
    val navBarHeight = navBarHeight
    val lifecycleOwner = LocalLifecycleOwner.current
    var initialVolume by remember { mutableIntStateOf(0) }
    var initialBrightness by remember { mutableFloatStateOf(0f) }
    val view = LocalView.current
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    var hDragging by remember { mutableStateOf(false) }
    var vDragging by remember { mutableStateOf(false) }
    var isVolume by remember { mutableStateOf(false) }
    var isBrightness by remember { mutableStateOf(false) }
    var alreadyDragged by remember { mutableStateOf(false) }
    var hDragAmount by remember { mutableLongStateOf(0L) }
    var vDragAmount by remember { mutableLongStateOf(0L) }
    var width by remember { mutableIntStateOf(view.width) }
    var height by remember { mutableIntStateOf(view.height) }
    var isVolumeShown by remember { mutableStateOf(false) }
    var isBrightnessShown by remember { mutableStateOf(false) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    Surface(
        Modifier
            .fillMaxSize()
            .onSizeChanged {
                width = it.width
                height = it.height
            }
            .onGloballyPositioned {
                width = it.size.width
                height = it.size.height
            },
        color = androidx.compose.ui.graphics.Color.Black
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).also {
                        it.player = player
                        it.useController = false
                        it.setBackgroundColor(Color.TRANSPARENT)
                        it.resizeMode = state.resizeMode
                    }
                },
                update = {
                    when (lifecycle) {
                        Lifecycle.Event.ON_PAUSE -> {
                            it.onPause()
                            //it.player?.pause()
                        }
                        Lifecycle.Event.ON_RESUME -> {
                            it.onResume()
                        }
                        else -> Unit
                    }
                    it.resizeMode = state.resizeMode
                },
                modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onPlayerEvent(PlayerEvent.ToggleControls)
                        },
                        onDoubleTap = {
                            onPlayerEvent(PlayerEvent.PlayPause)
                        }
                    )
                }.pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            if ((it.y > statusBarHeight.toPx()) and (it.y < (height - navBarHeight.toPx()))) {
                                vDragging = true
                                vDragAmount = 0L
                                if (it.x > width.toFloat() / 2) {
                                    isVolume = true
                                    initialVolume =
                                        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                    onPlayerEvent(PlayerEvent.ShowVolumeOverlay)
                                } else {
                                    isBrightness = true
                                    initialBrightness =
                                        if (alreadyDragged) window.attributes.screenBrightness
                                        else state.brightness
                                    alreadyDragged = true
                                    onPlayerEvent(PlayerEvent.ShowBrightnessOverlay)
                                }
                            } else vDragging = false
                        },
                        onDragEnd = {
                            vDragging = false
                            onPlayerEvent(PlayerEvent.HideVolumeOverlay)
                            onPlayerEvent(PlayerEvent.HideBrightnessOverlay)
                            isVolume = false
                            isBrightness = false
                        }
                    ) { _, amount ->
                        vDragAmount += amount.toLong()
                        if (vDragging) {
                            if (isVolume) {
                                val newVolume =
                                    (initialVolume +
                                            (audioManager.getStreamMaxVolume(
                                                AudioManager.STREAM_MUSIC
                                            ) * (-vDragAmount.toFloat() / height * 2)).roundToInt())
                                        .coerceIn(
                                            0,
                                            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                        )
                                onPlayerEvent(PlayerEvent.SetVolume(newVolume / audioManager
                                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()))
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    newVolume,
                                    0
                                )
                            } else {
                                window.attributes = window.attributes.apply {
                                    screenBrightness = (
                                        initialBrightness - vDragAmount.toFloat() / height.toFloat() * 4
                                    ).coerceIn(0f, 1f)
                                }
                                onPlayerEvent(PlayerEvent.SetBrightness(window.attributes.screenBrightness))
                            }
                        }
                    }
                }.pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            hDragging = true
                            hDragAmount = 0L
                        },
                        onDragEnd = {
                            onPlayerEvent(PlayerEvent.SeekTime(player.currentPosition + hDragAmount))
                            hDragging = false
                        }
                    ) { _, amount ->
                        isVolumeShown = false
                        isBrightnessShown = false
                        hDragAmount += amount.toLong() * 100
                    }
                }
            )
            Controls(
                state,
                items.getOrNull(state.index)?.title ?: "",
                onPlayerEvent,
                onPipRequest,
                onFinishRequest
            )
            VolumeOverlay(state.volumeOverlayVisible, state.volume, Modifier.align(Alignment.TopStart))
            BrightnessOverlay(state.brightnessOverlayVisible, state.brightness, Modifier.align(Alignment.TopEnd))
            SeekOverlay(hDragging, hDragAmount, Modifier.align(Alignment.Center))
        }
    }
    ResizeModeBottomSheet(
        state.resizeModeVisible,
        { onPlayerEvent(PlayerEvent.HideResizeMode) },
        state.resizeMode,
        { onPlayerEvent(PlayerEvent.SetResizeMode(it)) },
    )
    MoreBottomSheet(
        state.moreSheetVisible,
        { onPlayerEvent(PlayerEvent.HideMoreSheet) },
        { onPlayerEvent(PlayerEvent.ShowSpeedDialog) },
        state.speed
    )
    SubtitleBottomSheet(
        state.subtitleSheetVisible,
        { onPlayerEvent(PlayerEvent.HideSubtitleSheet) },
        state.subtitlesEnabled,
        { onPlayerEvent(PlayerEvent.SetSubtitlesEnabled(it)) },
        state.subtitlesTracks,
        state.selectedAudioTrack,
        { onPlayerEvent(PlayerEvent.SetSelectedSubtitles(it)) },
    )
    SpeedDialog(
        state.speedDialogVisible,
        { onPlayerEvent(PlayerEvent.HideSpeedDialog) },
        state.speed
    ) {
        onPlayerEvent(PlayerEvent.SetSpeed(it))
    }
}
