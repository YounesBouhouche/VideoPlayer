package com.younesbouh.videoplayer.player.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.younesbouh.videoplayer.main.domain.models.Track

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtitleBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    tracks: List<Track>,
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (visible)
        ModalBottomSheet(onDismissRequest, modifier, contentWindowInsets = {
            BottomSheetDefaults.windowInsets.add(WindowInsets(bottom = 16.dp, left = 16.dp, right = 16.dp))
        }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            Row(
                Modifier
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(
                        if (enabled) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onEnabledChange(!enabled) }
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Enable Subtitles",
                    style = MaterialTheme.typography.titleLarge,
                    color =
                        if (enabled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    ,
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                )
            }
            if (tracks.isEmpty())
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "No subtitles available",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            else
                tracks.forEachIndexed { index, track ->
                    Row(Modifier.padding(8.dp)
                            .clickable { if (enabled) onSelectedChange(index) }
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = enabled && index == selected,
                            onClick = { if (enabled) onSelectedChange(0) },
                        )
                        Text(
                            track.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.clickable { if (enabled) onSelectedChange(index) }
                        )
                    }
                }
        }
}