package com.nuvio.app.features.trakt

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object TraktLibraryStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_trakt_library.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_trakt_library.json")
    }
}
