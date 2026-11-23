package com.dor2500.uptrack.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.dor2500.uptrack.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

data class AppReleaseInfo(
    val version: String,
    val releaseNotes: String,
    val apkDownloadUrl: String,
    val htmlUrl: String
)

sealed interface DownloadState {
    data class Progress(val percent: Int) : DownloadState
    data class Ready(val apkFile: File) : DownloadState
    data class Error(val message: String) : DownloadState
}

class AppUpdater(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun checkForUpdate(): Result<AppReleaseInfo?> = withContext(Dispatchers.IO) {
        runCatching {
            val apiUrl = "https://api.github.com/repos/${BuildConfig.GITHUB_REPO}/releases/latest"
            val request = Request.Builder()
                .url(apiUrl)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "UpTrack/${BuildConfig.VERSION_NAME} (Android)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.code == 404) return@runCatching null
                if (!response.isSuccessful) {
                    throw IllegalStateException("HTTP ${response.code}: ${response.message}")
                }
                val body = response.body?.string() ?: return@runCatching null
                val obj = json.parseToJsonElement(body).jsonObject
                val tag = obj["tag_name"]?.jsonPrimitive?.content.orEmpty()
                val notes = obj["body"]?.jsonPrimitive?.content.orEmpty()
                val htmlUrl = obj["html_url"]?.jsonPrimitive?.content.orEmpty()

                val latestClean = tag.removePrefix("v").substringBefore("-")
                val currentClean = BuildConfig.VERSION_NAME.removePrefix("v").substringBefore("-")

                if (!isNewerVersion(latestClean, currentClean)) {
                    return@runCatching null
                }

                val assets = obj["assets"]?.jsonArray
                val apkUrl = assets
                    ?.firstOrNull {
                        it.jsonObject["name"]?.jsonPrimitive?.content
                            ?.endsWith(".apk", ignoreCase = true) == true
                    }
                    ?.jsonObject?.get("browser_download_url")?.jsonPrimitive?.content
                    ?: htmlUrl

                AppReleaseInfo(
                    version = tag,
                    releaseNotes = notes.ifBlank { "New release available on GitHub." },
                    apkDownloadUrl = apkUrl,
                    htmlUrl = htmlUrl
                )
            }
        }
    }

    fun downloadApk(url: String): Flow<DownloadState> = flow {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "UpTrack/${BuildConfig.VERSION_NAME} (Android)")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    emit(DownloadState.Error("HTTP ${response.code}: ${response.message}"))
                    return@flow
                }
                val body = response.body ?: run {
                    emit(DownloadState.Error("Empty download body"))
                    return@flow
                }
                val totalBytes = body.contentLength()
                val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
                val outFile = File(updatesDir, "UpTrack-latest.apk")

                body.byteStream().use { input ->
                    outFile.outputStream().use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead = 0L
                        var lastPercent = -1
                        while (true) {
                            val read = input.read(buffer)
                            if (read == -1) break
                            output.write(buffer, 0, read)
                            bytesRead += read
                            if (totalBytes > 0) {
                                val percent = ((bytesRead * 100) / totalBytes).toInt().coerceIn(0, 100)
                                if (percent != lastPercent) {
                                    lastPercent = percent
                                    emit(DownloadState.Progress(percent))
                                }
                            }
                        }
                    }
                }
                emit(DownloadState.Ready(outFile))
            }
        } catch (e: Exception) {
            emit(DownloadState.Error(e.message ?: "Download failed"))
        }
    }.flowOn(Dispatchers.IO)

    fun installApk(apkFile: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !context.packageManager.canRequestPackageInstalls()
        ) {
            val settingsIntent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(settingsIntent)
            return
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(installIntent)
    }

    internal fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLen = maxOf(latestParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}
