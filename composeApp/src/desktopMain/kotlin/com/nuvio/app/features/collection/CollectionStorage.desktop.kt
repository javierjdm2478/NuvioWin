package com.nuvio.app.features.collection

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object CollectionStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_collection.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_collection.json")
    }
}
