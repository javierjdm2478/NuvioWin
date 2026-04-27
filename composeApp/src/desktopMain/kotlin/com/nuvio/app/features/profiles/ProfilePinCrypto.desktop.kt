package com.nuvio.app.features.profiles

import java.security.MessageDigest

internal actual object ProfilePinCrypto {
    actual fun sha256Hex(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { byte ->
            "%02x".format(byte.toInt() and 0xFF)
        }
    }
}
