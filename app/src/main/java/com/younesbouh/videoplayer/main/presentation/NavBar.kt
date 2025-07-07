package com.younesbouh.videoplayer.main.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.younesbouh.videoplayer.core.presentation.util.composables.isCompact
import com.younesbouh.videoplayer.main.domain.models.NavRoutes
import com.younesbouh.videoplayer.main.domain.models.Routes

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    state: Int,
    navigate: (NavRoutes) -> Unit,
) {
    if (isCompact) {
        NavigationBar(modifier) {
            Routes.entries.forEachIndexed { index, screen ->
                NavigationBarItem(
                    modifier = Modifier.testTag("nav_${screen.name.lowercase()}"),
                    selected = index == state,
                    alwaysShowLabel = false,
                    icon = {
                        AnimatedContent(index == state, transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }, label = "") { selected ->
                            if (selected) Icon(screen.filledIcon, null)
                            else Icon(screen.icon, null)
                        }
                    },
                    label = {
                        Text(
                            stringResource(screen.title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        navigate(screen.destination)
                    },
                )
            }
        }
    } else {
        NavigationRail(
            modifier = modifier.displayCutoutPadding(),
            windowInsets = WindowInsets.systemBars,
        ) {
            Routes.entries.forEachIndexed { index, screen ->
                NavigationRailItem(
                    selected = index == state,
                    alwaysShowLabel = false,
                    icon = {
                        Icon(screen.icon, null)
                    },
                    label = {
                        Text(stringResource(screen.title))
                    },
                    onClick = {
                        navigate(screen.destination)
                    },
                )
            }
        }
    }
}
