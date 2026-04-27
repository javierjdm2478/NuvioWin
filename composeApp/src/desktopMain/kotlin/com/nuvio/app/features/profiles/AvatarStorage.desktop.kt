package com.nuvio.app.features.profiles

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object AvatarStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_avatar_cache.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_avatar_cache.json")
    }
}
