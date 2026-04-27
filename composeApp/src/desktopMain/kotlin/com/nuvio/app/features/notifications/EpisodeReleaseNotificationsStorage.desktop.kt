package com.nuvio.app.features.notifications

import com.nuvio.app.core.desktop.DesktopTextStorage

internal actual object EpisodeReleaseNotificationsStorage {
    actual fun loadPayload(): String? =
        DesktopTextStorage.read("nuvio_episode_release_notifications.json")

    actual fun savePayload(payload: String) {
        DesktopTextStorage.write(payload, "nuvio_episode_release_notifications.json")
    }
}
