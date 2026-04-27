package com.nuvio.app.features.watchprogress

import com.nuvio.app.core.desktop.DesktopTextStorage
import com.nuvio.app.core.desktop.safePathPart

internal actual object ContinueWatchingEnrichmentStorage {
    actual fun loadPayload(key: String): String? =
        DesktopTextStorage.read("nuvio_continue_watching_enrichment", "${safePathPart(key)}.json")

    actual fun savePayload(key: String, payload: String) {
        DesktopTextStorage.write(payload, "nuvio_continue_watching_enrichment", "${safePathPart(key)}.json")
    }
}
