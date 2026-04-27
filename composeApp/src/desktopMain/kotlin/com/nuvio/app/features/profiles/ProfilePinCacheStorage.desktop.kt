package com.nuvio.app.features.profiles

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object ProfilePinCacheStorage {
    actual fun loadPayload(profileIndex: Int): String? =
        DesktopTextStorage.read("nuvio_profile_pin_cache", "$profileIndex.json")

    actual fun savePayload(profileIndex: Int, payload: String) {
        DesktopTextStorage.write(payload, "nuvio_profile_pin_cache", "$profileIndex.json")
    }

    actual fun removePayload(profileIndex: Int) {
        DesktopTextStorage.remove("nuvio_profile_pin_cache", "$profileIndex.json")
    }
}
