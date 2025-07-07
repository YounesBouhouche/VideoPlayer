package com.younesbouh.videoplayer.settings.presentation.routes.theme

import com.younesbouh.videoplayer.settings.domain.models.ColorScheme
import com.younesbouh.videoplayer.settings.domain.models.Theme

data class UiState(
    val themeDialog: Boolean = false,
    val colorDialog: Boolean = false,
    val selectedTheme: Theme = Theme.SYSTEM,
    val selectedColor: ColorScheme = ColorScheme.BLUE,
)
