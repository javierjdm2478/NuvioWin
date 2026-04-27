package com.nuvio.app.features.watchprogress

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object WatchProgressStorage {
    actual fun loadPayload(profileId: Int): String? =
        DesktopTextStorage.read("nuvio_watch_progress", "$profileId.json")

    actual fun savePayload(profileId: Int, payload: String) {
        DesktopTextStorage.write(payload, "nuvio_watch_progress", "$profileId.json")
    }
}
