package com.nuvio.app.features.library

import com.nuvio.app.core.desktop.DesktopTextStorage

actual object LibraryStorage {
    actual fun loadPayload(profileId: Int): String? =
        DesktopTextStorage.read("nuvio_library", "$profileId.json")

    actual fun savePayload(profileId: Int, payload: String) {
        DesktopTextStorage.write(payload, "nuvio_library", "$profileId.json")
    }
}
