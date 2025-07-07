package com.younesbouh.videoplayer.main.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.younesbouh.videoplayer.main.presentation.states.SortState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T>SortBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    sortState: SortState<T>,
    options: List<T>,
    icon: @Composable (T) -> ImageVector,
    text: @Composable (T) -> Int,
    onSortStateChange: (SortState<T>) -> Unit,
    modifier: Modifier = Modifier
) {
    if (visible)
        ModalBottomSheet(
            onDismissRequest,
            modifier,
            contentWindowInsets = {
                BottomSheetDefaults.windowInsets.add(WindowInsets(16.dp, 0.dp, 16.dp, 16.dp))
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Text(
                "Sort by",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(24.dp))
            VerticalSegmentedButtons(
                options,
                sortState.sortType,
                {
                    onSortStateChange(sortState.copy(sortType = it))
                }
            ) { item, active ->
                Icon(icon(item), null, Modifier.size(22.dp))
                Text(stringResource(text(item)))
            }
            Spacer(Modifier.height(24.dp))
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                SegmentedButton(
                    sortState.ascending,
                    {
                        onSortStateChange(sortState.copy(ascending = true))
                    },
                    SegmentedButtonDefaults.itemShape(0, 2),
                    modifier = Modifier.height(50.dp),
                    icon = {
                        Icon(Icons.Default.ArrowUpward, null)
                    }
                ) {
                    Text("Ascending")
                }
                SegmentedButton(
                    !sortState.ascending,
                    {
                        onSortStateChange(sortState.copy(ascending = false))
                    },
                    SegmentedButtonDefaults.itemShape(1, 2),
                    modifier = Modifier.height(50.dp),
                    icon = {
                        Icon(Icons.Default.ArrowDownward, null)
                    }
                ) {
                    Text("Descending")
                }
            }
        }
}