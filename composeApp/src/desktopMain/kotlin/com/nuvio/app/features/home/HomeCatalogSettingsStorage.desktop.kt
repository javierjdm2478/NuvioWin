package com.nuvio.app.features.home

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object HomeCatalogSettingsStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_home_catalog_settings.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_home_catalog_settings.json")
    }
}
