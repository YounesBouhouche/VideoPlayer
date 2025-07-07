package com.younesbouh.videoplayer.main.domain.events

sealed interface PlayerEvent {
    data class Play(val index: Int, val time: Long = 0L) : PlayerEvent
    data class Seek(val index: Int, val time: Long) : PlayerEvent
    data class SeekTime(val time: Long) : PlayerEvent
    data object PlayPause : PlayerEvent
    data object ToggleControls : PlayerEvent
    data class Backward(val ms: Long): PlayerEvent
    data class Forward(val ms: Long): PlayerEvent
    data object ShowVolumeOverlay: PlayerEvent
    data object ShowBrightnessOverlay: PlayerEvent
    data object HideVolumeOverlay: PlayerEvent
    data object HideBrightnessOverlay: PlayerEvent
    data class SetVolume(val volume: Float): PlayerEvent
    data class SetBrightness(val brightness: Float): PlayerEvent
    data object ShowResizeMode: PlayerEvent
    data object HideResizeMode: PlayerEvent
    data class SetResizeMode(val mode: Int): PlayerEvent
    data object ShowMoreSheet: PlayerEvent
    data object HideMoreSheet: PlayerEvent
    data object ShowSpeedDialog: PlayerEvent
    data object HideSpeedDialog: PlayerEvent
    data class SetSpeed(val speed: Float): PlayerEvent
    data object ToggleLooping: PlayerEvent
    data object ShowSubtitleSheet: PlayerEvent
    data object HideSubtitleSheet: PlayerEvent
    data class SetSubtitlesEnabled(val enabled: Boolean): PlayerEvent
    data class SetSelectedSubtitles(val selected: Int): PlayerEvent
}
