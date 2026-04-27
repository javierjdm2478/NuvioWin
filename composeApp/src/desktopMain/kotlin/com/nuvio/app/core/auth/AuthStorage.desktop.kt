package com.nuvio.app.core.auth

import com.nuvio.app.core.desktop.DesktopKeyValueStore

internal actual object AuthStorage {
    private const val AnonymousUserIdKey = "anonymous_user_id"
    private val store = DesktopKeyValueStore("nuvio_auth")

    actual fun loadAnonymousUserId(): String? =
        store.getString(AnonymousUserIdKey)

    actual fun saveAnonymousUserId(userId: String) {
        store.putString(AnonymousUserIdKey, userId)
    }

    actual fun clearAnonymousUserId() {
        store.remove(AnonymousUserIdKey)
    }
}
