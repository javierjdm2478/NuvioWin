package com.nuvio.app.features.watchprogress

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object ContinueWatchingPreferencesStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_continue_watching_preferences.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_continue_watching_preferences.json")
    }
}
