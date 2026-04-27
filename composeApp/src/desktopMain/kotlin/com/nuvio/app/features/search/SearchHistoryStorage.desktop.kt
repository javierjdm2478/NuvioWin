package com.nuvio.app.features.search

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object SearchHistoryStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_search_history.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_search_history.json")
    }
}
