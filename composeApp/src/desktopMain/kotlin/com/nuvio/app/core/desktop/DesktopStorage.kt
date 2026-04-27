package com.nuvio.app.core.desktop

import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE
import java.util.Properties

internal object DesktopPaths {
    val appData: Path by lazy {
        val roaming = System.getenv("APPDATA")?.takeIf { it.isNotBlank() }
        val base = roaming?.let(Paths::get)
            ?: Paths.get(System.getProperty("user.home"), "AppData", "Roaming")
        base.resolve("NuvioWin").also { Files.createDirectories(it) }
    }

    val downloads: Path by lazy {
        appData.resolve("Downloads").also { Files.createDirectories(it) }
    }
}

internal object DesktopTextStorage {
    fun read(vararg parts: String): String? {
        val file = resolve(*parts)
        return runCatching {
            if (Files.exists(file)) {
                Files.readString(file, StandardCharsets.UTF_8)
            } else {
                null
            }
        }.getOrNull()
    }

    fun write(payload: String, vararg parts: String) {
        val file = resolve(*parts)
        Files.createDirectories(file.parent)
        Files.writeString(
            file,
            payload,
            StandardCharsets.UTF_8,
            CREATE,
            WRITE,
            TRUNCATE_EXISTING,
        )
    }

    fun remove(vararg parts: String): Boolean =
        runCatching { Files.deleteIfExists(resolve(*parts)) }.getOrDefault(false)

    fun removeKnownLocalAccountFiles() {
        val names = listOf(
            "nuvio_addons.properties",
            "nuvio_library",
            "nuvio_home_catalog_settings.json",
            "nuvio_player_settings.properties",
            "nuvio_profile_cache.json",
            "nuvio_avatar_cache.json",
            "nuvio_profile_pin_cache",
            "nuvio_theme_settings.properties",
            "nuvio_poster_card_style.json",
            "nuvio_mdblist_settings.properties",
            "nuvio_trakt_auth.json",
            "nuvio_trakt_library.json",
            "nuvio_watched",
            "nuvio_stream_link_cache",
            "nuvio_continue_watching_preferences.json",
            "nuvio_continue_watching_enrichment",
            "nuvio_episode_release_notifications.json",
            "nuvio_watch_progress",
            "nuvio_resume_prompt.properties",
            "nuvio_search_history.json",
            "nuvio_collection.json",
            "nuvio_downloads.json",
            "nuvio_meta_screen_settings.json",
            "nuvio_tmdb_settings.properties",
            "nuvio_trakt_comments.properties",
        )
        names.forEach { name ->
            val path = DesktopPaths.appData.resolve(name)
            runCatching {
                if (Files.isDirectory(path)) {
                    Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .forEach(Files::deleteIfExists)
                } else {
                    Files.deleteIfExists(path)
                }
            }
        }
    }

    private fun resolve(vararg parts: String): Path {
        require(parts.isNotEmpty()) { "At least one storage path part is required." }
        return parts.fold(DesktopPaths.appData) { current, part -> current.resolve(safePathPart(part)) }
    }
}

internal class DesktopKeyValueStore(private val name: String) {
    private val lock = Any()
    private val properties = Properties()
    private var loaded = false
    private val file: Path
        get() = DesktopPaths.appData.resolve("$name.properties")

    fun contains(key: String): Boolean = synchronized(lock) {
        loadLocked()
        properties.containsKey(key)
    }

    fun getString(key: String): String? = synchronized(lock) {
        loadLocked()
        properties.getProperty(key)
    }

    fun putString(key: String, value: String?) = synchronized(lock) {
        loadLocked()
        if (value == null) {
            properties.remove(key)
        } else {
            properties.setProperty(key, value)
        }
        flushLocked()
    }

    fun getBoolean(key: String): Boolean? =
        getString(key)?.toBooleanStrictOrNull()

    fun putBoolean(key: String, value: Boolean) =
        putString(key, value.toString())

    fun getInt(key: String): Int? =
        getString(key)?.toIntOrNull()

    fun putInt(key: String, value: Int) =
        putString(key, value.toString())

    fun getFloat(key: String): Float? =
        getString(key)?.toFloatOrNull()

    fun putFloat(key: String, value: Float) =
        putString(key, value.toString())

    fun getStringSet(key: String): Set<String>? =
        getString(key)?.lineSequence()?.filter { it.isNotBlank() }?.toSet()

    fun putStringSet(key: String, values: Set<String>) =
        putString(key, values.joinToString("\n"))

    fun remove(key: String) =
        putString(key, null)

    fun clear(keys: Iterable<String>) = synchronized(lock) {
        loadLocked()
        keys.forEach(properties::remove)
        flushLocked()
    }

    private fun loadLocked() {
        if (loaded) return
        val currentFile = file
        if (Files.exists(currentFile)) {
            Files.newBufferedReader(currentFile, StandardCharsets.UTF_8).use(properties::load)
        }
        loaded = true
    }

    private fun flushLocked() {
        val currentFile = file
        Files.createDirectories(currentFile.parent)
        Files.newBufferedWriter(
            currentFile,
            StandardCharsets.UTF_8,
            CREATE,
            WRITE,
            TRUNCATE_EXISTING,
        ).use { writer ->
            properties.store(writer, "NuvioWin desktop settings")
        }
    }
}

internal fun safePathPart(value: String): String =
    value.trim()
        .ifBlank { "default" }
        .replace(Regex("[^A-Za-z0-9._ -]"), "_")
        .take(120)

internal fun pathFromLocalFileUri(uri: String?): Path? {
    val value = uri?.trim()?.takeIf { it.isNotBlank() } ?: return null
    return runCatching {
        if (value.startsWith("file:", ignoreCase = true)) {
            Paths.get(URI(value))
        } else {
            Paths.get(value)
        }
    }.getOrNull()
}
