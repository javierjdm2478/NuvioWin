package com.nuvio.app.features.player

import java.util.Locale

internal actual object DeviceLanguagePreferences {
    actual fun preferredLanguageCodes(): List<String> {
        val defaultLocale = Locale.getDefault()
        return listOfNotNull(
            defaultLocale.toLanguageTag(),
            defaultLocale.language,
            "en",
        )
            .mapNotNull(::normalizeLanguageCode)
            .distinct()
    }
}
