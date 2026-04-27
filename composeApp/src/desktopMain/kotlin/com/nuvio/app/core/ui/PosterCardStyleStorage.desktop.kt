package com.nuvio.app.core.ui

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object PosterCardStyleStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_poster_card_style.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_poster_card_style.json")
    }
}
