package com.dor2500.uptrack.data.local

import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppScanMatch(
    val serviceId: String,
    val serviceName: String,
    val packageName: String
)

class InstalledAppScanner(private val context: Context) {

    suspend fun scan(allServices: List<TrackedServiceEntity>): List<AppScanMatch> =
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val servicesById = allServices.associateBy { it.id }

            // Build normalized name index for fallback matching of launcher apps
            val normalizedCatalog = allServices
                .filter { !it.isCustom }
                .mapNotNull { service ->
                    val cleanName = service.name
                        .substringBefore(" (")
                        .trim()
                    val norm = normalize(cleanName)
                    if (norm.length >= 4) norm to service else null
                }

            val matched = linkedMapOf<String, AppScanMatch>()

            for (appInfo in installedApps) {
                val pkg = appInfo.packageName ?: continue
                if (pkg == context.packageName) continue

                // 1. Direct package-name match (works for both user & key system/Google apps)
                val mappedId = PACKAGE_TO_SERVICE_ID[pkg]
                if (mappedId != null) {
                    val service = servicesById[mappedId]
                    if (service != null && !matched.containsKey(service.id)) {
                        matched[service.id] = AppScanMatch(
                            serviceId = service.id,
                            serviceName = service.name,
                            packageName = pkg
                        )
                        continue
                    }
                }

                // 2. Fallback label match only for user-launchable apps to avoid system noise
                val isLaunchable = pm.getLaunchIntentForPackage(pkg) != null
                if (!isLaunchable) continue

                val appLabel = pm.getApplicationLabel(appInfo).toString()
                val normLabel = normalize(appLabel)
                if (normLabel.length < 4) continue

                val labelMatch = normalizedCatalog.firstOrNull { (normService, _) ->
                    normLabel == normService
                }?.second

                if (labelMatch != null && !matched.containsKey(labelMatch.id)) {
                    matched[labelMatch.id] = AppScanMatch(
                        serviceId = labelMatch.id,
                        serviceName = labelMatch.name,
                        packageName = pkg
                    )
                }
            }

            matched.values.toList()
        }

    private fun normalize(value: String): String =
        value.lowercase().replace(Regex("[^a-z0-9\u0590-\u05ff]"), "")

    companion object {
        private val PACKAGE_TO_SERVICE_ID: Map<String, String> = mapOf(
            // Google & Pixel
            "com.google.android.gms" to "google_play_services",
            "com.android.vending" to "google_play_store",
            "com.google.android.GoogleCamera" to "google_pixel_camera",
            "com.google.android.webview" to "google_webview",
            "com.google.android.apps.wear.companion" to "google_pixel_watch",
            "com.google.android.googlequicksearchbox" to "google_app",
            "com.google.android.apps.messaging" to "google_messages",
            "com.google.android.inputmethod.latin" to "google_gboard",
            "com.google.android.apps.photos" to "google_photos",
            "com.google.android.apps.maps" to "google_maps",
            "com.google.android.dialer" to "google_phone",
            "com.google.android.contacts" to "google_contacts",
            "com.google.android.deskclock" to "google_clock",
            "com.google.android.calculator" to "google_calculator",
            "com.google.android.projection.gearhead" to "google_android_auto",
            "com.google.android.apps.chromecast.app" to "google_home",
            "com.google.android.apps.nexuslauncher" to "google_pixel_launcher",
            "com.google.android.apps.wearables.maestro.companion" to "google_pixel_buds",
            "com.google.android.gm" to "google_gmail",
            "com.google.android.calendar" to "google_calendar",
            "com.google.android.apps.docs" to "google_drive",
            "com.google.android.apps.docs.editors.docs" to "google_docs",
            "com.google.android.apps.docs.editors.sheets" to "google_sheets",
            "com.google.android.apps.docs.editors.slides" to "google_slides",
            "com.google.android.keep" to "google_keep",
            "com.google.android.apps.translate" to "google_translate",
            "com.google.android.apps.walletnfcrel" to "google_wallet",
            "com.google.android.apps.authenticator2" to "google_authenticator",
            "com.google.android.youtube" to "google_youtube",
            "com.google.android.apps.youtube.music" to "google_yt_music",
            "com.google.android.apps.nbu.files" to "google_files",
            "com.google.android.apps.adm" to "google_find_my",

            // Messaging & Social
            "com.whatsapp" to "msg_whatsapp_beta",
            "com.whatsapp.w4b" to "msg_whatsapp_business",
            "org.telegram.messenger" to "msg_telegram",
            "org.telegram.messenger.web" to "msg_telegram",
            "org.thunderdog.challegram" to "msg_telegram_x",
            "nekox.messenger" to "msg_nekogram",
            "com.discord" to "msg_discord",
            "org.thoughtcrime.securesms" to "msg_signal",
            "com.instagram.android" to "msg_instagram",
            "com.instagram.barcelona" to "msg_threads",
            "com.facebook.katana" to "msg_facebook",
            "com.facebook.orca" to "msg_messenger",
            "com.twitter.android" to "msg_x_twitter",
            "com.reddit.frontpage" to "msg_reddit",
            "com.Slack" to "msg_slack",
            "com.microsoft.teams" to "msg_teams",
            "us.zoom.videomeetings" to "msg_zoom",
            "com.snapchat.android" to "msg_snapchat",
            "com.zhiliaoapp.musically" to "msg_tiktok",
            "com.linkedin.android" to "msg_linkedin",
            "com.viber.voip" to "msg_viber",
            "im.vector.app" to "msg_element",
            "network.loki.messenger" to "msg_session",
            "chat.simplex.app" to "msg_simplex",
            "xyz.blueskyweb.app" to "msg_bluesky",
            "org.joinmastodon.android" to "msg_mastodon",
            "com.keylesspalace.tusky" to "msg_tusky",
            "net.thunderbird.android" to "msg_thunderbird",
            "ch.protonmail.android" to "msg_proton_mail",
            "de.tutao.tutanota" to "msg_tuta_mail",

            // Browsers
            "com.android.chrome" to "browser_chrome_stable",
            "com.chrome.beta" to "browser_chrome_beta",
            "com.chrome.canary" to "browser_chrome_canary",
            "org.mozilla.firefox" to "browser_firefox",
            "org.mozilla.firefox_beta" to "browser_firefox_beta",
            "org.mozilla.fenix" to "browser_firefox_nightly",
            "org.mozilla.focus" to "browser_firefox_focus",
            "com.brave.browser" to "browser_brave",
            "com.vivaldi.browser" to "browser_vivaldi_stable",
            "com.vivaldi.browser.snapshot" to "browser_vivaldi",
            "com.microsoft.emmx" to "browser_edge",
            "com.microsoft.emmx.canary" to "browser_edge_canary",
            "com.opera.browser" to "browser_opera",
            "com.opera.gx" to "browser_opera_gx",
            "com.duckduckgo.mobile.android" to "browser_duckduckgo",

            // Android Power-User & Open Source
            "com.aurora.store" to "gl_aurora_store",
            "org.fdroid.fdroid" to "gl_fdroid_client",
            "app.organicmaps" to "gl_organic_maps",

            // Israel Regional
            "com.waze" to "il_waze",
            "com.tranzmate" to "il_moovit",
            "com.bnhp.payments.app" to "il_bit",
            "com.payboxapp" to "il_paybox",
            "com.hopon.ravkav" to "il_ravkav",
            "com.unicell.pango" to "il_pango",
            "com.cellopark.android" to "il_cello",
            "com.tzofar.redalert" to "il_tzofar",
            "com.alert.meserhadash" to "il_oref",
            "com.keshet.mako.vod" to "il_12plus",
            "com.kan.android" to "il_kan",

            // Global Media / Security / AI
            "com.openai.chatgpt" to "ai_chatgpt",
            "com.anthropic.claude" to "ai_claude",
            "com.google.android.apps.bard" to "ai_gemini",
            "ai.perplexity.app.android" to "ai_perplexity",
            "com.microsoft.copilot" to "ai_copilot",
            "com.x8bit.bitwarden" to "sec_bitwarden",
            "com.onepassword.android" to "sec_1password",
            "ch.protonvpn.android" to "sec_protonvpn",
            "com.tailscale.ipn" to "sec_tailscale",
            "com.wireguard.android" to "sec_wireguard",
            "net.mullvad.mullvadvpn" to "sec_mullvad",
            "com.adguard.android" to "sec_adguard",
            "io.homeassistant.companion.android" to "sm_home_assistant",
            "com.plexapp.android" to "sm_plex",
            "org.jellyfin.mobile" to "sm_jellyfin",
            "com.spotify.music" to "sm_spotify",
            "com.netflix.mediaclient" to "sm_netflix",
            "org.videolan.vlc" to "sm_vlc",
            "com.termux" to "pw_termux",
            "moe.shizuku.privileged.api" to "pw_shizuku",
            "com.topjohnwu.magisk" to "pw_magisk"
        )
    }
}
