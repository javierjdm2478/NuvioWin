package com.nuvio.app.features.player

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.awt.BorderLayout
import java.awt.Desktop
import java.net.URI
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javafx.application.Platform as FxPlatform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.scene.paint.Color as FxColor
import javafx.util.Duration

@Composable
actual fun PlatformPlayerSurface(
    sourceUrl: String,
    sourceAudioUrl: String?,
    sourceHeaders: Map<String, String>,
    sourceResponseHeaders: Map<String, String>,
    useYoutubeChunkedPlayback: Boolean,
    modifier: Modifier,
    playWhenReady: Boolean,
    resizeMode: PlayerResizeMode,
    useNativeController: Boolean,
    onControllerReady: (PlayerEngineController) -> Unit,
    onSnapshot: (PlayerPlaybackSnapshot) -> Unit,
    onError: (String?) -> Unit,
) {
    val currentOnSnapshot = rememberUpdatedState(onSnapshot)
    val currentOnError = rememberUpdatedState(onError)
    val holder = remember { DesktopJavaFxPlayerController() }
    val focusRequester = remember { FocusRequester() }
    val clipboard = LocalClipboardManager.current
    var snapshot by remember { mutableStateOf(PlayerPlaybackSnapshot()) }
    var fallbackMessage by remember(sourceUrl, sourceAudioUrl, sourceHeaders, sourceResponseHeaders, useYoutubeChunkedPlayback) {
        mutableStateOf(
            desktopFallbackReason(
                sourceUrl = sourceUrl,
                sourceAudioUrl = sourceAudioUrl,
                sourceHeaders = sourceHeaders,
                sourceResponseHeaders = sourceResponseHeaders,
                useYoutubeChunkedPlayback = useYoutubeChunkedPlayback,
            ),
        )
    }

    val controller: PlayerEngineController = holder

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(controller) {
        onControllerReady(controller)
    }

    LaunchedEffect(playWhenReady, fallbackMessage) {
        if (fallbackMessage == null) {
            holder.setPlayWhenReady(playWhenReady)
        }
    }

    LaunchedEffect(resizeMode, fallbackMessage) {
        if (fallbackMessage == null) {
            holder.setResizeMode(resizeMode)
        }
    }

    DisposableEffect(sourceUrl, fallbackMessage) {
        if (fallbackMessage == null) {
            holder.load(
                sourceUrl = sourceUrl,
                playWhenReady = playWhenReady,
                resizeMode = resizeMode,
                onSnapshot = { nextSnapshot ->
                    snapshot = nextSnapshot
                    currentOnSnapshot.value(nextSnapshot)
                },
                onFallback = { message ->
                    fallbackMessage = message
                    currentOnError.value(null)
                    currentOnSnapshot.value(PlayerPlaybackSnapshot(isLoading = false))
                },
            )
        } else {
            currentOnError.value(null)
            currentOnSnapshot.value(PlayerPlaybackSnapshot(isLoading = false))
        }

        onDispose {
            holder.dispose()
        }
    }

    Box(
        modifier = modifier
            .background(androidx.compose.ui.graphics.Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .playerKeyboardShortcuts(
                snapshot = snapshot,
                controller = controller,
            ),
    ) {
        val message = fallbackMessage
        if (message == null) {
            SwingPanel(
                factory = { holder.component },
                modifier = Modifier.fillMaxSize(),
                update = {
                    holder.setPlayWhenReady(playWhenReady)
                    holder.setResizeMode(resizeMode)
                },
            )
        } else {
            DesktopPlayerFallback(
                message = message,
                sourceUrl = sourceUrl,
                onOpenExternal = { openExternal(sourceUrl) },
                onCopyLink = { clipboard.setText(AnnotatedString(sourceUrl)) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun Modifier.playerKeyboardShortcuts(
    snapshot: PlayerPlaybackSnapshot,
    controller: PlayerEngineController,
): Modifier =
    onPreviewKeyEvent { event: KeyEvent ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        when (event.key) {
            Key.Spacebar,
            Key.K,
            -> {
                if (snapshot.isPlaying) controller.pause() else controller.play()
                true
            }
            Key.DirectionLeft,
            Key.J,
            -> {
                controller.seekBy(-10_000L)
                true
            }
            Key.DirectionRight,
            Key.L,
            -> {
                controller.seekBy(10_000L)
                true
            }
            else -> false
        }
    }

@Composable
private fun DesktopPlayerFallback(
    message: String,
    sourceUrl: String,
    onOpenExternal: () -> Unit,
    onCopyLink: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            color = androidx.compose.ui.graphics.Color.White,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = sourceUrl,
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
        Row(
            modifier = Modifier.padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = onOpenExternal) {
                Text("Open external player")
            }
            OutlinedButton(onClick = onCopyLink) {
                Text("Copy link")
            }
        }
    }
}

private fun desktopFallbackReason(
    sourceUrl: String,
    sourceAudioUrl: String?,
    sourceHeaders: Map<String, String>,
    sourceResponseHeaders: Map<String, String>,
    useYoutubeChunkedPlayback: Boolean,
): String? {
    if (!sourceAudioUrl.isNullOrBlank()) {
        return "This stream uses a separate audio track. The Windows preview player cannot merge it yet."
    }
    if (sourceHeaders.isNotEmpty() || sourceResponseHeaders.isNotEmpty()) {
        return "This stream requires custom HTTP headers. Open it in an external player or copy the link."
    }
    if (useYoutubeChunkedPlayback) {
        return "Chunked YouTube playback is not available in the Windows preview player."
    }
    val normalized = sourceUrl.trim().lowercase()
    return if (
        normalized.startsWith("http://") ||
        normalized.startsWith("https://") ||
        normalized.startsWith("file:")
    ) {
        null
    } else {
        "This stream type is not supported by the Windows preview player."
    }
}

private fun openExternal(url: String) {
    runCatching {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(url))
        }
    }
}

private class DesktopJavaFxPlayerController : PlayerEngineController {
    val component: JPanel = JPanel(BorderLayout())
    private val fxPanel = JFXPanel()
    private var mediaPlayer: MediaPlayer? = null
    private var mediaView: MediaView? = null
    private var sourceUrl: String? = null
    private var playWhenReady: Boolean = true
    private var resizeMode: PlayerResizeMode = PlayerResizeMode.Fit
    private var onSnapshot: (PlayerPlaybackSnapshot) -> Unit = {}
    private var onFallback: (String) -> Unit = {}

    init {
        component.add(fxPanel, BorderLayout.CENTER)
    }

    fun load(
        sourceUrl: String,
        playWhenReady: Boolean,
        resizeMode: PlayerResizeMode,
        onSnapshot: (PlayerPlaybackSnapshot) -> Unit,
        onFallback: (String) -> Unit,
    ) {
        this.sourceUrl = sourceUrl
        this.playWhenReady = playWhenReady
        this.resizeMode = resizeMode
        this.onSnapshot = onSnapshot
        this.onFallback = onFallback

        FxPlatform.runLater {
            runCatching {
                mediaPlayer?.dispose()
                val media = Media(sourceUrl)
                val player = MediaPlayer(media)
                val view = MediaView(player)
                val root = StackPane(view)
                root.style = "-fx-background-color: black;"
                view.fitWidthProperty().bind(root.widthProperty())
                view.fitHeightProperty().bind(root.heightProperty())
                applyResizeMode(view, resizeMode)

                player.setOnReady {
                    emitSnapshot(player)
                    if (this.playWhenReady) player.play()
                }
                player.setOnError {
                    fallback(player.error?.message ?: "The Windows preview player could not open this stream.")
                }
                media.setOnError {
                    fallback(media.error?.message ?: "The Windows preview player could not open this media.")
                }
                player.setOnEndOfMedia {
                    emitSnapshot(player, isEnded = true)
                }
                player.currentTimeProperty().addListener { _, _, _ -> emitSnapshot(player) }
                player.bufferProgressTimeProperty().addListener { _, _, _ -> emitSnapshot(player) }
                player.statusProperty().addListener { _, _, _ -> emitSnapshot(player) }
                player.rate = 1.0

                mediaPlayer = player
                mediaView = view
                fxPanel.scene = Scene(root, FxColor.BLACK)
                emitSnapshot(player)
            }.onFailure { error ->
                fallback(error.message ?: "The Windows preview player could not initialize JavaFX media.")
            }
        }
    }

    fun dispose() {
        FxPlatform.runLater {
            mediaPlayer?.dispose()
            mediaPlayer = null
            mediaView = null
            fxPanel.scene = null
        }
    }

    fun setPlayWhenReady(value: Boolean) {
        playWhenReady = value
        FxPlatform.runLater {
            val player = mediaPlayer ?: return@runLater
            if (value) player.play() else player.pause()
        }
    }

    fun setResizeMode(value: PlayerResizeMode) {
        resizeMode = value
        FxPlatform.runLater {
            mediaView?.let { applyResizeMode(it, value) }
        }
    }

    override fun play() = setPlayWhenReady(true)

    override fun pause() = setPlayWhenReady(false)

    override fun seekTo(positionMs: Long) {
        FxPlatform.runLater {
            mediaPlayer?.seek(Duration.millis(positionMs.toDouble().coerceAtLeast(0.0)))
        }
    }

    override fun seekBy(offsetMs: Long) {
        FxPlatform.runLater {
            val player = mediaPlayer ?: return@runLater
            val next = player.currentTime.toMillis() + offsetMs.toDouble()
            player.seek(Duration.millis(next.coerceAtLeast(0.0)))
        }
    }

    override fun retry() {
        val currentSource = sourceUrl ?: return
        load(currentSource, playWhenReady, resizeMode, onSnapshot, onFallback)
    }

    override fun setPlaybackSpeed(speed: Float) {
        FxPlatform.runLater {
            mediaPlayer?.rate = speed.toDouble().coerceIn(0.25, 4.0)
        }
    }

    override fun getAudioTracks(): List<AudioTrack> = emptyList()
    override fun getSubtitleTracks(): List<SubtitleTrack> = emptyList()
    override fun selectAudioTrack(index: Int) = Unit
    override fun selectSubtitleTrack(index: Int) = Unit
    override fun setSubtitleUri(url: String) = Unit
    override fun clearExternalSubtitle() = Unit
    override fun clearExternalSubtitleAndSelect(trackIndex: Int) = Unit

    private fun emitSnapshot(player: MediaPlayer, isEnded: Boolean = false) {
        val status = player.status
        val snapshot = PlayerPlaybackSnapshot(
            isLoading = status == MediaPlayer.Status.UNKNOWN ||
                status == MediaPlayer.Status.STALLED ||
                status == MediaPlayer.Status.HALTED,
            isPlaying = status == MediaPlayer.Status.PLAYING,
            isEnded = isEnded,
            durationMs = player.totalDuration.toMillis().toSafeLong(),
            positionMs = player.currentTime.toMillis().toSafeLong(),
            bufferedPositionMs = player.bufferProgressTime.toMillis().toSafeLong(),
            playbackSpeed = player.rate.toFloat(),
        )
        SwingUtilities.invokeLater {
            onSnapshot(snapshot)
        }
    }

    private fun fallback(message: String) {
        SwingUtilities.invokeLater {
            onFallback(message)
        }
    }

    private fun applyResizeMode(view: MediaView, mode: PlayerResizeMode) {
        view.isPreserveRatio = mode == PlayerResizeMode.Fit
        view.setSmooth(true)
    }
}

private fun Double.toSafeLong(): Long =
    if (isFinite() && this > 0.0) toLong() else 0L
