package com.younesbouh.videoplayer.welcome.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import com.younesbouh.videoplayer.R

@Composable
fun WelcomeText(modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            "Welcome",
            Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 60.sp
            )
        )
        Text(
            stringResource(R.string.welcome_message),
            Modifier.fillMaxWidth(),
            lineHeight = 36.sp,
            fontSize = 18.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onPermissionRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompact =
        currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    Scaffold(
        modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .add(WindowInsets(bottom = 16.dp)),
    ) {
        if (isCompact)
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.undraw_online_media_opxh),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentScale = ContentScale.Fit,
                )
                WelcomeText(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .weight(1f)
                )
                Button(onPermissionRequest,
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)) {
                    Text(
                        "Grant permission",
                        Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        else
            Row(modifier = Modifier
                .fillMaxSize()
                .padding(it),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.undraw_online_media_opxh),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentScale = ContentScale.Fit,
                )
                Column(Modifier.fillMaxSize().weight(1f)) {
                    WelcomeText(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .weight(1f)
                    )
                    Button(onPermissionRequest,
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)) {
                        Text(
                            "Grant permission",
                            Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen({ })
}
