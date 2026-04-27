package com.nuvio.app.features.settings

import com.nuvio.app.core.desktop.DesktopKeyValueStore
import com.nuvio.app.core.storage.ProfileScopedKey
import com.nuvio.app.core.sync.decodeSyncBoolean
import com.nuvio.app.core.sync.decodeSyncString
import com.nuvio.app.core.sync.encodeSyncBoolean
import com.nuvio.app.core.sync.encodeSyncString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal actual object ThemeSettingsStorage {
    private const val selectedThemeKey = "selected_theme"
    private const val amoledEnabledKey = "amoled_enabled"
    private const val selectedAppLanguageKey = "selected_app_language"
    private val syncKeys = listOf(selectedThemeKey, amoledEnabledKey, selectedAppLanguageKey)
    private val store = DesktopKeyValueStore("nuvio_theme_settings")

    actual fun loadSelectedTheme(): String? =
        store.getString(ProfileScopedKey.of(selectedThemeKey))

    actual fun saveSelectedTheme(themeName: String) {
        store.putString(ProfileScopedKey.of(selectedThemeKey), themeName)
    }

    actual fun loadAmoledEnabled(): Boolean? {
        val scopedKey = ProfileScopedKey.of(amoledEnabledKey)
        return if (store.contains(scopedKey)) store.getBoolean(scopedKey) else null
    }

    actual fun saveAmoledEnabled(enabled: Boolean) {
        store.putBoolean(ProfileScopedKey.of(amoledEnabledKey), enabled)
    }

    actual fun loadSelectedAppLanguage(): String? =
        store.getString(ProfileScopedKey.of(selectedAppLanguageKey))

    actual fun saveSelectedAppLanguage(languageCode: String) {
        store.putString(ProfileScopedKey.of(selectedAppLanguageKey), languageCode)
    }

    actual fun applySelectedAppLanguage(languageCode: String) = Unit

    actual fun exportToSyncPayload(): JsonObject = buildJsonObject {
        loadSelectedTheme()?.let { put(selectedThemeKey, encodeSyncString(it)) }
        loadAmoledEnabled()?.let { put(amoledEnabledKey, encodeSyncBoolean(it)) }
        loadSelectedAppLanguage()?.let { put(selectedAppLanguageKey, encodeSyncString(it)) }
    }

    actual fun replaceFromSyncPayload(payload: JsonObject) {
        store.clear(syncKeys.map(ProfileScopedKey::of))
        payload.decodeSyncString(selectedThemeKey)?.let(::saveSelectedTheme)
        payload.decodeSyncBoolean(amoledEnabledKey)?.let(::saveAmoledEnabled)
        payload.decodeSyncString(selectedAppLanguageKey)?.let(::saveSelectedAppLanguage)
        applySelectedAppLanguage(loadSelectedAppLanguage() ?: AppLanguage.ENGLISH.code)
    }
}
