package com.nuvio.app.features.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize

@Composable
actual fun LockPlayerToLandscape() = Unit

@Composable
actual fun EnterImmersivePlayerMode() = Unit

@Composable
actual fun ManagePlayerPictureInPicture(
    isPlaying: Boolean,
    playerSize: IntSize,
) = Unit

@Composable
actual fun rememberPlayerGestureController(): PlayerGestureController? =
    remember {
        object : PlayerGestureController {
            private var volume = PlayerAudioLevel(fraction = 1f, isMuted = false)

            override fun currentBrightness(): Float? = null

            override fun setBrightness(level: Float): Float? = null

            override fun currentVolume(): PlayerAudioLevel = volume

            override fun setVolume(level: Float): PlayerAudioLevel {
                volume = PlayerAudioLevel(
                    fraction = level.coerceIn(0f, 1f),
                    isMuted = level <= 0f,
                )
                return volume
            }
        }
    }
