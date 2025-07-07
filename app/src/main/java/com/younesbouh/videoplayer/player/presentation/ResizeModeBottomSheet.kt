package com.younesbouh.videoplayer.player.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.younesbouh.videoplayer.main.presentation.VerticalSegmentedButtons


@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResizeModeBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    resizeMode: Int,
    onResizeModeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (visible)
        ModalBottomSheet(onDismissRequest, modifier, contentWindowInsets = {
            BottomSheetDefaults.windowInsets.add(WindowInsets(16.dp, 0.dp, 16.dp, 16.dp))
        }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            Text(
                "Resize Mode",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))
            VerticalSegmentedButtons(
                resizeModes,
                resizeModes.first { it.mode == resizeMode },
                {
                    onResizeModeChange(it.mode)
                }
            ) { item, active ->
                Icon(item.icon, null)
                Text(item.text)
            }
        }
}