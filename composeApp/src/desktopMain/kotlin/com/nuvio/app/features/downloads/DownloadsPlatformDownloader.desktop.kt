package com.nuvio.app.features.downloads

import com.nuvio.app.core.desktop.DesktopPaths
import com.nuvio.app.core.desktop.pathFromLocalFileUri
import com.nuvio.app.core.desktop.safePathPart
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

internal actual object DownloadsPlatformDownloader {
    actual fun start(
        request: DownloadPlatformRequest,
        onProgress: (downloadedBytes: Long, totalBytes: Long?) -> Unit,
        onSuccess: (localFileUri: String, totalBytes: Long?) -> Unit,
        onFailure: (message: String) -> Unit,
    ): DownloadsTaskHandle {
        val cancelled = AtomicBoolean(false)
        val safeFileName = safePathPart(request.destinationFileName)
        val partialFile = DesktopPaths.downloads.resolve("$safeFileName.part")
        val finalFile = DesktopPaths.downloads.resolve(safeFileName)

        val worker = thread(
            start = true,
            isDaemon = true,
            name = "NuvioWin-download-$safeFileName",
        ) {
            runCatching {
                Files.createDirectories(DesktopPaths.downloads)
                val connection = (URL(request.sourceUrl).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 15_000
                    readTimeout = 30_000
                    request.sourceHeaders.forEach { (key, value) ->
                        setRequestProperty(key, value)
                    }
                }

                connection.inputStream.use { input ->
                    Files.newOutputStream(partialFile).use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var downloaded = 0L
                        val total = connection.contentLengthLong.takeIf { it > 0L }
                        while (!cancelled.get()) {
                            val read = input.read(buffer)
                            if (read < 0) break
                            output.write(buffer, 0, read)
                            downloaded += read.toLong()
                            onProgress(downloaded, total)
                        }
                        if (cancelled.get()) return@thread
                        output.flush()
                        Files.move(
                            partialFile,
                            finalFile,
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE,
                        )
                        onSuccess(finalFile.toUri().toString(), total)
                    }
                }
            }.onFailure { error ->
                if (!cancelled.get()) {
                    onFailure(error.message ?: "Download failed.")
                }
            }
        }

        return object : DownloadsTaskHandle {
            override fun cancel() {
                cancelled.set(true)
                worker.interrupt()
            }
        }
    }

    actual fun removeFile(localFileUri: String?): Boolean {
        val file = pathFromLocalFileUri(localFileUri) ?: return false
        return runCatching { Files.deleteIfExists(file) }.getOrDefault(false)
    }

    actual fun removePartialFile(destinationFileName: String): Boolean =
        runCatching {
            Files.deleteIfExists(DesktopPaths.downloads.resolve("${safePathPart(destinationFileName)}.part"))
        }.getOrDefault(false)
}
