package com.dor2500.uptrack.updater

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import java.security.MessageDigest
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
    data class NeedsUninstall(val savedFileName: String) : DownloadState
    data class Error(val message: String) : DownloadState
}

class AppUpdater(private val context: Context) {
    companion object {
        private val LEGACY_PACKAGES = listOf(
            "com.dor2500.update" + "pulse",
            "com.dor2500.update" + "pulse.debug"
        )
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    fun findInstalledLegacyPackages(): List<String> {
        val pm = context.packageManager
        return LEGACY_PACKAGES.filter { pkg ->
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0))
                } else {
                    @Suppress("DEPRECATION")
                    pm.getPackageInfo(pkg, 0)
                }
                true
            }.getOrDefault(false)
        }
    }

    fun requestUninstall(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun hasSameSigningCertificate(apkFile: File): Boolean {
        return runCatching {
            val installedDigests = getInstalledCertDigests(context.packageName)
            val archiveDigests = getArchiveCertDigests(apkFile.absolutePath)
            if (installedDigests.isEmpty() || archiveDigests.isEmpty()) return true
            installedDigests.any { it in archiveDigests }
        }.getOrDefault(true)
    }

    suspend fun saveApkToDownloads(apkFile: File, version: String): String = withContext(Dispatchers.IO) {
        val cleanVer = version.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val fileName = "UpTrack-$cleanVer.apk"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/vnd.android.package-archive")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Could not save APK to Downloads")
            resolver.openOutputStream(uri)?.use { out ->
                apkFile.inputStream().use { input -> input.copyTo(out) }
            }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            fileName
        } else {
            @Suppress("DEPRECATION")
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsDir.mkdirs()
            val outFile = File(downloadsDir, fileName)
            apkFile.copyTo(outFile, overwrite = true)
            fileName
        }
    }

    private fun getInstalledCertDigests(packageName: String): Set<String> {
        val pm = context.packageManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val flags = PackageManager.GET_SIGNING_CERTIFICATES
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, flags)
            }
            val signingInfo = info.signingInfo ?: return emptySet()
            val sigs = if (signingInfo.hasMultipleSigners()) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo.signingCertificateHistory
            }
            sigs.map { sha256Hex(it.toByteArray()) }.toSet()
        } else {
            @Suppress("DEPRECATION")
            val info = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            @Suppress("DEPRECATION")
            info.signatures?.map { sha256Hex(it.toByteArray()) }?.toSet().orEmpty()
        }
    }

    private fun getArchiveCertDigests(apkPath: String): Set<String> {
        val pm = context.packageManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val flags = PackageManager.GET_SIGNING_CERTIFICATES
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageArchiveInfo(apkPath, PackageManager.PackageInfoFlags.of(flags.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageArchiveInfo(apkPath, flags)
            } ?: return emptySet()
            val signingInfo = info.signingInfo ?: return emptySet()
            val sigs = if (signingInfo.hasMultipleSigners()) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo.signingCertificateHistory
            }
            sigs.map { sha256Hex(it.toByteArray()) }.toSet()
        } else {
            @Suppress("DEPRECATION")
            val info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES) ?: return emptySet()
            @Suppress("DEPRECATION")
            info.signatures?.map { sha256Hex(it.toByteArray()) }?.toSet().orEmpty()
        }
    }

    private fun sha256Hex(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02x".format(it) }

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
