package com.nuvio.app

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.nuvio.app.core.ui.DesktopBackDispatcher
import java.awt.Taskbar

fun main() = application {
    remember { runCatching { Taskbar.getTaskbar().setIconImage(null) } }

    Window(
        onCloseRequest = ::exitApplication,
        title = "NuvioWin",
        state = rememberWindowState(width = 1280.dp, height = 800.dp),
        onPreviewKeyEvent = ::handleGlobalKey,
    ) {
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable(),
        ) {
            App()
        }
    }
}

private fun handleGlobalKey(event: KeyEvent): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    return when {
        event.key == Key.Escape -> DesktopBackDispatcher.dispatch()
        event.isAltPressed && event.key == Key.DirectionLeft -> DesktopBackDispatcher.dispatch()
        else -> false
    }
}
