package com.younesbouh.videoplayer.main.presentation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.younesbouh.videoplayer.R
import com.younesbouh.videoplayer.core.presentation.VideoCardListItem
import com.younesbouh.videoplayer.main.domain.events.SearchEvent
import com.younesbouh.videoplayer.main.domain.models.VideoCard
import com.younesbouh.videoplayer.main.presentation.states.SearchState
import com.younesbouh.videoplayer.settings.presentation.SettingsActivity
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    loading: Boolean = false,
    onSearchEvent: (SearchEvent) -> Unit,
    trailingContent: @Composable () -> Unit,
    onLongClick: (VideoCard) -> Unit,
    play: (VideoCard) -> Unit,
) {
    val context = LocalContext.current
    val padding by animateDpAsState(targetValue = if (state.expanded) 0.dp else 16.dp, label = "")
    Box(Modifier
        .padding(bottom = padding, top = padding / 2, start = padding, end = padding)) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.query,
                    onQueryChange = { onSearchEvent(SearchEvent.UpdateQuery(it)) },
                    onSearch = { onSearchEvent(SearchEvent.UpdateQuery(it)) },
                    expanded = state.expanded,
                    placeholder = {
                        Text(stringResource(R.string.search))
                    },
                    leadingIcon = {
                        if (state.expanded) {
                            IconButton(onClick = { onSearchEvent(SearchEvent.Collapse) }) {
                                Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                            }
                        } else {
                            IconButton(onClick = { onSearchEvent(SearchEvent.Expand) }) {
                                Icon(Icons.Default.Search, null)
                            }
                        }
                    },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { context.startActivity(Intent(context, SettingsActivity::class.java)) }) {
                                Icon(Icons.Default.Settings, null)
                            }
                            AnimatedVisibility(!state.expanded) {
                                trailingContent()
                            }
                        }
                    },
                    onExpandedChange = { onSearchEvent(SearchEvent.UpdateExpanded(it)) },
                )
            },
            expanded = state.expanded,
            onExpandedChange = { onSearchEvent(SearchEvent.UpdateExpanded(it)) },
            modifier =
                Modifier
                    .fillMaxWidth(),
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                items(state.result, { it.id }) {
                    VideoCardListItem(
                        it,
                        Modifier.animateItem(),
                        onLongClick = { onLongClick(it) }
                    ) {
                        play(it)
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = loading,
            enter = materialSharedAxisZIn(true),
            exit = materialSharedAxisZOut(true),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SearchBarDefaults.InputFieldHeight)
                    .align(Alignment.BottomCenter)
                    .imePadding(),
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
