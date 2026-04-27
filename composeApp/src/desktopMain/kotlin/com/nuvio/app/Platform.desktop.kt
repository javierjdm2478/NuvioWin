package com.nuvio.app

class WindowsDesktopPlatform : Platform {
    override val name: String = "Windows Desktop"
}

actual fun getPlatform(): Platform = WindowsDesktopPlatform()

internal actual val isIos: Boolean = false
