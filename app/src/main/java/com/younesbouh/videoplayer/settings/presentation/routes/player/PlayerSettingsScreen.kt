package com.younesbouh.videoplayer.settings.presentation.routes.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.younesbouh.videoplayer.R
import com.younesbouh.videoplayer.main.presentation.util.plus
import com.younesbouh.videoplayer.settings.presentation.components.SettingsItem
import com.younesbouh.videoplayer.settings.presentation.components.SettingsList
import com.younesbouh.videoplayer.settings.presentation.components.listItemShape
import com.younesbouh.videoplayer.settings.presentation.util.Category
import com.younesbouh.videoplayer.settings.presentation.util.Checked
import com.younesbouh.videoplayer.settings.presentation.util.SettingData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSettingsScreen(
    viewModel: PlayerSettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val showVolumeSlider by viewModel.showVolumeSlider.collectAsState()
    val showPitchButton by viewModel.showPitchButton.collectAsState()
    val settings = listOf(
        Category(
            name = R.string.customize_view,
            items = listOf(
                SettingData(
                    headline = R.string.show_volume_slider,
                    icon = Icons.AutoMirrored.Default.VolumeUp,
                    checked = Checked(false, showVolumeSlider) {
                        viewModel.saveSettings(showVolumeSlider = it)
                    },
                    large = false,
                    separator = false,
                ) {
                    viewModel.saveSettings(showVolumeSlider = !showVolumeSlider)
                },
            ),
        ),
        Category(
            name = R.string.controls,
            items = listOf(
                SettingData(
                    headline = R.string.show_pitch_button,
                    icon = Icons.Default.RecordVoiceOver,
                    checked = Checked(false, showPitchButton) {
                        viewModel.saveSettings(showPitchButton = it)
                    },
                    large = false,
                    separator = false,
                ) {
                    viewModel.saveSettings(showPitchButton = !showPitchButton)
                }
            ),
        )
    )
    Scaffold(
        modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.player),
                        maxLines = 1,
                        overflow = TextOverflow.Companion.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = paddingValues + PaddingValues(12.dp, 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(settings) {
                SettingsList(it.name) {
                    it.items.forEachIndexed { index, item ->
                        SettingsItem(
                            data = item,
                            shape = listItemShape(index, it.items.size),
                        )
                    }
                }
            }
        }
    }
}