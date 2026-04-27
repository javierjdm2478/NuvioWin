package com.nuvio.app.core.ui

import java.awt.Window
import kotlin.system.exitProcess

actual fun platformExitApp() {
    Window.getWindows().forEach { window ->
        runCatching { window.dispose() }
    }
    exitProcess(0)
}
