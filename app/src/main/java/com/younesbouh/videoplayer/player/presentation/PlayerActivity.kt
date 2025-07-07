package com.younesbouh.videoplayer.player.presentation

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.younesbouh.videoplayer.R
import com.younesbouh.videoplayer.core.presentation.util.composables.SetSystemBarColors
import com.younesbouh.videoplayer.main.domain.events.PlayerEvent
import com.younesbouh.videoplayer.settings.data.SettingsDataStore
import com.younesbouh.videoplayer.ui.theme.AppTheme
import org.koin.android.ext.android.inject
import org.koin.compose.viewmodel.koinViewModel
import timber.log.Timber

private const val ACTION_PLAYER_CONTROL = "player_control"

private const val EXTRA_CONTROL_TYPE = "control_type"
private const val CONTROL_TYPE_START_OR_PAUSE = 1
private const val CONTROL_TYPE_SKIP_NEXT = 2
private const val CONTROL_TYPE_SKIP_PREV = 3
private const val REQUEST_TYPE_START_OR_PAUSE = 4
private const val REQUEST_TYPE_SKIP_NEXT = 5
private const val REQUEST_TYPE_SKIP_PREV = 6

@SuppressLint("UnspecifiedRegisterReceiverFlag", "UnsafeOptInUsageError")
class PlayerActivity : ComponentActivity() {
    val settingsDataStore by inject<SettingsDataStore>()
    lateinit var playerVM: PlayerVM

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || intent.action != ACTION_PLAYER_CONTROL) {
                return
            }
            playerVM.onPlayerEvent(
                when(intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                    CONTROL_TYPE_SKIP_PREV -> PlayerEvent.Backward(10000L)
                    CONTROL_TYPE_START_OR_PAUSE -> {
                        updatePictureInPictureParams(!playerVM.player.isPlaying)
                        PlayerEvent.PlayPause
                    }
                    CONTROL_TYPE_SKIP_NEXT -> PlayerEvent.Forward(10000L)
                    else -> return
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val uri = if (intent.action.equals(Intent.ACTION_VIEW)) intent.data else null
        val item = intent.getStringExtra("item")
        val time = intent.getLongExtra("time", 0)
        if ((uri == null) and (item == null)) return
        val audioManager = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            registerReceiver(
                broadcastReceiver,
                IntentFilter(ACTION_PLAYER_CONTROL),
                RECEIVER_NOT_EXPORTED
            )
        else
            registerReceiver(broadcastReceiver, IntentFilter(ACTION_PLAYER_CONTROL))
        setContent {
            SetSystemBarColors(dataStore = settingsDataStore)
            playerVM = koinViewModel<PlayerVM>()
            val files = playerVM.items.collectAsState().value
            val state = playerVM.playerState.collectAsState().value
            val view = LocalView.current
            LaunchedEffect(Unit) {
                val finishCallback: () -> Unit = {
                    playerVM.onPlayerEvent(PlayerEvent.Play(0, time))
                    playerVM.onPlayerEvent(PlayerEvent.SetBrightness(
                        (android.provider.Settings.System.getInt(
                            contentResolver,
                            android.provider.Settings.System.SCREEN_BRIGHTNESS)  - 1
                                ) / 255f)
                    )
                    updatePictureInPictureParams(true)
                }
                if (uri != null)
                    playerVM.setItem(uri, finishCallback)
                else if (item != null)
                    playerVM.setItem(item, finishCallback)
                view.keepScreenOn = true
            }
            LaunchedEffect(state.playState) {
                updatePictureInPictureParams(playerVM.player.isPlaying)
            }
            LaunchedEffect(state.controlsVisible) {
                if (state.controlsVisible) {
                    WindowInsetsControllerCompat(window, view).show(WindowInsetsCompat.Type.systemBars())
                } else {
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowInsetsControllerCompat(window, view).let { controller ->
                        controller.hide(WindowInsetsCompat.Type.systemBars())
                        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }
            }
            AppTheme {
                PlayerScreen(
                    files,
                    state,
                    playerVM.player,
                    window,
                    audioManager,
                    playerVM::onPlayerEvent,
                    {
                        enterPictureInPictureMode(updatePictureInPictureParams(playerVM.player.isPlaying))
                    }
                ) {
                    finish()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (::playerVM.isInitialized) {
            playerVM.player.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        if (::playerVM.isInitialized) {
            playerVM.resumePlayer()
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        playerVM.updatePictureInPicture(isInPictureInPictureMode)
        playerVM.onPlayerEvent(PlayerEvent.HideVolumeOverlay)
        playerVM.onPlayerEvent(PlayerEvent.HideBrightnessOverlay)
        playerVM.onPlayerEvent(PlayerEvent.HideMoreSheet)
        playerVM.onPlayerEvent(PlayerEvent.HideResizeMode)
        updatePictureInPictureParams(playerVM.player.isPlaying)
    }

    private fun updatePictureInPictureParams(isPlaying: Boolean): PictureInPictureParams {
        val params = PictureInPictureParams.Builder()
            .setActions(
                listOf(
                    createRemoteAction(
                        R.drawable.baseline_replay_10_24,
                        R.string.skip_prev,
                        REQUEST_TYPE_SKIP_PREV,
                        CONTROL_TYPE_SKIP_PREV
                    ),
                    createRemoteAction(
                        if (isPlaying) R.drawable.baseline_pause_24
                        else R.drawable.baseline_play_arrow_24,
                        R.string.play_pause,
                        REQUEST_TYPE_START_OR_PAUSE,
                        CONTROL_TYPE_START_OR_PAUSE
                    ),
                    createRemoteAction(
                        R.drawable.baseline_forward_10_24,
                        R.string.skip_next,
                        REQUEST_TYPE_SKIP_NEXT,
                        CONTROL_TYPE_SKIP_NEXT
                    ),
                )
            )
            .setAspectRatio(
                if (playerVM.player.videoSize.width == 0) Rational(16, 9)
                else
                    Rational(
                        playerVM.player.videoSize.width,
                        playerVM.player.videoSize.height.takeIf { it != 0 } ?: 1
                    )
            )
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setAutoEnterEnabled(true)
                    setSeamlessResizeEnabled(false)
                }
            }
            .build()
        setPictureInPictureParams(params)
        return params
    }

    private fun createRemoteAction(
        @DrawableRes iconResId: Int,
        @StringRes titleResId: Int,
        requestCode: Int,
        controlType: Int
    ): RemoteAction {
        return RemoteAction(
            Icon.createWithResource(this, iconResId),
            getString(titleResId),
            getString(titleResId),
            PendingIntent.getBroadcast(
                this,
                requestCode,
                Intent(ACTION_PLAYER_CONTROL).putExtra(EXTRA_CONTROL_TYPE, controlType).apply {
                    setPackage(packageName)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
