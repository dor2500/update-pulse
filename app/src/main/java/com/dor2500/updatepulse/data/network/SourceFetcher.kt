package com.dor2500.updatepulse.data.network

import android.util.Xml
import com.dor2500.updatepulse.BuildConfig
import com.dor2500.updatepulse.data.local.TrackedServiceEntity
import com.dor2500.updatepulse.data.model.SourceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.util.concurrent.TimeUnit

data class FetchedRelease(
    val version: String,
    val title: String,
    val changelog: String,
    val url: String
)

class SourceFetcher {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchLatest(
        service: TrackedServiceEntity,
        githubToken: String = ""
    ): Result<FetchedRelease> =
        withContext(Dispatchers.IO) {
            runCatching {
                when (service.sourceType) {
                    SourceType.GITHUB_RELEASES -> fetchGitHub(
                        repo = service.sourceUrlOrRepo.trim(),
                        includePreReleases = service.includePreReleases,
                        githubToken = githubToken
                    )
                    SourceType.RSS_ATOM -> fetchRssAtom(service.sourceUrlOrRepo.trim())
                    SourceType.JSON_ENDPOINT -> fetchJson(
                        url = service.sourceUrlOrRepo.trim(),
                        keyPath = service.jsonKey.orEmpty().trim()
                    )
                    SourceType.WEBPAGE_REGEX -> fetchWebpage(
                        url = service.sourceUrlOrRepo.trim(),
                        regexPattern = service.regexPattern.orEmpty().trim()
                    )
                }
            }
        }

    private fun executeGet(
        url: String,
        accept: String? = null,
        bearerToken: String = ""
    ): String {
        val builder = Request.Builder()
            .url(url)
            .header("User-Agent", "UpdatePulse/${BuildConfig.VERSION_NAME} (Android)")
        if (accept != null) {
            builder.header("Accept", accept)
        }
        if (bearerToken.isNotBlank()) {
            builder.header("Authorization", "Bearer ${bearerToken.trim()}")
        }
        client.newCall(builder.build()).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("HTTP ${response.code}: ${response.message}")
            }
            return response.body?.string() ?: throw IllegalStateException("Empty response body")
        }
    }

    private fun fetchGitHub(
        repo: String,
        includePreReleases: Boolean,
        githubToken: String
    ): FetchedRelease {
        val cleanRepo = repo.removePrefix("https://github.com/").trim('/')
        require(cleanRepo.contains("/")) { "Invalid GitHub repo format. Expected owner/repo" }

        val url = if (includePreReleases) {
            "https://api.github.com/repos/$cleanRepo/releases?per_page=1"
        } else {
            "https://api.github.com/repos/$cleanRepo/releases/latest"
        }

        val body = executeGet(
            url = url,
            accept = "application/vnd.github+json",
            bearerToken = githubToken
        )
        val element = json.parseToJsonElement(body)
        val releaseObj = if (includePreReleases) {
            element.jsonArray.firstOrNull()?.jsonObject
                ?: throw IllegalStateException("No releases found for $cleanRepo")
        } else {
            element.jsonObject
        }

        val tagName = releaseObj["tag_name"]?.jsonPrimitive?.content.orEmpty()
        val name = releaseObj["name"]?.jsonPrimitive?.content.orEmpty().ifBlank { tagName }
        val changelog = releaseObj["body"]?.jsonPrimitive?.content.orEmpty()
        val htmlUrl = releaseObj["html_url"]?.jsonPrimitive?.content
            ?: "https://github.com/$cleanRepo/releases"

        val version = tagName.ifBlank { name }.ifBlank {
            throw IllegalStateException("No tag_name found in GitHub release")
        }
        return FetchedRelease(
            version = version,
            title = name,
            changelog = changelog.take(1500),
            url = htmlUrl
        )
    }

    private fun fetchRssAtom(url: String): FetchedRelease {
        val xmlContent = executeGet(url)
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xmlContent))

        var insideItem = false
        var isAtom = false
        var title = ""
        var link = ""
        var description = ""
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    val tag = parser.name.lowercase()
                    if (tag == "feed") isAtom = true
                    if (tag == "item" || tag == "entry") {
                        insideItem = true
                    } else if (insideItem) {
                        when (tag) {
                            "title" -> if (title.isEmpty()) title = readTagText(parser)
                            "link" -> {
                                if (isAtom) {
                                    val href = parser.getAttributeValue(null, "href")
                                    if (!href.isNullOrBlank() && link.isEmpty()) link = href
                                } else {
                                    val text = readTagText(parser)
                                    if (text.isNotBlank() && link.isEmpty()) link = text
                                }
                            }
                            "description", "summary", "content" -> {
                                if (description.isEmpty()) description = readTagText(parser)
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    val tag = parser.name.lowercase()
                    if (insideItem && (tag == "item" || tag == "entry")) {
                        break
                    }
                }
            }
            eventType = parser.next()
        }

        if (title.isBlank()) {
            throw IllegalStateException("No feed items found")
        }

        val extractedVersion = extractVersionFromTitle(title)
        val cleanChangelog = stripHtml(description).take(1200)
        return FetchedRelease(
            version = extractedVersion,
            title = title.trim(),
            changelog = cleanChangelog,
            url = link.ifBlank { url }
        )
    }

    private fun readTagText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text ?: ""
            parser.nextTag()
        }
        return result.trim()
    }

    private fun extractVersionFromTitle(title: String): String {
        val versionRegex = Regex("""v?(\d+(?:\.\d+){1,4}(?:[-._]?(?:alpha|beta|rc|canary|dev|pre)\d*)?)""", RegexOption.IGNORE_CASE)
        return versionRegex.find(title)?.value ?: title.take(40).trim()
    }

    private fun fetchJson(url: String, keyPath: String): FetchedRelease {
        val body = executeGet(url, accept = "application/json")
        val root = json.parseToJsonElement(body)
        val path = keyPath.ifBlank { "version" }
        val resolved = resolveJsonPath(root, path)
            ?: throw IllegalStateException("Key '$path' not found in JSON")

        val version = when (resolved) {
            is JsonPrimitive -> resolved.content
            else -> resolved.toString()
        }

        return FetchedRelease(
            version = version,
            title = version,
            changelog = "Detected from JSON key '$path'",
            url = url
        )
    }

    private fun resolveJsonPath(element: JsonElement, path: String): JsonElement? {
        var current: JsonElement = element
        for (segment in path.split('.')) {
            current = when (current) {
                is JsonObject -> current[segment] ?: return null
                is JsonArray -> {
                    val idx = segment.toIntOrNull() ?: 0
                    current.getOrNull(idx) ?: return null
                }
                else -> return null
            }
        }
        return current
    }

    private fun fetchWebpage(url: String, regexPattern: String): FetchedRelease {
        val html = executeGet(url)
        val pattern = regexPattern.ifBlank { """v?(\d+\.\d+(?:\.\d+)+)""" }
        val regex = Regex(pattern)
        val match = regex.find(html)
            ?: throw IllegalStateException("Pattern did not match anything on page")
        val version = if (match.groupValues.size > 1 && match.groupValues[1].isNotBlank()) {
            match.groupValues[1]
        } else {
            match.value
        }
        return FetchedRelease(
            version = version.trim(),
            title = version.trim(),
            changelog = "Detected on webpage via pattern",
            url = url
        )
    }

    private fun stripHtml(raw: String): String {
        return raw
            .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("<[^>]*>"), "")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
    }
}
