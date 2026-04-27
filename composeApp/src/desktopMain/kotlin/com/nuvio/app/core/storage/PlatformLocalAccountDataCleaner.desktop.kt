package com.nuvio.app.core.storage

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object PlatformLocalAccountDataCleaner {
    actual fun wipe() {
        DesktopTextStorage.removeKnownLocalAccountFiles()
    }
}
