package com.nuvio.app.features.details

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object MetaScreenSettingsStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_meta_screen_settings.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_meta_screen_settings.json")
    }
}
