package com.younesbouh.videoplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.younesbouh.videoplayer.core.presentation.util.composables.SetSystemBarColors
import com.younesbouh.videoplayer.main.domain.models.Routes
import com.younesbouh.videoplayer.main.presentation.AppScreen
import com.younesbouh.videoplayer.main.presentation.viewmodel.MainVM
import com.younesbouh.videoplayer.main.presentation.viewmodel.NavigationVM
import com.younesbouh.videoplayer.player.presentation.PlayerActivity
import com.younesbouh.videoplayer.settings.data.SettingsDataStore
import com.younesbouh.videoplayer.ui.theme.AppTheme
import org.koin.android.ext.android.inject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToLong

class MainActivity : ComponentActivity() {
    val permissions by inject<Array<String>>()
    val dataStore by inject<SettingsDataStore>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isGranted =
            permissions.map {
                ContextCompat
                    .checkSelfPermission(this@MainActivity, it) == PackageManager.PERMISSION_GRANTED
            }.reduce { old, current -> old and current }
        enableEdgeToEdge()
        setContent {
            SetSystemBarColors(dataStore = dataStore)
            val mainVM = koinViewModel<MainVM>()
            val granted by mainVM.granted.collectAsState()
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val isParent =
                currentRoute?.let { route ->
                    Routes.entries.map { it.destination.javaClass.kotlin.qualifiedName }.contains(route)
                } != false
            val navigationVM = koinViewModel<NavigationVM>()
            val navigationState by navigationVM.state.collectAsState()
            val requestPermissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                if (results.containsValue(true)) mainVM.setGranted()
            }
            LaunchedEffect(key1 = currentRoute) {
                Routes
                    .entries
                    .firstOrNull { currentRoute == it.destination::class.qualifiedName }
                    ?.let {
                        navigationVM.update(it.index)
                    }
            }
            LaunchedEffect(Unit) {
                if (isGranted) mainVM.setGranted()
            }
            AppTheme {
                AppScreen(
                    granted,
                    {
                        requestPermissions.launch(permissions)
                    },
                    mainVM,
                    navController,
                    isParent,
                    navigationState
                ) {
                    mainVM.setLastPlayed(it.id)
                    startActivity(Intent(this, PlayerActivity::class.java).apply {
                        putExtra("item", it.path)
                        putExtra("time", (it.progress * it.duration).roundToLong())
                    })
                }
            }
        }
    }
}
