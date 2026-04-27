package com.nuvio.app.features.watched

import com.nuvio.app.core.desktop.DesktopTextStorage

actual object WatchedStorage {
    actual fun loadPayload(profileId: Int): String? =
        DesktopTextStorage.read("nuvio_watched", "$profileId.json")

    actual fun savePayload(profileId: Int, payload: String) {
        DesktopTextStorage.write(payload, "nuvio_watched", "$profileId.json")
    }
}
