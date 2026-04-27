package com.nuvio.app.features.downloads

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object DownloadsStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_downloads.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_downloads.json")
    }
}
