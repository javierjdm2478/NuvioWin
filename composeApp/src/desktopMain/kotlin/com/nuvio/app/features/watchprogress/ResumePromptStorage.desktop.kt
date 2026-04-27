package com.nuvio.app.features.watchprogress

import com.nuvio.app.core.desktop.DesktopKeyValueStore

internal actual object ResumePromptStorage {
    private const val WasInPlayerKey = "was_in_player"
    private const val LastPlayerVideoIdKey = "last_player_video_id"
    private val store = DesktopKeyValueStore("nuvio_resume_prompt")

    actual fun loadWasInPlayer(): Boolean =
        store.getBoolean(WasInPlayerKey) ?: false

    actual fun saveWasInPlayer(value: Boolean) {
        store.putBoolean(WasInPlayerKey, value)
    }

    actual fun loadLastPlayerVideoId(): String? =
        store.getString(LastPlayerVideoIdKey)

    actual fun saveLastPlayerVideoId(videoId: String?) {
        store.putString(LastPlayerVideoIdKey, videoId?.takeIf { it.isNotBlank() })
    }
}
