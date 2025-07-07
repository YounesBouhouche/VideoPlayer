package com.younesbouh.videoplayer.player.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onShowSpeedDialog: () -> Unit,
    speed: Float,
    modifier: Modifier = Modifier
) {
    if (visible)
        ModalBottomSheet(onDismissRequest, modifier, contentWindowInsets = {
            BottomSheetDefaults.windowInsets.add(WindowInsets(bottom = 16.dp))
        }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            RowButton(
                Icons.Default.Speed,
                "Speed",
                "${speed.toBigDecimal().setScale(2, RoundingMode.FLOOR)}x"
            ) {
                onShowSpeedDialog()
                onDismissRequest()
            }
            RowButton(Icons.Outlined.MusicNote, "Audio Track") { }
            RowButton(Icons.Default.AccessTime, "Jump to time") { }
            RowButton(Icons.Outlined.Info, "Show info") { }
        }
}

@Composable
fun RowButton(
    icon: ImageVector,
    text: String,
    trailingText: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier.clickable(onClick = onClick).padding(horizontal = 24.dp, vertical = 18.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            Modifier.fillMaxWidth().weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
        ) {
            Icon(icon, null)
            Text(text, style = MaterialTheme.typography.titleMedium)
        }
        trailingText?.let {
            Text(
                it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}