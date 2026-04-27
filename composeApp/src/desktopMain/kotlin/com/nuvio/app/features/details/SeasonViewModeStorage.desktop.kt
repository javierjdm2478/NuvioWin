package com.nuvio.app.features.details

import com.nuvio.app.core.desktop.DesktopKeyValueStore

internal actual object SeasonViewModeStorage {
    private const val Key = "season_view_mode"
    private val store = DesktopKeyValueStore("nuvio_season_view_mode")

    actual fun load(): SeasonViewMode? =
        store.getString(Key)?.let { value ->
            SeasonViewMode.entries.firstOrNull { it.name == value }
        }

    actual fun save(mode: SeasonViewMode) {
        store.putString(Key, mode.name)
    }
}
