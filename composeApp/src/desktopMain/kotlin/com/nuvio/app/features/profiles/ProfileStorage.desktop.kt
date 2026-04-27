package com.nuvio.app.features.profiles

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object ProfileStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_profile_cache.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_profile_cache.json")
    }
}
