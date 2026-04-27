package com.nuvio.app.features.addons

import com.nuvio.app.core.desktop.DesktopKeyValueStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

internal actual object AddonStorage {
    private const val InstalledUrlsKeyPrefix = "installed_addon_urls"
    private val store = DesktopKeyValueStore("nuvio_addons")

    actual fun loadInstalledAddonUrls(profileId: Int): List<String> =
        store.getString("$InstalledUrlsKeyPrefix.$profileId")
            ?.lineSequence()
            ?.map(String::trim)
            ?.filter(String::isNotBlank)
            ?.toList()
            .orEmpty()

    actual fun saveInstalledAddonUrls(profileId: Int, urls: List<String>) {
        store.putString(
            "$InstalledUrlsKeyPrefix.$profileId",
            urls.map(String::trim).filter(String::isNotBlank).joinToString("\n"),
        )
    }
}

private val desktopHttpClient = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 30_000
    }
    followRedirects = true
}

actual suspend fun httpGetText(url: String): String =
    httpGetTextWithHeaders(url, emptyMap())

actual suspend fun httpPostJson(url: String, body: String): String =
    httpPostJsonWithHeaders(url, body, emptyMap())

actual suspend fun httpGetTextWithHeaders(
    url: String,
    headers: Map<String, String>,
): String =
    httpRequestRaw("GET", url, headers, "").body

actual suspend fun httpPostJsonWithHeaders(
    url: String,
    body: String,
    headers: Map<String, String>,
): String =
    httpRequestRaw("POST", url, headers, body).body

actual suspend fun httpRequestRaw(
    method: String,
    url: String,
    headers: Map<String, String>,
    body: String,
): RawHttpResponse {
    val response = desktopHttpClient.request(url) {
        this.method = HttpMethod(method.uppercase())
        headers.forEach { (key, value) ->
            header(key, value)
        }
        if (body.isNotBlank()) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    return RawHttpResponse(
        status = response.status.value,
        statusText = response.status.description,
        url = response.request.url.toString(),
        body = response.bodyAsText(),
        headers = response.headers.entries().associate { (key, values) ->
            key to values.joinToString(", ")
        },
    )
}
