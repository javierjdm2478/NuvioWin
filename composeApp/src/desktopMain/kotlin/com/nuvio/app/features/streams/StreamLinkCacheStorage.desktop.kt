package com.nuvio.app.features.streams

import com.nuvio.app.core.desktop.DesktopTextStorage
import com.nuvio.app.core.desktop.safePathPart

internal actual object StreamLinkCacheStorage {
    actual fun loadEntry(hashedKey: String): String? =
        DesktopTextStorage.read("nuvio_stream_link_cache", "${safePathPart(hashedKey)}.json")

    actual fun saveEntry(hashedKey: String, payload: String) {
        DesktopTextStorage.write(payload, "nuvio_stream_link_cache", "${safePathPart(hashedKey)}.json")
    }

    actual fun removeEntry(hashedKey: String) {
        DesktopTextStorage.remove("nuvio_stream_link_cache", "${safePathPart(hashedKey)}.json")
    }
}
