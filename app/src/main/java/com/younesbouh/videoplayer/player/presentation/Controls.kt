package com.younesbouh.videoplayer.player.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PictureInPicture
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.younesbouh.videoplayer.main.domain.events.PlayerEvent
import com.younesbouh.videoplayer.main.presentation.states.PlayerState
import com.younesbouh.videoplayer.main.presentation.util.timeString
import com.younesbouh.videoplayer.player.domain.PlayState
import kotlin.math.roundToLong

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.Controls(
    state: PlayerState,
    title: String,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onPipRequest: (Boolean) -> Unit,
    onFinishRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val time = state.time
    val duration = state.duration
    val interactionSource = remember { MutableInteractionSource() }
    AnimatedVisibility(
        state.controlsVisible and !state.pictureInPicture,
        modifier.align(Alignment.TopCenter),
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black,
                            Color.Transparent
                        )
                    )
                )
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .padding(
                        WindowInsets.statusBars.union(WindowInsets.displayCutout).add(
                            WindowInsets(left = 16.dp, right = 16.dp, top = 8.dp)
                        ).asPaddingValues()
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ControlButton(Icons.AutoMirrored.Default.ArrowBack, onClick = onFinishRequest)
                Text(
                    title,
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(
                            color = Color.Black, offset = Offset(0f, 5f), blurRadius = 5f
                        )
                    ),
                    maxLines = 1
                )
                ControlButton(Icons.Default.MoreVert) {
                    onPlayerEvent(PlayerEvent.ShowMoreSheet)
                }
            }
        }
    }
    AnimatedVisibility(
        state.controlsVisible and !state.pictureInPicture,
        modifier.align(Alignment.BottomCenter),
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        WindowInsets.navigationBars.union(WindowInsets.displayCutout).add(
                            WindowInsets(8.dp, 8.dp, 8.dp, 8.dp)
                        ).asPaddingValues()
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimeText(if (dragging) (sliderValue * duration).roundToLong() else time)
                    TimeText(duration)
                }
                Slider(
                    interactionSource = interactionSource,
                    value =
                        if (dragging) sliderValue
                        else if (duration == 0L) 0f
                        else (time / duration.toFloat()),
                    onValueChange = {
                        sliderValue = it
                        dragging = true
                    },
                    onValueChangeFinished = {
                        onPlayerEvent(PlayerEvent.SeekTime((sliderValue * duration).toLong()))
                        dragging = false
                    },
                    modifier = Modifier.fillMaxWidth().height(30.dp),
                    thumb = {
                        SliderDefaults.Thumb(
                            interactionSource = interactionSource,
                            colors = SliderDefaults.colors(),
                            thumbSize = DpSize(4.dp, 30.dp)
                        )
                    }
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ControlButton(Icons.Outlined.Subtitles) {
                            onPlayerEvent(PlayerEvent.ShowSubtitleSheet)
                        }
                        ControlButton(
                            Icons.Outlined.Repeat,
                            active = state.isLooping
                        ) {
                            onPlayerEvent(PlayerEvent.ToggleLooping)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ControlButton(Icons.Outlined.Replay10) {
                            onPlayerEvent(PlayerEvent.SeekTime(time - 10000))
                        }
                        if (state.isLoading)
                            CircularProgressIndicator(Modifier.size(40.dp))
                        else
                            ControlButton(
                                when(state.playState) {
                                    PlayState.PLAYING -> Icons.Outlined.Pause
                                    PlayState.PAUSED -> Icons.Outlined.PlayArrow
                                    PlayState.STOP -> Icons.Default.Replay
                                }
                            ) {
                                onPlayerEvent(PlayerEvent.PlayPause)
                            }
                        ControlButton(Icons.Outlined.Forward10) {
                            onPlayerEvent(PlayerEvent.SeekTime(time + 10000))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ControlButton(resizeModes.first { state.resizeMode == it.mode }.icon) {
                            onPlayerEvent(PlayerEvent.ShowResizeMode)
                        }
                        ControlButton(Icons.Outlined.PictureInPicture) {
                            onPipRequest(!state.pictureInPicture)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeText(value: Long, modifier: Modifier = Modifier) {
    Text(
        text = value.timeString,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall.copy(
            shadow = Shadow(
                color = Color.Black, offset = Offset(0f, 5f), blurRadius = 5f
            )
        ),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun ControlButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    onClick: () -> Unit
) {
    FilledIconToggleButton(
        active,
        { onClick() },
        modifier.size(40.dp),
    ) {
        Icon(icon, null, Modifier.size(20.dp))
    }
}