package com.nuvio.app.features.trakt

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object TraktAuthStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_trakt_auth.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_trakt_auth.json")
    }
}
