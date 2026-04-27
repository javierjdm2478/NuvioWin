package com.nuvio.app.features.trakt

import com.nuvio.app.core.desktop.DesktopKeyValueStore
import com.nuvio.app.core.storage.ProfileScopedKey
import com.nuvio.app.core.sync.decodeSyncBoolean
import com.nuvio.app.core.sync.encodeSyncBoolean
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal actual object TraktCommentsStorage {
    private const val enabledKey = "comments_enabled"
    private val store = DesktopKeyValueStore("nuvio_trakt_comments")

    actual fun loadEnabled(): Boolean? {
        val scopedKey = ProfileScopedKey.of(enabledKey)
        return if (store.contains(scopedKey)) store.getBoolean(scopedKey) else null
    }

    actual fun saveEnabled(enabled: Boolean) {
        store.putBoolean(ProfileScopedKey.of(enabledKey), enabled)
    }

    actual fun exportToSyncPayload(): JsonObject = buildJsonObject {
        loadEnabled()?.let { put(enabledKey, encodeSyncBoolean(it)) }
    }

    actual fun replaceFromSyncPayload(payload: JsonObject) {
        store.remove(ProfileScopedKey.of(enabledKey))
        payload.decodeSyncBoolean(enabledKey)?.let(::saveEnabled)
    }
}
