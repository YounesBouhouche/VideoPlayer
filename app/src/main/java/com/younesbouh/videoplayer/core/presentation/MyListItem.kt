package com.younesbouh.videoplayer.core.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.younesbouh.videoplayer.main.presentation.util.timeString

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyListItem(
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    headline: String,
    supporting: String,
    cover: @Composable () -> Unit,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier
            .background(background, MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .clipToBounds()
            .combinedClickable(onLongClick = onLongClick, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        cover()
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                headline,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                    ),
            )
            Text(
                supporting,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        trailingContent?.invoke(this)
    }
}

@Composable
fun MyListItem(
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    headline: String,
    supporting: String,
    progress: Float? = null,
    cover: ImageBitmap? = null,
    alternative: ImageVector = Icons.Default.Movie,
    time: Long? = null,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
) {
    MyListItem(
        modifier,
        background,
        onClick,
        onLongClick,
        headline,
        supporting,
        {
            Box(
                Modifier
                    .height(80.dp)
                    .aspectRatio(16 / 9f, true)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    alternative,
                    null,
                    Modifier.size(30.dp),
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
                AnimatedVisibility(cover != null, Modifier.fillMaxSize()) {
                    if (cover != null)
                        Image(cover, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
                time?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.background.copy(alpha = .8f),
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        shape = SuggestionChipDefaults.shape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                    ) {
                        Text(
                            time.timeString,
                            Modifier.padding(6.dp),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
                progress?.let {
                    LinearProgressIndicator(
                        progress = { it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent,
                        gapSize = 0.dp,
                        strokeCap = StrokeCap.Square,
                        drawStopIndicator = {}
                    )
                }
            }
        },
        trailingContent,
    )
}