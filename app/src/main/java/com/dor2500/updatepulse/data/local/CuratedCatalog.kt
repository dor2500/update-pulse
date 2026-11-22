package com.dor2500.updatepulse.data.local

import com.dor2500.updatepulse.data.model.ServiceCategory
import com.dor2500.updatepulse.data.model.ServiceRegion
import com.dor2500.updatepulse.data.model.SourceType

object CuratedCatalog {
    private fun gh(
        id: String,
        name: String,
        category: ServiceCategory,
        repo: String,
        color: Long,
        region: ServiceRegion = ServiceRegion.GLOBAL,
        subscribed: Boolean = false,
        preRelease: Boolean = false
    ) = TrackedServiceEntity(
        id = id,
        name = name,
        category = category,
        sourceType = SourceType.GITHUB_RELEASES,
        sourceUrlOrRepo = repo,
        region = region,
        includePreReleases = preRelease,
        accentColorHex = color,
        isSubscribed = subscribed
    )

    private fun rss(
        id: String,
        name: String,
        category: ServiceCategory,
        url: String,
        color: Long,
        region: ServiceRegion = ServiceRegion.GLOBAL,
        subscribed: Boolean = false
    ) = TrackedServiceEntity(
        id = id,
        name = name,
        category = category,
        sourceType = SourceType.RSS_ATOM,
        sourceUrlOrRepo = url,
        region = region,
        accentColorHex = color,
        isSubscribed = subscribed
    )

    private fun apkm(
        id: String,
        name: String,
        category: ServiceCategory,
        slug: String,
        color: Long,
        region: ServiceRegion = ServiceRegion.GLOBAL,
        subscribed: Boolean = false
    ) = rss(
        id = id,
        name = name,
        category = category,
        url = "https://www.apkmirror.com/apk/$slug/feed/",
        color = color,
        region = region,
        subscribed = subscribed
    )

    private fun gl(
        id: String,
        name: String,
        category: ServiceCategory,
        project: String,
        color: Long,
        region: ServiceRegion = ServiceRegion.GLOBAL
    ) = TrackedServiceEntity(
        id = id,
        name = name,
        category = category,
        sourceType = SourceType.GITLAB_RELEASES,
        sourceUrlOrRepo = project,
        region = region,
        accentColorHex = color
    )

    private fun npm(
        id: String,
        name: String,
        category: ServiceCategory,
        pkg: String,
        color: Long
    ) = TrackedServiceEntity(
        id = id,
        name = name,
        category = category,
        sourceType = SourceType.NPM_REGISTRY,
        sourceUrlOrRepo = pkg,
        accentColorHex = color
    )

    private fun pypi(
        id: String,
        name: String,
        category: ServiceCategory,
        pkg: String,
        color: Long
    ) = TrackedServiceEntity(
        id = id,
        name = name,
        category = category,
        sourceType = SourceType.PYPI_PACKAGE,
        sourceUrlOrRepo = pkg,
        accentColorHex = color
    )

    private fun docker(
        id: String,
        name: String,
        category: ServiceCategory,
        image: String,
        color: Long
    ) = TrackedServiceEntity(
        id = id,
        name = name,
        category = category,
        sourceType = SourceType.DOCKER_HUB,
        sourceUrlOrRepo = image,
        accentColorHex = color
    )

    val defaultServices: List<TrackedServiceEntity> = buildList {
        // =====================================================================
        // 1. GOOGLE & PIXEL (35)
        // =====================================================================
        add(rss("google_android_beta", "Android / Pixel Beta Program", ServiceCategory.BETA, "https://android-developers.googleblog.com/feeds/posts/default", 0xFF34D399L, subscribed = true))
        add(TrackedServiceEntity(id = "google_pixel_ota", name = "Google Pixel Factory Images & OTA", category = ServiceCategory.GOOGLE_PIXEL, sourceType = SourceType.WEBPAGE_REGEX, sourceUrlOrRepo = "https://developers.google.com/android/ota", regexPattern = """(\d+\.\d+\.\d+\s*\([A-Z0-9.]+\))""", accentColorHex = 0xFF60A5FAL))
        add(rss("google_security_bulletin", "Android Security Bulletin", ServiceCategory.GOOGLE_PIXEL, "https://source.android.com/docs/security/bulletin/rss.xml", 0xFFEF4444L))
        add(apkm("google_play_services", "Google Play Services", ServiceCategory.GOOGLE_PIXEL, "google-inc/google-play-services", 0xFF38BDF8L))
        add(apkm("google_play_store", "Google Play Store", ServiceCategory.GOOGLE_PIXEL, "google-inc/google-play-store", 0xFF22C55EL))
        add(apkm("google_pixel_camera", "Pixel Camera (Google Camera)", ServiceCategory.GOOGLE_PIXEL, "google-inc/camera", 0xFF818CF8L))
        add(apkm("google_webview", "Android System WebView", ServiceCategory.GOOGLE_PIXEL, "google-inc/android-system-webview", 0xFF2DD4BFL))
        add(apkm("google_pixel_watch", "Pixel Watch App", ServiceCategory.GOOGLE_PIXEL, "google-inc/google-pixel-watch", 0xFFA78BFAL))
        add(apkm("google_app", "Google App Beta", ServiceCategory.BETA, "google-inc/google-search", 0xFF4285F4L))
        add(apkm("google_messages", "Google Messages", ServiceCategory.GOOGLE_PIXEL, "google-inc/messenger-google-inc", 0xFF3B82F6L))
        add(apkm("google_gboard", "Gboard", ServiceCategory.GOOGLE_PIXEL, "google-inc/gboard", 0xFF60A5FAL))
        add(apkm("google_photos", "Google Photos", ServiceCategory.GOOGLE_PIXEL, "google-inc/photos", 0xFFFBBF24L))
        add(apkm("google_maps", "Google Maps", ServiceCategory.GOOGLE_PIXEL, "google-inc/maps", 0xFF22C55EL))
        add(apkm("google_phone", "Google Phone (Dialer)", ServiceCategory.GOOGLE_PIXEL, "google-inc/google-phone", 0xFF10B981L))
        add(apkm("google_contacts", "Google Contacts", ServiceCategory.GOOGLE_PIXEL, "google-inc/google-contacts", 0xFF3B82F6L))
        add(apkm("google_clock", "Google Clock", ServiceCategory.GOOGLE_PIXEL, "google-inc/clock", 0xFF60A5FAL))
        add(apkm("google_calculator", "Google Calculator", ServiceCategory.GOOGLE_PIXEL, "google-inc/google-calculator", 0xFF38BDF8L))
        add(apkm("google_android_auto", "Android Auto", ServiceCategory.GOOGLE_PIXEL, "google-inc/android-auto", 0xFF38BDF8L))
        add(apkm("google_home", "Google Home", ServiceCategory.GOOGLE_PIXEL, "google-inc/google-home", 0xFF60A5FAL))
        add(apkm("google_pixel_launcher", "Pixel Launcher", ServiceCategory.GOOGLE_PIXEL, "google-inc/pixel-launcher", 0xFF34D399L))
        add(apkm("google_pixel_buds", "Pixel Buds", ServiceCategory.GOOGLE_PIXEL, "google-inc/google-pixel-buds", 0xFFA78BFAL))
        add(apkm("google_gmail", "Gmail", ServiceCategory.GOOGLE_PIXEL, "google-inc/gmail", 0xFFEF4444L))
        add(apkm("google_calendar", "Google Calendar", ServiceCategory.GOOGLE_PIXEL, "google-inc/calendar", 0xFF3B82F6L))
        add(apkm("google_drive", "Google Drive", ServiceCategory.GOOGLE_PIXEL, "google-inc/drive", 0xFFFBBF24L))
        add(apkm("google_docs", "Google Docs", ServiceCategory.GOOGLE_PIXEL, "google-inc/docs", 0xFF2563EBL))
        add(apkm("google_sheets", "Google Sheets", ServiceCategory.GOOGLE_PIXEL, "google-inc/sheets", 0xFF22C55EL))
        add(apkm("google_slides", "Google Slides", ServiceCategory.GOOGLE_PIXEL, "google-inc/slides", 0xFFF59E0BL))
        add(apkm("google_keep", "Google Keep", ServiceCategory.GOOGLE_PIXEL, "google-inc/keep", 0xFFFBBF24L))
        add(apkm("google_translate", "Google Translate", ServiceCategory.GOOGLE_PIXEL, "google-inc/translate", 0xFF3B82F6L))
        add(apkm("google_wallet", "Google Wallet", ServiceCategory.GOOGLE_PIXEL, "google-inc/google-wallet", 0xFF10B981L))
        add(apkm("google_authenticator", "Google Authenticator", ServiceCategory.GOOGLE_PIXEL, "google-inc/google-authenticator", 0xFF64748BL))
        add(apkm("google_youtube", "YouTube", ServiceCategory.GOOGLE_PIXEL, "google-inc/youtube", 0xFFEF4444L))
        add(apkm("google_yt_music", "YouTube Music", ServiceCategory.GOOGLE_PIXEL, "google-inc/youtube-music", 0xFFDC2626L))
        add(apkm("google_files", "Files by Google", ServiceCategory.GOOGLE_PIXEL, "google-inc/files-go", 0xFF38BDF8L))
        add(apkm("google_find_my", "Google Find My Device", ServiceCategory.GOOGLE_PIXEL, "google-inc/find-my-device", 0xFF22C55EL))

        // =====================================================================
        // 2. MESSAGING & SOCIAL (32)
        // =====================================================================
        add(apkm("msg_whatsapp_beta", "WhatsApp Messenger", ServiceCategory.MESSAGING_SOCIAL, "whatsapp-inc/whatsapp", 0xFF22C55EL))
        add(apkm("msg_whatsapp_business", "WhatsApp Business", ServiceCategory.MESSAGING_SOCIAL, "whatsapp-inc/whatsapp-business", 0xFF10B981L))
        add(apkm("msg_telegram", "Telegram", ServiceCategory.MESSAGING_SOCIAL, "telegram-fz-llc/telegram", 0xFF38BDF8L))
        add(apkm("msg_telegram_x", "Telegram X", ServiceCategory.MESSAGING_SOCIAL, "telegram-fz-llc/telegram-x", 0xFF0EA5E9L))
        add(gh("msg_nekogram", "Nekogram (Telegram Client)", ServiceCategory.MESSAGING_SOCIAL, "Nekogram/Nekogram", 0xFF38BDF8L))
        add(apkm("msg_discord", "Discord", ServiceCategory.MESSAGING_SOCIAL, "discord-inc/discord-chat-for-gamers", 0xFF818CF8L))
        add(gh("msg_signal", "Signal Android", ServiceCategory.MESSAGING_SOCIAL, "signalapp/Signal-Android", 0xFF3B82F6L))
        add(apkm("msg_instagram", "Instagram", ServiceCategory.MESSAGING_SOCIAL, "instagram/instagram-instagram", 0xFFF472B6L))
        add(apkm("msg_threads", "Threads", ServiceCategory.MESSAGING_SOCIAL, "instagram/threads-an-instagram-app", 0xFFE2E8F0L))
        add(apkm("msg_facebook", "Facebook", ServiceCategory.MESSAGING_SOCIAL, "facebook-2/facebook", 0xFF2563EBL))
        add(apkm("msg_messenger", "Facebook Messenger", ServiceCategory.MESSAGING_SOCIAL, "facebook-2/messenger", 0xFF60A5FAL))
        add(apkm("msg_x_twitter", "X (Twitter)", ServiceCategory.MESSAGING_SOCIAL, "x-corp/twitter", 0xFF94A3B8L))
        add(apkm("msg_reddit", "Reddit", ServiceCategory.MESSAGING_SOCIAL, "redditinc/reddit", 0xFFF97316L))
        add(apkm("msg_slack", "Slack", ServiceCategory.MESSAGING_SOCIAL, "slack-technologies-inc/slack", 0xFFA855F7L))
        add(apkm("msg_teams", "Microsoft Teams", ServiceCategory.MESSAGING_SOCIAL, "microsoft-corporation/microsoft-teams", 0xFF6366F1L))
        add(apkm("msg_zoom", "Zoom Workplace", ServiceCategory.MESSAGING_SOCIAL, "zoom-video-communications/zoom", 0xFF2563EBL))
        add(apkm("msg_snapchat", "Snapchat", ServiceCategory.MESSAGING_SOCIAL, "snap-inc/snapchat", 0xFFFBBF24L))
        add(apkm("msg_tiktok", "TikTok", ServiceCategory.MESSAGING_SOCIAL, "tiktok-pte-ltd/tik-tok", 0xFFF43F5EL))
        add(apkm("msg_linkedin", "LinkedIn", ServiceCategory.MESSAGING_SOCIAL, "linkedin/linkedin", 0xFF0284C7L))
        add(apkm("msg_viber", "Viber", ServiceCategory.MESSAGING_SOCIAL, "viber-media-s-a-r-l/viber", 0xFFA855F7L))
        add(apkm("msg_skype", "Microsoft Skype", ServiceCategory.MESSAGING_SOCIAL, "skype/skype", 0xFF0EA5E9L))
        add(gh("msg_element", "Element (Matrix)", ServiceCategory.MESSAGING_SOCIAL, "element-hq/element-android", 0xFF10B981L))
        add(gh("msg_session", "Session Private Messenger", ServiceCategory.MESSAGING_SOCIAL, "oxen-io/session-android", 0xFF34D399L))
        add(gh("msg_simplex", "SimpleX Chat", ServiceCategory.MESSAGING_SOCIAL, "simplex-chat/simplex-chat", 0xFF38BDF8L))
        add(gh("msg_bluesky", "Bluesky Social", ServiceCategory.MESSAGING_SOCIAL, "bluesky-social/social-app", 0xFF0EA5E9L))
        add(gh("msg_mastodon", "Mastodon Android", ServiceCategory.MESSAGING_SOCIAL, "mastodon/mastodon-android", 0xFF6366F1L))
        add(gh("msg_tusky", "Tusky (Mastodon Client)", ServiceCategory.MESSAGING_SOCIAL, "tuskyapp/Tusky", 0xFF3B82F6L))
        add(gh("msg_thunderbird", "Thunderbird Android", ServiceCategory.MESSAGING_SOCIAL, "thunderbird/thunderbird-android", 0xFF0EA5E9L))
        add(gh("msg_proton_mail", "Proton Mail Android", ServiceCategory.MESSAGING_SOCIAL, "ProtonMail/android-mail", 0xFF8B5CF6L))
        add(gh("msg_tuta_mail", "Tuta Mail", ServiceCategory.MESSAGING_SOCIAL, "tutao/tutanota", 0xFFEF4444L))
        add(gh("msg_mattermost", "Mattermost Mobile", ServiceCategory.MESSAGING_SOCIAL, "mattermost/mattermost-mobile", 0xFF2563EBL))
        add(gh("msg_zulip", "Zulip Mobile", ServiceCategory.MESSAGING_SOCIAL, "zulip/zulip-flutter", 0xFF34D399L))

        // =====================================================================
        // 3. BROWSERS (20)
        // =====================================================================
        add(apkm("browser_chrome_beta", "Google Chrome Beta", ServiceCategory.BETA, "google-inc/chrome-beta", 0xFFFBBF24L))
        add(apkm("browser_chrome_canary", "Google Chrome Canary", ServiceCategory.BETA, "google-inc/chrome-canary", 0xFFF59E0BL))
        add(apkm("browser_chrome_stable", "Google Chrome", ServiceCategory.BROWSERS, "google-inc/chrome", 0xFF22C55EL))
        add(apkm("browser_firefox_nightly", "Firefox Nightly", ServiceCategory.BETA, "mozilla/firefox-nightly-for-developers", 0xFFFB923CL))
        add(apkm("browser_firefox_beta", "Firefox Beta", ServiceCategory.BETA, "mozilla/firefox-beta", 0xFFF97316L))
        add(apkm("browser_firefox", "Firefox Stable", ServiceCategory.BROWSERS, "mozilla/firefox", 0xFFF97316L))
        add(apkm("browser_firefox_focus", "Firefox Focus", ServiceCategory.BROWSERS, "mozilla/firefox-focus", 0xFFA855F7L))
        add(gh("browser_brave", "Brave Browser", ServiceCategory.BROWSERS, "brave/brave-browser", 0xFFF97316L))
        add(apkm("browser_vivaldi", "Vivaldi Browser Beta", ServiceCategory.BETA, "vivaldi-technologies/vivaldi-browser-beta", 0xFFEF4444L))
        add(apkm("browser_vivaldi_stable", "Vivaldi Browser", ServiceCategory.BROWSERS, "vivaldi-technologies/vivaldi-browser", 0xFFDC2626L))
        add(apkm("browser_edge_canary", "Microsoft Edge Canary", ServiceCategory.BETA, "microsoft-corporation/microsoft-edge-canary", 0xFF0EA5E9L))
        add(apkm("browser_edge", "Microsoft Edge", ServiceCategory.BROWSERS, "microsoft-corporation/microsoft-edge", 0xFF0284C7L))
        add(apkm("browser_opera", "Opera Browser", ServiceCategory.BROWSERS, "opera-software-asa/opera", 0xFFEF4444L))
        add(apkm("browser_opera_gx", "Opera GX", ServiceCategory.BROWSERS, "opera-software-asa/opera-gx", 0xFFF43F5EL))
        add(apkm("browser_ddg", "DuckDuckGo Browser", ServiceCategory.BROWSERS, "duckduckgo/duckduckgo-privacy-browser", 0xFFFB923CL))
        add(apkm("browser_samsung", "Samsung Internet Browser", ServiceCategory.BROWSERS, "samsung-electronics-co-ltd/samsung-internet-for-android", 0xFF6366F1L))
        add(gh("browser_cromite", "Cromite", ServiceCategory.BROWSERS, "uazo/cromite", 0xFF60A5FAL))
        add(gh("browser_mullvad", "Mullvad Browser", ServiceCategory.BROWSERS, "mullvad/mullvad-browser", 0xFF22C55EL))
        add(gh("browser_tor", "Tor Browser Android", ServiceCategory.BROWSERS, "torproject/tor-browser", 0xFF8B5CF6L))
        add(gh("browser_via", "Via Browser", ServiceCategory.BROWSERS, "tuyafeng/Via", 0xFF38BDF8L))

        // =====================================================================
        // 4. ANDROID POWER-USER & MODDING (45)
        // =====================================================================
        add(gh("power_magisk", "Magisk", ServiceCategory.ANDROID_POWER, "topjohnwu/Magisk", 0xFF14B8A6L, subscribed = true))
        add(gh("power_kernelsu", "KernelSU", ServiceCategory.ANDROID_POWER, "tiann/KernelSU", 0xFF06B6D4L))
        add(gh("power_apatch", "APatch", ServiceCategory.ANDROID_POWER, "bmax121/APatch", 0xFF22D3EEL))
        add(gh("power_lsposed", "LSPosed", ServiceCategory.ANDROID_POWER, "LSPosed/LSPosed", 0xFFA855F7L))
        add(gh("power_zygisk_next", "Zygisk Next", ServiceCategory.ANDROID_POWER, "Dr-TSNG/ZygiskNext", 0xFF8B5CF6L))
        add(gh("power_pif", "PlayIntegrityFix", ServiceCategory.ANDROID_POWER, "chiteroman/PlayIntegrityFix", 0xFF22C55EL))
        add(gh("power_tricky_store", "TrickyStore", ServiceCategory.ANDROID_POWER, "5ec1cff/TrickyStore", 0xFF38BDF8L))
        add(gh("power_shizuku", "Shizuku", ServiceCategory.ANDROID_POWER, "RikkaApps/Shizuku", 0xFF6366F1L))
        add(gh("power_revanced", "ReVanced Manager", ServiceCategory.ANDROID_POWER, "ReVanced/revanced-manager", 0xFF8B5CF6L))
        add(gh("power_revanced_patches", "ReVanced Patches", ServiceCategory.ANDROID_POWER, "ReVanced/revanced-patches", 0xFFA78BFAL))
        add(gh("power_obtainium", "Obtainium", ServiceCategory.ANDROID_POWER, "ImranR98/Obtainium", 0xFFA855F7L, subscribed = true))
        add(gh("power_termux", "Termux", ServiceCategory.ANDROID_POWER, "termux/termux-app", 0xFF64748BL))
        add(gh("power_termux_api", "Termux:API", ServiceCategory.ANDROID_POWER, "termux/termux-api", 0xFF94A3B8L))
        add(gh("power_adaway", "AdAway", ServiceCategory.ANDROID_POWER, "AdAway/AdAway", 0xFFEF4444L))
        add(rss("power_fdroid", "F-Droid Client", ServiceCategory.ANDROID_POWER, "https://gitlab.com/fdroid/fdroidclient/-/tags?format=atom", 0xFF3B82F6L))
        add(gh("power_droidify", "Droid-ify (F-Droid Client)", ServiceCategory.ANDROID_POWER, "Droid-ify/client", 0xFF38BDF8L))
        add(gh("power_neostore", "Neo Store (F-Droid Client)", ServiceCategory.ANDROID_POWER, "NeoApplications/Neo-Store", 0xFF818CF8L))
        add(gh("power_sdmaid_se", "SD Maid SE", ServiceCategory.ANDROID_POWER, "d4rken-org/sdmaid-se", 0xFF34D399L))
        add(gh("power_appmanager", "App Manager", ServiceCategory.ANDROID_POWER, "MuntashirAkon/AppManager", 0xFF60A5FAL))
        add(gh("power_canta", "Canta (Universal Debloater)", ServiceCategory.ANDROID_POWER, "samolego/Canta", 0xFFF472B6L))
        add(gh("power_hail", "Hail (App Freezer)", ServiceCategory.ANDROID_POWER, "aistra0528/Hail", 0xFF38BDF8L))
        add(gh("power_amarok", "Amarok (App Hider)", ServiceCategory.ANDROID_POWER, "deltazefiro/Amarok-Hider", 0xFFA855F7L))
        add(gh("power_lawnchair", "Lawnchair Launcher", ServiceCategory.ANDROID_POWER, "LawnchairLauncher/lawnchair", 0xFF22C55EL))
        add(gh("power_kiss", "KISS Launcher", ServiceCategory.ANDROID_POWER, "Neamar/KISS", 0xFF10B981L))
        add(apkm("power_nova", "Nova Launcher", ServiceCategory.ANDROID_POWER, "teslacoil-software/nova-launcher", 0xFFEF4444L))
        add(apkm("power_niagara", "Niagara Launcher", ServiceCategory.ANDROID_POWER, "peter-huber/niagara-launcher", 0xFF38BDF8L))
        add(apkm("power_tasker", "Tasker", ServiceCategory.ANDROID_POWER, "joaomgcd/tasker", 0xFFF97316L))
        add(apkm("power_macrodroid", "MacroDroid", ServiceCategory.ANDROID_POWER, "arlosoft/macrodroid", 0xFF3B82F6L))
        add(gh("power_seal", "Seal (Video Downloader)", ServiceCategory.ANDROID_POWER, "JunkFood02/Seal", 0xFFA78BFAL))
        add(gh("power_ytdlnis", "YTDLnis", ServiceCategory.ANDROID_POWER, "deniscerri/ytdlnis", 0xFF60A5FAL))
        add(gh("power_breezy_weather", "Breezy Weather", ServiceCategory.ANDROID_POWER, "breezy-weather/breezy-weather", 0xFFFBBF24L))
        add(gh("power_local_send", "LocalSend", ServiceCategory.ANDROID_POWER, "localsend/localsend", 0xFF14B8A6L))
        add(gh("power_kde_connect", "KDE Connect Android", ServiceCategory.ANDROID_POWER, "KDE/kdeconnect-android", 0xFF38BDF8L))
        add(gh("power_scrcpy", "scrcpy", ServiceCategory.ANDROID_POWER, "Genymobile/scrcpy", 0xFF22C55EL))
        add(gh("power_neo_backup", "Neo Backup", ServiceCategory.ANDROID_POWER, "NeoApplications/Neo-Backup", 0xFF8B5CF6L))
        add(gh("power_aves", "Aves Gallery", ServiceCategory.ANDROID_POWER, "deckerst/aves", 0xFF38BDF8L))
        add(gh("power_fossify_gallery", "Fossify Gallery", ServiceCategory.ANDROID_POWER, "FossifyOrg/Gallery", 0xFFF97316L))
        add(gh("power_fossify_phone", "Fossify Phone", ServiceCategory.ANDROID_POWER, "FossifyOrg/Phone", 0xFF22C55EL))
        add(gh("power_fossify_messages", "Fossify Messages", ServiceCategory.ANDROID_POWER, "FossifyOrg/Messages", 0xFF3B82F6L))
        add(gh("power_fossify_contacts", "Fossify Contacts", ServiceCategory.ANDROID_POWER, "FossifyOrg/Contacts", 0xFFFBBF24L))
        add(gh("power_organic_maps", "Organic Maps", ServiceCategory.ANDROID_POWER, "organicmaps/organicmaps", 0xFF22C55EL))
        add(gh("power_osmand", "OsmAnd", ServiceCategory.ANDROID_POWER, "osmandapp/OsmAnd", 0xFFF97316L))
        add(gh("power_antennapod", "AntennaPod", ServiceCategory.ANDROID_POWER, "AntennaPod/AntennaPod", 0xFF38BDF8L))
        add(gh("power_obtainium_forks", "Fossify File Manager", ServiceCategory.ANDROID_POWER, "FossifyOrg/File-Manager", 0xFF60A5FAL))
        add(gh("power_mixplorer", "Amaze File Manager", ServiceCategory.ANDROID_POWER, "TeamAmaze/AmazeFileManager", 0xFF0EA5E9L))

        // =====================================================================
        // 5. AI & LLMs (36)
        // =====================================================================
        add(gh("ai_ollama", "Ollama", ServiceCategory.AI_LLM, "ollama/ollama", 0xFFE2E8F0L))
        add(gh("ai_open_webui", "Open WebUI", ServiceCategory.AI_LLM, "open-webui/open-webui", 0xFF38BDF8L))
        add(gh("ai_llamacpp", "llama.cpp", ServiceCategory.AI_LLM, "ggerganov/llama.cpp", 0xFFF97316L))
        add(gh("ai_comfyui", "ComfyUI", ServiceCategory.AI_LLM, "comfyanonymous/ComfyUI", 0xFF60A5FAL))
        add(gh("ai_sd_webui", "Stable Diffusion WebUI", ServiceCategory.AI_LLM, "AUTOMATIC1111/stable-diffusion-webui", 0xFFA855F7L))
        add(gh("ai_sd_forge", "Stable Diffusion Forge", ServiceCategory.AI_LLM, "lllyasviel/stable-diffusion-webui-forge", 0xFFC084FCL))
        add(gh("ai_jan", "Jan (Local AI)", ServiceCategory.AI_LLM, "janhq/jan", 0xFF34D399L))
        add(gh("ai_anythingllm", "AnythingLLM", ServiceCategory.AI_LLM, "Mintplex-Labs/anything-llm", 0xFF818CF8L))
        add(gh("ai_vllm", "vLLM", ServiceCategory.AI_LLM, "vllm-project/vllm", 0xFFFBBF24L))
        add(gh("ai_localai", "LocalAI", ServiceCategory.AI_LLM, "mudler/LocalAI", 0xFF22C55EL))
        add(gh("ai_langchain", "LangChain", ServiceCategory.AI_LLM, "langchain-ai/langchain", 0xFF10B981L))
        add(gh("ai_llamaindex", "LlamaIndex", ServiceCategory.AI_LLM, "run-llama/llama_index", 0xFFA855F7L))
        add(gh("ai_continue", "Continue (AI Code Assistant)", ServiceCategory.AI_LLM, "continuedev/continue", 0xFF6366F1L))
        add(gh("ai_cline", "Cline (VS Code AI Agent)", ServiceCategory.AI_LLM, "cline/cline", 0xFFA78BFAL))
        add(gh("ai_aider", "Aider (AI Pair Programmer)", ServiceCategory.AI_LLM, "Aider-AI/aider", 0xFF38BDF8L))
        add(gh("ai_openhands", "OpenHands", ServiceCategory.AI_LLM, "All-Hands-AI/OpenHands", 0xFFF97316L))
        add(gh("ai_claude_code", "Claude Code", ServiceCategory.AI_LLM, "anthropics/claude-code", 0xFFD97706L))
        add(gh("ai_gemini_cli", "Gemini CLI", ServiceCategory.AI_LLM, "google-gemini/gemini-cli", 0xFF4285F4L))
        add(gh("ai_codex_cli", "OpenAI Codex CLI", ServiceCategory.AI_LLM, "openai/codex", 0xFF10B981L))
        add(gh("ai_dify", "Dify", ServiceCategory.AI_LLM, "langgenius/dify", 0xFF3B82F6L))
        add(gh("ai_flowise", "Flowise", ServiceCategory.AI_LLM, "FlowiseAI/Flowise", 0xFF60A5FAL))
        add(gh("ai_whispercpp", "whisper.cpp", ServiceCategory.AI_LLM, "ggerganov/whisper.cpp", 0xFF94A3B8L))
        add(gh("ai_faster_whisper", "Faster-Whisper", ServiceCategory.AI_LLM, "SYSTRAN/faster-whisper", 0xFF38BDF8L))
        add(gh("ai_koboldcpp", "KoboldCpp", ServiceCategory.AI_LLM, "LostRuins/koboldcpp", 0xFFF59E0BL))
        add(gh("ai_sillytavern", "SillyTavern", ServiceCategory.AI_LLM, "SillyTavern/SillyTavern", 0xFFF472B6L))
        add(gh("ai_gpt4all", "GPT4All", ServiceCategory.AI_LLM, "nomic-ai/gpt4all", 0xFF22C55EL))
        add(gh("ai_transformers", "HuggingFace Transformers", ServiceCategory.AI_LLM, "huggingface/transformers", 0xFFFBBF24L))
        add(gh("ai_diffusers", "HuggingFace Diffusers", ServiceCategory.AI_LLM, "huggingface/diffusers", 0xFFF59E0BL))
        add(gh("ai_pytorch", "PyTorch", ServiceCategory.AI_LLM, "pytorch/pytorch", 0xFFEF4444L))
        add(gh("ai_onnxruntime", "ONNX Runtime", ServiceCategory.AI_LLM, "microsoft/onnxruntime", 0xFF3B82F6L))
        add(gh("ai_chroma", "Chroma Vector DB", ServiceCategory.AI_LLM, "chroma-core/chroma", 0xFFF97316L))
        add(gh("ai_qdrant", "Qdrant", ServiceCategory.AI_LLM, "qdrant/qdrant", 0xFFF43F5EL))
        add(gh("ai_milvus", "Milvus", ServiceCategory.AI_LLM, "milvus-io/milvus", 0xFF0EA5E9L))
        add(gh("ai_litellm", "LiteLLM", ServiceCategory.AI_LLM, "BerriAI/litellm", 0xFF34D399L))
        add(gh("ai_crewai", "CrewAI", ServiceCategory.AI_LLM, "crewAIInc/crewAI", 0xFFEF4444L))
        add(gh("ai_autogen", "Microsoft AutoGen", ServiceCategory.AI_LLM, "microsoft/autogen", 0xFF60A5FAL))

        // =====================================================================
        // 6. PC, OS & DEV TOOLS (65)
        // =====================================================================
        add(rss("dev_win11_insider", "Windows 11 Insider Preview", ServiceCategory.PC_DEV, "https://blogs.windows.com/windows-insider/feed/", 0xFF0EA5E9L))
        add(gh("dev_powertoys", "Microsoft PowerToys", ServiceCategory.PC_DEV, "microsoft/PowerToys", 0xFF38BDF8L, subscribed = true))
        add(gh("dev_vscode", "Visual Studio Code", ServiceCategory.PC_DEV, "microsoft/vscode", 0xFF2563EBL))
        add(gh("dev_vscodium", "VSCodium", ServiceCategory.PC_DEV, "VSCodium/vscodium", 0xFF3B82F6L))
        add(gh("dev_neovim", "Neovim", ServiceCategory.PC_DEV, "neovim/neovim", 0xFF22C55EL))
        add(gh("dev_helix", "Helix Editor", ServiceCategory.PC_DEV, "helix-editor/helix", 0xFFA855F7L))
        add(gh("dev_zed", "Zed Editor", ServiceCategory.PC_DEV, "zed-industries/zed", 0xFF818CF8L))
        add(rss("dev_docker", "Docker Desktop", ServiceCategory.PC_DEV, "https://docs.docker.com/desktop/release-notes/index.xml", 0xFF0284C7L))
        add(gh("dev_docker_compose", "Docker Compose", ServiceCategory.PC_DEV, "docker/compose", 0xFF38BDF8L))
        add(gh("dev_podman", "Podman", ServiceCategory.PC_DEV, "containers/podman", 0xFFA855F7L))
        add(gh("dev_kubernetes", "Kubernetes", ServiceCategory.PC_DEV, "kubernetes/kubernetes", 0xFF3B82F6L))
        add(gh("dev_helm", "Helm", ServiceCategory.PC_DEV, "helm/helm", 0xFF0EA5E9L))
        add(gh("dev_terraform", "Terraform", ServiceCategory.PC_DEV, "hashicorp/terraform", 0xFF8B5CF6L))
        add(gh("dev_opentofu", "OpenTofu", ServiceCategory.PC_DEV, "opentofu/opentofu", 0xFFFBBF24L))
        add(gh("dev_ansible", "Ansible", ServiceCategory.PC_DEV, "ansible/ansible", 0xFFEF4444L))
        add(gh("dev_nodejs", "Node.js", ServiceCategory.PC_DEV, "nodejs/node", 0xFF22C55EL))
        add(gh("dev_deno", "Deno", ServiceCategory.PC_DEV, "denoland/deno", 0xFFE2E8F0L))
        add(gh("dev_bun", "Bun", ServiceCategory.PC_DEV, "oven-sh/bun", 0xFFF472B6L))
        add(gh("dev_python", "CPython", ServiceCategory.PC_DEV, "python/cpython", 0xFF3B82F6L))
        add(gh("dev_uv", "uv (Python Package Manager)", ServiceCategory.PC_DEV, "astral-sh/uv", 0xFFC084FCL))
        add(gh("dev_ruff", "Ruff", ServiceCategory.PC_DEV, "astral-sh/ruff", 0xFFA855F7L))
        add(gh("dev_rust", "Rust", ServiceCategory.PC_DEV, "rust-lang/rust", 0xFFF97316L))
        add(gh("dev_golang", "Go (Golang)", ServiceCategory.PC_DEV, "golang/go", 0xFF06B6D4L))
        add(gh("dev_kotlin", "Kotlin", ServiceCategory.PC_DEV, "JetBrains/kotlin", 0xFF8B5CF6L))
        add(gh("dev_gradle", "Gradle", ServiceCategory.PC_DEV, "gradle/gradle", 0xFF14B8A6L))
        add(gh("dev_cmake", "CMake", ServiceCategory.PC_DEV, "Kitware/CMake", 0xFFEF4444L))
        add(gh("dev_git_windows", "Git for Windows", ServiceCategory.PC_DEV, "git-for-windows/git", 0xFFEF4444L))
        add(gh("dev_gh_cli", "GitHub CLI (gh)", ServiceCategory.PC_DEV, "cli/cli", 0xFFE2E8F0L))
        add(gh("dev_lazygit", "lazygit", ServiceCategory.PC_DEV, "jesseduffield/lazygit", 0xFF22C55EL))
        add(gh("dev_winget", "WinGet CLI", ServiceCategory.PC_DEV, "microsoft/winget-cli", 0xFF60A5FAL))
        add(gh("dev_scoop", "Scoop", ServiceCategory.PC_DEV, "ScoopInstaller/Scoop", 0xFF34D399L))
        add(gh("dev_homebrew", "Homebrew", ServiceCategory.PC_DEV, "Homebrew/brew", 0xFFF59E0BL))
        add(gh("dev_windows_terminal", "Windows Terminal", ServiceCategory.PC_DEV, "microsoft/terminal", 0xFF38BDF8L))
        add(gh("dev_wezterm", "WezTerm", ServiceCategory.PC_DEV, "wezterm/wezterm", 0xFF818CF8L))
        add(gh("dev_alacritty", "Alacritty", ServiceCategory.PC_DEV, "alacritty/alacritty", 0xFFF97316L))
        add(gh("dev_kitty", "Kitty Terminal", ServiceCategory.PC_DEV, "kovidgoyal/kitty", 0xFFA855F7L))
        add(gh("dev_pwsh", "PowerShell 7", ServiceCategory.PC_DEV, "PowerShell/PowerShell", 0xFF2563EBL))
        add(gh("dev_ohmyposh", "Oh My Posh", ServiceCategory.PC_DEV, "JanDeDobbeleer/oh-my-posh", 0xFF38BDF8L))
        add(gh("dev_starship", "Starship Prompt", ServiceCategory.PC_DEV, "starship/starship", 0xFFF472B6L))
        add(gh("dev_rufus", "Rufus", ServiceCategory.PC_DEV, "pbatard/rufus", 0xFF3B82F6L))
        add(gh("dev_ventoy", "Ventoy", ServiceCategory.PC_DEV, "ventoy/Ventoy", 0xFF10B981L))
        add(gh("dev_7zip", "7-Zip", ServiceCategory.PC_DEV, "ip7z/7zip", 0xFF64748BL))
        add(gh("dev_peazip", "PeaZip", ServiceCategory.PC_DEV, "peazip/PeaZip", 0xFF22C55EL))
        add(gh("dev_sharex", "ShareX", ServiceCategory.PC_DEV, "ShareX/ShareX", 0xFF60A5FAL))
        add(gh("dev_greenshot", "Greenshot", ServiceCategory.PC_DEV, "greenshot/greenshot", 0xFF22C55EL))
        add(gh("dev_flameshot", "Flameshot", ServiceCategory.PC_DEV, "flameshot-org/flameshot", 0xFFA855F7L))
        add(gh("dev_fancontrol", "FanControl", ServiceCategory.PC_DEV, "Rem0o/FanControl.Releases", 0xFF34D399L))
        add(gh("dev_lhm", "LibreHardwareMonitor", ServiceCategory.PC_DEV, "LibreHardwareMonitor/LibreHardwareMonitor", 0xFF0EA5E9L))
        add(gh("dev_bruno", "Bruno (API Client)", ServiceCategory.PC_DEV, "usebruno/bruno", 0xFFFBBF24L))
        add(gh("dev_insomnia", "Insomnia", ServiceCategory.PC_DEV, "Kong/insomnia", 0xFF8B5CF6L))
        add(gh("dev_dbeaver", "DBeaver", ServiceCategory.PC_DEV, "dbeaver/dbeaver", 0xFF64748BL))
        add(gh("dev_beekeeper", "Beekeeper Studio", ServiceCategory.PC_DEV, "beekeeper-studio/beekeeper-studio", 0xFFF59E0BL))
        add(gh("dev_obsidian", "Obsidian", ServiceCategory.PC_DEV, "obsidianmd/obsidian-releases", 0xFFA855F7L))
        add(gh("dev_logseq", "Logseq", ServiceCategory.PC_DEV, "logseq/logseq", 0xFF38BDF8L))
        add(gh("dev_joplin", "Joplin", ServiceCategory.PC_DEV, "laurent22/joplin", 0xFF2563EBL))
        add(gh("dev_notepadpp", "Notepad++", ServiceCategory.PC_DEV, "notepad-plus-plus/notepad-plus-plus", 0xFF22C55EL))
        add(gh("dev_fzf", "fzf", ServiceCategory.PC_DEV, "junegunn/fzf", 0xFFF43F5EL))
        add(gh("dev_ripgrep", "ripgrep", ServiceCategory.PC_DEV, "BurntSushi/ripgrep", 0xFFF97316L))
        add(gh("dev_bat", "bat", ServiceCategory.PC_DEV, "sharkdp/bat", 0xFF818CF8L))
        add(gh("dev_eza", "eza", ServiceCategory.PC_DEV, "eza-community/eza", 0xFF34D399L))
        add(gh("dev_fastfetch", "fastfetch", ServiceCategory.PC_DEV, "fastfetch-cli/fastfetch", 0xFF38BDF8L))
        add(gh("dev_btop", "btop", ServiceCategory.PC_DEV, "aristocratos/btop", 0xFFEF4444L))
        add(gh("dev_distrobox", "Distrobox", ServiceCategory.PC_DEV, "89luca89/distrobox", 0xFF60A5FAL))
        add(gh("dev_mise", "mise", ServiceCategory.PC_DEV, "jdx/mise", 0xFFA78BFAL))
        add(gh("dev_windhawk", "Windhawk", ServiceCategory.PC_DEV, "ramensoftware/windhawk", 0xFF38BDF8L))

        // =====================================================================
        // 7. SMART HOME & MEDIA (42)
        // =====================================================================
        add(gh("media_home_assistant", "Home Assistant Core", ServiceCategory.SMART_HOME_MEDIA, "home-assistant/core", 0xFF38BDF8L))
        add(gh("media_ha_android", "Home Assistant Android", ServiceCategory.SMART_HOME_MEDIA, "home-assistant/android", 0xFF0EA5E9L))
        add(gh("media_ha_os", "Home Assistant OS", ServiceCategory.SMART_HOME_MEDIA, "home-assistant/operating-system", 0xFF0284C7L))
        add(gh("media_zigbee2mqtt", "Zigbee2MQTT", ServiceCategory.SMART_HOME_MEDIA, "Koenkk/zigbee2mqtt", 0xFFFBBF24L))
        add(gh("media_zwave_js", "Z-Wave JS UI", ServiceCategory.SMART_HOME_MEDIA, "zwave-js/zwave-js-ui", 0xFF3B82F6L))
        add(gh("media_esphome", "ESPHome", ServiceCategory.SMART_HOME_MEDIA, "esphome/esphome", 0xFF94A3B8L))
        add(gh("media_frigate", "Frigate NVR", ServiceCategory.SMART_HOME_MEDIA, "blakeblackshear/frigate", 0xFF3B82F6L))
        add(gh("media_scrypted", "Scrypted", ServiceCategory.SMART_HOME_MEDIA, "koush/scrypted", 0xFF8B5CF6L))
        add(gh("media_nodered", "Node-RED", ServiceCategory.SMART_HOME_MEDIA, "node-red/node-red", 0xFFEF4444L))
        add(gh("media_mosquitto", "Eclipse Mosquitto", ServiceCategory.SMART_HOME_MEDIA, "eclipse-mosquitto/mosquitto", 0xFF38BDF8L))
        add(gh("media_tasmota", "Tasmota", ServiceCategory.SMART_HOME_MEDIA, "arendst/Tasmota", 0xFF22C55EL))
        add(gh("media_wled", "WLED", ServiceCategory.SMART_HOME_MEDIA, "Aircoookie/WLED", 0xFFA855F7L))
        add(gh("media_stremio", "Stremio", ServiceCategory.SMART_HOME_MEDIA, "Stremio/stremio-shell", 0xFF8B5CF6L))
        add(apkm("media_vlc", "VLC for Android", ServiceCategory.SMART_HOME_MEDIA, "videolabs/vlc", 0xFFF97316L))
        add(gh("media_kodi", "Kodi", ServiceCategory.SMART_HOME_MEDIA, "xbmc/xbmc", 0xFF0EA5E9L))
        add(gh("media_jellyfin", "Jellyfin Server", ServiceCategory.SMART_HOME_MEDIA, "jellyfin/jellyfin", 0xFFA855F7L))
        add(gh("media_jellyfin_android", "Jellyfin Android", ServiceCategory.SMART_HOME_MEDIA, "jellyfin/jellyfin-android", 0xFFC084FCL))
        add(gh("media_jellyfin_tv", "Jellyfin Android TV", ServiceCategory.SMART_HOME_MEDIA, "jellyfin/jellyfin-androidtv", 0xFF8B5CF6L))
        add(rss("media_plex", "Plex Media Server", ServiceCategory.SMART_HOME_MEDIA, "https://forums.plex.tv/c/announcements/5.rss", 0xFFF59E0BL))
        add(gh("media_newpipe", "NewPipe", ServiceCategory.SMART_HOME_MEDIA, "TeamNewPipe/NewPipe", 0xFFEF4444L))
        add(gh("media_tubular", "Tubular", ServiceCategory.SMART_HOME_MEDIA, "polymorphicshade/Tubular", 0xFFF43F5EL))
        add(gh("media_grayjay", "Grayjay", ServiceCategory.SMART_HOME_MEDIA, "futo-org/grayjay-android", 0xFF60A5FAL))
        add(gh("media_spotube", "Spotube", ServiceCategory.SMART_HOME_MEDIA, "KRTirtho/spotube", 0xFF22C55EL))
        add(gh("media_mpv_android", "mpv-android", ServiceCategory.SMART_HOME_MEDIA, "mpv-android/mpv-android", 0xFFA78BFAL))
        add(gh("media_mpv", "mpv Player", ServiceCategory.SMART_HOME_MEDIA, "mpv-player/mpv", 0xFF8B5CF6L))
        add(gh("media_audiobookshelf", "Audiobookshelf", ServiceCategory.SMART_HOME_MEDIA, "advplyr/audiobookshelf", 0xFFFBBF24L))
        add(gh("media_calibre", "Calibre", ServiceCategory.SMART_HOME_MEDIA, "kovidgoyal/calibre", 0xFF34D399L))
        add(gh("media_kavita", "Kavita", ServiceCategory.SMART_HOME_MEDIA, "Kareadita/Kavita", 0xFF10B981L))
        add(gh("media_komga", "Komga", ServiceCategory.SMART_HOME_MEDIA, "gotson/komga", 0xFF38BDF8L))
        add(gh("media_sonarr", "Sonarr", ServiceCategory.SMART_HOME_MEDIA, "Sonarr/Sonarr", 0xFF0EA5E9L))
        add(gh("media_radarr", "Radarr", ServiceCategory.SMART_HOME_MEDIA, "Radarr/Radarr", 0xFFFBBF24L))
        add(gh("media_prowlarr", "Prowlarr", ServiceCategory.SMART_HOME_MEDIA, "Prowlarr/Prowlarr", 0xFFF97316L))
        add(gh("media_bazarr", "Bazarr", ServiceCategory.SMART_HOME_MEDIA, "morpheus65535/bazarr", 0xFFA855F7L))
        add(gh("media_qbittorrent", "qBittorrent", ServiceCategory.SMART_HOME_MEDIA, "qbittorrent/qBittorrent", 0xFF3B82F6L))
        add(gh("media_ytdlp", "yt-dlp", ServiceCategory.SMART_HOME_MEDIA, "yt-dlp/yt-dlp", 0xFFEF4444L))
        add(gh("media_handbrake", "HandBrake", ServiceCategory.SMART_HOME_MEDIA, "HandBrake/HandBrake", 0xFF60A5FAL))
        add(gh("media_obs", "OBS Studio", ServiceCategory.SMART_HOME_MEDIA, "obsproject/obs-studio", 0xFF94A3B8L))
        add(gh("media_audacity", "Audacity", ServiceCategory.SMART_HOME_MEDIA, "audacity/audacity", 0xFF3B82F6L))
        add(gh("media_krita", "Krita", ServiceCategory.SMART_HOME_MEDIA, "KDE/krita", 0xFFF472B6L))
        add(gh("media_blender", "Blender", ServiceCategory.SMART_HOME_MEDIA, "blender/blender", 0xFFF97316L))
        add(gh("media_immich", "Immich", ServiceCategory.SMART_HOME_MEDIA, "immich-app/immich", 0xFF6366F1L))
        add(gh("media_photoprism", "PhotoPrism", ServiceCategory.SMART_HOME_MEDIA, "photoprism/photoprism", 0xFF38BDF8L))

        // =====================================================================
        // 8. SECURITY, PRIVACY & NETWORKING (40)
        // =====================================================================
        add(gh("sec_tailscale", "Tailscale", ServiceCategory.SECURITY_PRIVACY, "tailscale/tailscale", 0xFF60A5FAL))
        add(apkm("sec_wireguard", "WireGuard", ServiceCategory.SECURITY_PRIVACY, "wireguard-development-team/wireguard", 0xFFDC2626L))
        add(gh("sec_openvpn", "OpenVPN for Android", ServiceCategory.SECURITY_PRIVACY, "schwabe/ics-openvpn", 0xFFF97316L))
        add(gh("sec_proton_vpn", "Proton VPN Android", ServiceCategory.SECURITY_PRIVACY, "ProtonVPN/android-app", 0xFF8B5CF6L))
        add(gh("sec_mullvad_vpn", "Mullvad VPN", ServiceCategory.SECURITY_PRIVACY, "mullvad/mullvadvpn-app", 0xFF22C55EL))
        add(gh("sec_bitwarden", "Bitwarden Android", ServiceCategory.SECURITY_PRIVACY, "bitwarden/android", 0xFF2563EBL))
        add(gh("sec_vaultwarden", "Vaultwarden", ServiceCategory.SECURITY_PRIVACY, "dani-garcia/vaultwarden", 0xFF3B82F6L))
        add(gh("sec_keepassdx", "KeePassDX", ServiceCategory.SECURITY_PRIVACY, "Kunzisoft/KeePassDX", 0xFF10B981L))
        add(gh("sec_keepassxc", "KeePassXC", ServiceCategory.SECURITY_PRIVACY, "keepassxreboot/keepassxc", 0xFF22C55EL))
        add(gh("sec_aegis", "Aegis Authenticator", ServiceCategory.SECURITY_PRIVACY, "beemdevelopment/Aegis", 0xFF06B6D4L))
        add(gh("sec_ente_auth", "Ente Auth", ServiceCategory.SECURITY_PRIVACY, "ente-io/ente", 0xFF14B8A6L))
        add(gh("sec_2fas", "2FAS Auth", ServiceCategory.SECURITY_PRIVACY, "twofas/2fas-android", 0xFFEF4444L))
        add(gh("sec_adguard_home", "AdGuard Home", ServiceCategory.SECURITY_PRIVACY, "AdguardTeam/AdGuardHome", 0xFF22C55EL))
        add(gh("sec_adguard_android", "AdGuard for Android", ServiceCategory.SECURITY_PRIVACY, "AdguardTeam/AdguardForAndroid", 0xFF10B981L))
        add(gh("sec_pihole", "Pi-hole", ServiceCategory.SECURITY_PRIVACY, "pi-hole/pi-hole", 0xFFF43F5EL))
        add(gh("sec_blocky", "Blocky DNS", ServiceCategory.SECURITY_PRIVACY, "0xERR0R/blocky", 0xFF38BDF8L))
        add(gh("sec_rethinkdns", "RethinkDNS", ServiceCategory.SECURITY_PRIVACY, "celzero/rethink-app", 0xFF818CF8L))
        add(gh("sec_netguard", "NetGuard", ServiceCategory.SECURITY_PRIVACY, "M66B/NetGuard", 0xFF38BDF8L))
        add(gh("sec_blokada", "Blokada", ServiceCategory.SECURITY_PRIVACY, "blokadaorg/blokada", 0xFFF97316L))
        add(gh("sec_openwrt", "OpenWrt", ServiceCategory.SECURITY_PRIVACY, "openwrt/openwrt", 0xFF0EA5E9L))
        add(gh("sec_opnsense", "OPNsense", ServiceCategory.SECURITY_PRIVACY, "opnsense/core", 0xFFF97316L))
        add(gh("sec_caddy", "Caddy Server", ServiceCategory.SECURITY_PRIVACY, "caddyserver/caddy", 0xFF22C55EL))
        add(gh("sec_npm", "Nginx Proxy Manager", ServiceCategory.SECURITY_PRIVACY, "NginxProxyManager/nginx-proxy-manager", 0xFFF472B6L))
        add(gh("sec_traefik", "Traefik", ServiceCategory.SECURITY_PRIVACY, "traefik/traefik", 0xFF06B6D4L))
        add(gh("sec_cloudflared", "Cloudflare Tunnel (cloudflared)", ServiceCategory.SECURITY_PRIVACY, "cloudflare/cloudflared", 0xFFF97316L))
        add(gh("sec_crowdsec", "CrowdSec", ServiceCategory.SECURITY_PRIVACY, "crowdsecurity/crowdsec", 0xFF8B5CF6L))
        add(gh("sec_fail2ban", "Fail2ban", ServiceCategory.SECURITY_PRIVACY, "fail2ban/fail2ban", 0xFFEF4444L))
        add(gh("sec_wazuh", "Wazuh", ServiceCategory.SECURITY_PRIVACY, "wazuh/wazuh", 0xFF0EA5E9L))
        add(gh("sec_trivy", "Trivy", ServiceCategory.SECURITY_PRIVACY, "aquasecurity/trivy", 0xFF38BDF8L))
        add(gh("sec_nmap", "Nmap", ServiceCategory.SECURITY_PRIVACY, "nmap/nmap", 0xFF60A5FAL))
        add(gh("sec_mitmproxy", "mitmproxy", ServiceCategory.SECURITY_PRIVACY, "mitmproxy/mitmproxy", 0xFFF97316L))
        add(gh("sec_zerotier", "ZeroTier", ServiceCategory.SECURITY_PRIVACY, "zerotier/ZeroTierOne", 0xFFFBBF24L))
        add(gh("sec_nebula", "Nebula Mesh VPN", ServiceCategory.SECURITY_PRIVACY, "slackhq/nebula", 0xFF818CF8L))
        add(gh("sec_headscale", "Headscale", ServiceCategory.SECURITY_PRIVACY, "juanfont/headscale", 0xFF34D399L))
        add(gh("sec_authentik", "Authentik", ServiceCategory.SECURITY_PRIVACY, "goauthentik/authentik", 0xFFF97316L))
        add(gh("sec_authelia", "Authelia", ServiceCategory.SECURITY_PRIVACY, "authelia/authelia", 0xFF3B82F6L))
        add(gh("sec_pangolin", "Pangolin", ServiceCategory.SECURITY_PRIVACY, "fosrl/pangolin", 0xFFA855F7L))
        add(gh("sec_rustdesk", "RustDesk", ServiceCategory.SECURITY_PRIVACY, "rustdesk/rustdesk", 0xFF0EA5E9L))
        add(gh("sec_meshcentral", "MeshCentral", ServiceCategory.SECURITY_PRIVACY, "Ylianst/MeshCentral", 0xFF22C55EL))
        add(gh("sec_veracrypt", "VeraCrypt", ServiceCategory.SECURITY_PRIVACY, "veracrypt/VeraCrypt", 0xFF3B82F6L))

        // =====================================================================
        // 9. SELF-HOSTED & CLOUD (35)
        // =====================================================================
        add(gh("cloud_nextcloud", "Nextcloud Server", ServiceCategory.SELF_HOSTED, "nextcloud/server", 0xFF0EA5E9L))
        add(gh("cloud_nextcloud_android", "Nextcloud Android", ServiceCategory.SELF_HOSTED, "nextcloud/android", 0xFF0284C7L))
        add(gh("cloud_seafile", "Seafile", ServiceCategory.SELF_HOSTED, "haiwen/seafile", 0xFFF97316L))
        add(gh("cloud_paperless", "Paperless-ngx", ServiceCategory.SELF_HOSTED, "paperless-ngx/paperless-ngx", 0xFF22C55EL))
        add(gh("cloud_syncthing", "Syncthing", ServiceCategory.SELF_HOSTED, "syncthing/syncthing", 0xFF38BDF8L))
        add(gh("cloud_syncthing_fork", "Syncthing-Fork", ServiceCategory.SELF_HOSTED, "Catfriend1/syncthing-android", 0xFF0EA5E9L))
        add(gh("cloud_rclone", "Rclone", ServiceCategory.SELF_HOSTED, "rclone/rclone", 0xFF60A5FAL))
        add(gh("cloud_restic", "Restic", ServiceCategory.SELF_HOSTED, "restic/restic", 0xFF34D399L))
        add(gh("cloud_borg", "BorgBackup", ServiceCategory.SELF_HOSTED, "borgbackup/borg", 0xFF10B981L))
        add(gh("cloud_kopia", "Kopia", ServiceCategory.SELF_HOSTED, "kopia/kopia", 0xFF38BDF8L))
        add(gh("cloud_portainer", "Portainer", ServiceCategory.SELF_HOSTED, "portainer/portainer", 0xFF0284C7L))
        add(gh("cloud_dockge", "Dockge", ServiceCategory.SELF_HOSTED, "louislam/dockge", 0xFF34D399L))
        add(gh("cloud_watchtower", "Watchtower", ServiceCategory.SELF_HOSTED, "containrrr/watchtower", 0xFF60A5FAL))
        add(gh("cloud_diun", "Diun", ServiceCategory.SELF_HOSTED, "crazy-max/diun", 0xFF818CF8L))
        add(gh("cloud_uptime_kuma", "Uptime Kuma", ServiceCategory.SELF_HOSTED, "louislam/uptime-kuma", 0xFF10B981L))
        add(gh("cloud_beszel", "Beszel (Server Monitor)", ServiceCategory.SELF_HOSTED, "henrygd/beszel", 0xFF38BDF8L))
        add(gh("cloud_netdata", "Netdata", ServiceCategory.SELF_HOSTED, "netdata/netdata", 0xFF22C55EL))
        add(gh("cloud_grafana", "Grafana", ServiceCategory.SELF_HOSTED, "grafana/grafana", 0xFFF97316L))
        add(gh("cloud_prometheus", "Prometheus", ServiceCategory.SELF_HOSTED, "prometheus/prometheus", 0xFFEF4444L))
        add(gh("cloud_loki", "Grafana Loki", ServiceCategory.SELF_HOSTED, "grafana/loki", 0xFFF59E0BL))
        add(gh("cloud_gitea", "Gitea", ServiceCategory.SELF_HOSTED, "go-gitea/gitea", 0xFF22C55EL))
        add(gh("cloud_woodpecker", "Woodpecker CI", ServiceCategory.SELF_HOSTED, "woodpecker-ci/woodpecker", 0xFF34D399L))
        add(gh("cloud_mealie", "Mealie", ServiceCategory.SELF_HOSTED, "mealie-recipes/mealie", 0xFFF59E0BL))
        add(gh("cloud_tandoor", "Tandoor Recipes", ServiceCategory.SELF_HOSTED, "TandoorRecipes/recipes", 0xFFF97316L))
        add(gh("cloud_freshrss", "FreshRSS", ServiceCategory.SELF_HOSTED, "FreshRSS/FreshRSS", 0xFF3B82F6L))
        add(gh("cloud_miniflux", "Miniflux", ServiceCategory.SELF_HOSTED, "miniflux/v2", 0xFF64748BL))
        add(gh("cloud_wallabag", "Wallabag", ServiceCategory.SELF_HOSTED, "wallabag/wallabag", 0xFF0EA5E9L))
        add(gh("cloud_navidrome", "Navidrome", ServiceCategory.SELF_HOSTED, "navidrome/navidrome", 0xFF60A5FAL))
        add(gh("cloud_n8n", "n8n Workflow Automation", ServiceCategory.SELF_HOSTED, "n8n-io/n8n", 0xFFF43F5EL))
        add(gh("cloud_activepieces", "Activepieces", ServiceCategory.SELF_HOSTED, "activepieces/activepieces", 0xFFA855F7L))
        add(gh("cloud_homarr", "Homarr Dashboard", ServiceCategory.SELF_HOSTED, "homarr-labs/homarr", 0xFFA855F7L))
        add(gh("cloud_homepage", "Homepage Dashboard", ServiceCategory.SELF_HOSTED, "gethomepage/homepage", 0xFF38BDF8L))
        add(gh("cloud_stirling_pdf", "Stirling-PDF", ServiceCategory.SELF_HOSTED, "Stirling-Tools/Stirling-PDF", 0xFFEF4444L))
        add(gh("cloud_it_tools", "IT-Tools", ServiceCategory.SELF_HOSTED, "CorentinTh/it-tools", 0xFF22C55EL))
        add(gh("cloud_excalidraw", "Excalidraw", ServiceCategory.SELF_HOSTED, "excalidraw/excalidraw", 0xFF818CF8L))

        // =====================================================================
        // 10. GAMING & EMULATION (30)
        // =====================================================================
        add(gh("game_retroarch", "RetroArch", ServiceCategory.GAMING_EMULATION, "libretro/RetroArch", 0xFF94A3B8L))
        add(gh("game_dolphin", "Dolphin Emulator", ServiceCategory.GAMING_EMULATION, "dolphin-emu/dolphin", 0xFF38BDF8L))
        add(gh("game_ppsspp", "PPSSPP", ServiceCategory.GAMING_EMULATION, "hrydgard/ppsspp", 0xFF60A5FAL))
        add(gh("game_duckstation", "DuckStation", ServiceCategory.GAMING_EMULATION, "stenzek/duckstation", 0xFFFBBF24L))
        add(gh("game_pcsx2", "PCSX2", ServiceCategory.GAMING_EMULATION, "PCSX2/pcsx2", 0xFF3B82F6L))
        add(gh("game_rpcs3", "RPCS3", ServiceCategory.GAMING_EMULATION, "RPCS3/rpcs3", 0xFF818CF8L))
        add(gh("game_xemu", "xemu (Xbox Emulator)", ServiceCategory.GAMING_EMULATION, "xemu-project/xemu", 0xFF22C55EL))
        add(gh("game_vita3k", "Vita3K", ServiceCategory.GAMING_EMULATION, "Vita3K/Vita3K", 0xFF3B82F6L))
        add(gh("game_flycast", "Flycast", ServiceCategory.GAMING_EMULATION, "flyinghead/flycast", 0xFFF97316L))
        add(gh("game_melonds", "melonDS", ServiceCategory.GAMING_EMULATION, "melonDS-emu/melonDS", 0xFF34D399L))
        add(gh("game_mgba", "mGBA", ServiceCategory.GAMING_EMULATION, "mgba-emu/mgba", 0xFFA855F7L))
        add(gh("game_scummvm", "ScummVM", ServiceCategory.GAMING_EMULATION, "scummvm/scummvm", 0xFF22C55EL))
        add(gh("game_moonlight", "Moonlight Android", ServiceCategory.GAMING_EMULATION, "moonlight-stream/moonlight-android", 0xFF22C55EL))
        add(gh("game_moonlight_pc", "Moonlight PC", ServiceCategory.GAMING_EMULATION, "moonlight-stream/moonlight-qt", 0xFF10B981L))
        add(gh("game_sunshine", "Sunshine (Game Stream Host)", ServiceCategory.GAMING_EMULATION, "LizardByte/Sunshine", 0xFFF97316L))
        add(gh("game_apollo", "Apollo (Sunshine Fork)", ServiceCategory.GAMING_EMULATION, "ClassicOldSong/Apollo", 0xFFFBBF24L))
        add(gh("game_playnite", "Playnite", ServiceCategory.GAMING_EMULATION, "JosefNemec/Playnite", 0xFF6366F1L))
        add(gh("game_heroic", "Heroic Games Launcher", ServiceCategory.GAMING_EMULATION, "Heroic-Games-Launcher/HeroicGamesLauncher", 0xFF06B6D4L))
        add(gh("game_lutris", "Lutris", ServiceCategory.GAMING_EMULATION, "lutris/lutris", 0xFFF59E0BL))
        add(gh("game_bottles", "Bottles", ServiceCategory.GAMING_EMULATION, "bottlesdevs/Bottles", 0xFF3B82F6L))
        add(gh("game_proton_ge", "Proton-GE", ServiceCategory.GAMING_EMULATION, "GloriousEggroll/proton-ge-custom", 0xFFEF4444L))
        add(gh("game_mangohud", "MangoHud", ServiceCategory.GAMING_EMULATION, "flightlessmango/MangoHud", 0xFFF97316L))
        add(gh("game_prism", "Prism Launcher", ServiceCategory.GAMING_EMULATION, "PrismLauncher/PrismLauncher", 0xFF34D399L))
        add(gh("game_modrinth", "Modrinth App", ServiceCategory.GAMING_EMULATION, "modrinth/code", 0xFF22C55EL))
        add(gh("game_bazzite", "Bazzite OS", ServiceCategory.GAMING_EMULATION, "ublue-os/bazzite", 0xFF8B5CF6L))
        add(gh("game_es_de", "ES-DE (EmulationStation)", ServiceCategory.GAMING_EMULATION, "Excordis/ES-DE", 0xFF60A5FAL))
        add(gh("game_daijisho", "Daijisho Launcher", ServiceCategory.GAMING_EMULATION, "TapiocaFox/Daijishou", 0xFF38BDF8L))
        add(gh("game_winlator", "Winlator", ServiceCategory.GAMING_EMULATION, "brunodev85/winlator", 0xFF0EA5E9L))
        add(gh("game_chiaki", "Chiaki-ng (PS Remote Play)", ServiceCategory.GAMING_EMULATION, "streetpea/chiaki-ng", 0xFF2563EBL))
        add(gh("game_better_xcloud", "Better xCloud", ServiceCategory.GAMING_EMULATION, "redphx/better-xcloud", 0xFF22C55EL))

        // =====================================================================
        // 11. ISRAEL REGIONAL SERVICES (35)
        // =====================================================================
        add(apkm("il_bit", "bit (בנק הפועלים)", ServiceCategory.MESSAGING_SOCIAL, "bank-hapoalim/bit", 0xFF38BDF8L, region = ServiceRegion.ISRAEL))
        add(apkm("il_paybox", "PayBox (פייבוקס)", ServiceCategory.MESSAGING_SOCIAL, "paybox/paybox", 0xFF0EA5E9L, region = ServiceRegion.ISRAEL))
        add(apkm("il_poalim", "בנק הפועלים (Poalim)", ServiceCategory.SECURITY_PRIVACY, "bank-hapoalim/bank-hapoalim", 0xFFEF4444L, region = ServiceRegion.ISRAEL))
        add(apkm("il_leumi", "בנק לאומי (Leumi)", ServiceCategory.SECURITY_PRIVACY, "bank-leumi/leumi", 0xFF3B82F6L, region = ServiceRegion.ISRAEL))
        add(apkm("il_discount", "בנק דיסקונט (Discount)", ServiceCategory.SECURITY_PRIVACY, "discount-bank/discount", 0xFF22C55EL, region = ServiceRegion.ISRAEL))
        add(apkm("il_mizrahi", "מזרחי טפחות (Mizrahi)", ServiceCategory.SECURITY_PRIVACY, "mizrahi-tefahot/mizrahi-tefahot", 0xFFF97316L, region = ServiceRegion.ISRAEL))
        add(apkm("il_max", "MAX (מקס)", ServiceCategory.SECURITY_PRIVACY, "max-it-finance/max", 0xFF6366F1L, region = ServiceRegion.ISRAEL))
        add(apkm("il_isracard", "ישראכרט (Isracard)", ServiceCategory.SECURITY_PRIVACY, "isracard/isracard", 0xFF2563EBL, region = ServiceRegion.ISRAEL))
        add(apkm("il_cal", "כאל (Cal)", ServiceCategory.SECURITY_PRIVACY, "cal-israel-credit-cards/cal", 0xFFF59E0BL, region = ServiceRegion.ISRAEL))
        add(apkm("il_ravkav", "רב-קו אונליין (Rav-Kav)", ServiceCategory.ANDROID_POWER, "hopon/rav-kav-online", 0xFF22C55EL, region = ServiceRegion.ISRAEL))
        add(apkm("il_moovit", "Moovit", ServiceCategory.GOOGLE_PIXEL, "moovit-inc/moovit", 0xFFF97316L, region = ServiceRegion.ISRAEL))
        add(apkm("il_waze", "Waze", ServiceCategory.GOOGLE_PIXEL, "waze-mobile-limited/waze", 0xFF38BDF8L, region = ServiceRegion.ISRAEL))
        add(apkm("il_gett", "Gett", ServiceCategory.GOOGLE_PIXEL, "gett-gettaxi/gett", 0xFFFBBF24L, region = ServiceRegion.ISRAEL))
        add(apkm("il_wolt", "Wolt", ServiceCategory.SMART_HOME_MEDIA, "wolt/wolt", 0xFF0EA5E9L, region = ServiceRegion.ISRAEL))
        add(apkm("il_pango", "Pango (פנגו)", ServiceCategory.ANDROID_POWER, "pango/pango", 0xFF3B82F6L, region = ServiceRegion.ISRAEL))
        add(apkm("il_cello", "Cello (סלופארק)", ServiceCategory.ANDROID_POWER, "cellopark/cellopark", 0xFF10B981L, region = ServiceRegion.ISRAEL))
        add(apkm("il_maccabi", "מכבי שירותי בריאות", ServiceCategory.SECURITY_PRIVACY, "maccabi-healthcare-services/maccabi", 0xFF2563EBL, region = ServiceRegion.ISRAEL))
        add(apkm("il_clalit", "כללית (Clalit)", ServiceCategory.SECURITY_PRIVACY, "clalit-health-services/clalit", 0xFF22C55EL, region = ServiceRegion.ISRAEL))
        add(apkm("il_meuhedet", "מאוחדת (Meuhedet)", ServiceCategory.SECURITY_PRIVACY, "meuhedet/meuhedet", 0xFFF97316L, region = ServiceRegion.ISRAEL))
        add(apkm("il_leumit", "לאומית (Leumit)", ServiceCategory.SECURITY_PRIVACY, "leumit-health-services/leumit", 0xFF38BDF8L, region = ServiceRegion.ISRAEL))
        add(apkm("il_oref", "פיקוד העורף (Home Front)", ServiceCategory.SECURITY_PRIVACY, "home-front-command/pikud-haoref", 0xFFF97316L, region = ServiceRegion.ISRAEL))
        add(apkm("il_tzofar", "צופר - צבע אדום (Tzofar)", ServiceCategory.SECURITY_PRIVACY, "tzofar/tzofar", 0xFFEF4444L, region = ServiceRegion.ISRAEL))
        add(apkm("il_12plus", "12+ (מאקו / קשת)", ServiceCategory.SMART_HOME_MEDIA, "keshet-broadcasting/mako-tv", 0xFF38BDF8L, region = ServiceRegion.ISRAEL))
        add(apkm("il_kan", "כאן 11 (KAN)", ServiceCategory.SMART_HOME_MEDIA, "kan-israeli-public-broadcasting-corporation/kan", 0xFFE2E8F0L, region = ServiceRegion.ISRAEL))
        add(apkm("il_yes_plus", "yes+", ServiceCategory.SMART_HOME_MEDIA, "yes-dbs-satellite-services/yes-plus", 0xFF3B82F6L, region = ServiceRegion.ISRAEL))
        add(apkm("il_partner_tv", "Partner TV+", ServiceCategory.SMART_HOME_MEDIA, "partner-communications/partner-tv", 0xFF06B6D4L, region = ServiceRegion.ISRAEL))
        add(apkm("il_cellcom_tv", "סלקום TV", ServiceCategory.SMART_HOME_MEDIA, "cellcom/cellcom-tv", 0xFFA855F7L, region = ServiceRegion.ISRAEL))
        add(apkm("il_yad2", "יד2 (Yad2)", ServiceCategory.SELF_HOSTED, "yad2/yad2", 0xFFF97316L, region = ServiceRegion.ISRAEL))
        add(apkm("il_israel_post", "דואר ישראל", ServiceCategory.SELF_HOSTED, "israel-post/israel-post", 0xFFEF4444L, region = ServiceRegion.ISRAEL))
        add(apkm("il_bezeq", "בזק (My Bezeq)", ServiceCategory.SECURITY_PRIVACY, "bezeq/my-bezeq", 0xFF3B82F6L, region = ServiceRegion.ISRAEL))
        add(apkm("il_shufersal", "שופרסל (Shufersal)", ServiceCategory.SELF_HOSTED, "shufersal/shufersal", 0xFFEF4444L, region = ServiceRegion.ISRAEL))
        add(apkm("il_superpharm", "סופר-פארם", ServiceCategory.SELF_HOSTED, "super-pharm/super-pharm", 0xFF2563EBL, region = ServiceRegion.ISRAEL))
        add(apkm("il_ksp", "KSP", ServiceCategory.SELF_HOSTED, "ksp/ksp", 0xFF38BDF8L, region = ServiceRegion.ISRAEL))
        add(gh("il_israel_rail", "Israel Weather / Gov Open Data", ServiceCategory.PC_DEV, "hasadna/open-bus-map-search", 0xFF22C55EL, region = ServiceRegion.ISRAEL))
        add(gh("il_open_knesset", "Hasadna Open Budget", ServiceCategory.PC_DEV, "OpenBudget/BudgetKey", 0xFF60A5FAL, region = ServiceRegion.ISRAEL))

        // =====================================================================
        // 12. USA REGIONAL SERVICES (16)
        // =====================================================================
        add(apkm("us_venmo", "Venmo", ServiceCategory.MESSAGING_SOCIAL, "paypal-inc/venmo", 0xFF38BDF8L, region = ServiceRegion.USA))
        add(apkm("us_cashapp", "Cash App", ServiceCategory.MESSAGING_SOCIAL, "square-inc/cash-app", 0xFF22C55EL, region = ServiceRegion.USA))
        add(apkm("us_uber", "Uber", ServiceCategory.GOOGLE_PIXEL, "uber-technologies-inc/uber", 0xFFE2E8F0L, region = ServiceRegion.USA))
        add(apkm("us_lyft", "Lyft", ServiceCategory.GOOGLE_PIXEL, "lyft-inc/lyft", 0xFFF472B6L, region = ServiceRegion.USA))
        add(apkm("us_doordash", "DoorDash", ServiceCategory.SMART_HOME_MEDIA, "doordash/doordash", 0xFFEF4444L, region = ServiceRegion.USA))
        add(apkm("us_instacart", "Instacart", ServiceCategory.SMART_HOME_MEDIA, "instacart/instacart", 0xFF22C55EL, region = ServiceRegion.USA))
        add(apkm("us_hulu", "Hulu", ServiceCategory.SMART_HOME_MEDIA, "hulu/hulu", 0xFF10B981L, region = ServiceRegion.USA))
        add(apkm("us_peacock", "Peacock TV", ServiceCategory.SMART_HOME_MEDIA, "peacock-tv/peacock", 0xFFFBBF24L, region = ServiceRegion.USA))
        add(apkm("us_paramount", "Paramount+", ServiceCategory.SMART_HOME_MEDIA, "cbs-interactive-inc/paramount-plus", 0xFF2563EBL, region = ServiceRegion.USA))
        add(apkm("us_max", "Max (HBO)", ServiceCategory.SMART_HOME_MEDIA, "warnermedia-direct-llc/max", 0xFF3B82F6L, region = ServiceRegion.USA))
        add(apkm("us_disney", "Disney+", ServiceCategory.SMART_HOME_MEDIA, "disney/disney-plus", 0xFF0EA5E9L, region = ServiceRegion.USA))
        add(apkm("us_espn", "ESPN", ServiceCategory.SMART_HOME_MEDIA, "espn-inc/espn", 0xFFEF4444L, region = ServiceRegion.USA))
        add(apkm("us_robinhood", "Robinhood", ServiceCategory.SECURITY_PRIVACY, "robinhood/robinhood", 0xFF22C55EL, region = ServiceRegion.USA))
        add(apkm("us_coinbase", "Coinbase", ServiceCategory.SECURITY_PRIVACY, "coinbase/coinbase", 0xFF2563EBL, region = ServiceRegion.USA))
        add(apkm("us_chase", "Chase Mobile", ServiceCategory.SECURITY_PRIVACY, "jpmorgan-chase/chase-mobile", 0xFF0284C7L, region = ServiceRegion.USA))
        add(rss("us_noaa", "NOAA / NHC Advisories", ServiceCategory.PC_DEV, "https://www.nhc.noaa.gov/xml/TWOAT.xml", 0xFF38BDF8L, region = ServiceRegion.USA))

        // =====================================================================
        // 13. EUROPE REGIONAL SERVICES (12)
        // =====================================================================
        add(apkm("eu_revolut", "Revolut", ServiceCategory.SECURITY_PRIVACY, "revolut/revolut", 0xFF38BDF8L, region = ServiceRegion.EUROPE))
        add(apkm("eu_wise", "Wise (TransferWise)", ServiceCategory.SECURITY_PRIVACY, "transferwise/wise", 0xFF22C55EL, region = ServiceRegion.EUROPE))
        add(apkm("eu_n26", "N26 Bank", ServiceCategory.SECURITY_PRIVACY, "n26/n26", 0xFF14B8A6L, region = ServiceRegion.EUROPE))
        add(apkm("eu_bunq", "bunq", ServiceCategory.SECURITY_PRIVACY, "bunq/bunq", 0xFF60A5FAL, region = ServiceRegion.EUROPE))
        add(apkm("eu_blablacar", "BlaBlaCar", ServiceCategory.GOOGLE_PIXEL, "comuto/blablacar", 0xFF0EA5E9L, region = ServiceRegion.EUROPE))
        add(apkm("eu_flixbus", "FlixBus", ServiceCategory.GOOGLE_PIXEL, "flixbus/flixbus", 0xFF22C55EL, region = ServiceRegion.EUROPE))
        add(apkm("eu_trainline", "Trainline", ServiceCategory.GOOGLE_PIXEL, "trainline/trainline", 0xFF10B981L, region = ServiceRegion.EUROPE))
        add(apkm("eu_vinted", "Vinted", ServiceCategory.SELF_HOSTED, "vinted/vinted", 0xFF14B8A6L, region = ServiceRegion.EUROPE))
        add(apkm("eu_zalando", "Zalando", ServiceCategory.SELF_HOSTED, "zalando-se/zalando", 0xFFF97316L, region = ServiceRegion.EUROPE))
        add(apkm("eu_allegro", "Allegro", ServiceCategory.SELF_HOSTED, "allegro/allegro", 0xFFF97316L, region = ServiceRegion.EUROPE))
        add(apkm("eu_glovo", "Glovo", ServiceCategory.SMART_HOME_MEDIA, "glovo/glovo", 0xFFFBBF24L, region = ServiceRegion.EUROPE))
        add(apkm("eu_bolt", "Bolt", ServiceCategory.GOOGLE_PIXEL, "taxify/bolt", 0xFF22C55EL, region = ServiceRegion.EUROPE))

        // =====================================================================
        // 14. UK REGIONAL SERVICES (10)
        // =====================================================================
        add(apkm("uk_monzo", "Monzo Bank", ServiceCategory.SECURITY_PRIVACY, "monzo/monzo", 0xFFF43F5EL, region = ServiceRegion.UK))
        add(apkm("uk_starling", "Starling Bank", ServiceCategory.SECURITY_PRIVACY, "starling-bank/starling-bank", 0xFF8B5CF6L, region = ServiceRegion.UK))
        add(apkm("uk_bbc_iplayer", "BBC iPlayer", ServiceCategory.SMART_HOME_MEDIA, "bbc/bbc-iplayer", 0xFFF43F5EL, region = ServiceRegion.UK))
        add(apkm("uk_itvx", "ITVX", ServiceCategory.SMART_HOME_MEDIA, "itv-plc/itv-hub", 0xFF34D399L, region = ServiceRegion.UK))
        add(apkm("uk_channel4", "Channel 4", ServiceCategory.SMART_HOME_MEDIA, "channel-4/all-4", 0xFFA855F7L, region = ServiceRegion.UK))
        add(apkm("uk_deliveroo", "Deliveroo", ServiceCategory.SMART_HOME_MEDIA, "deliveroo/deliveroo", 0xFF14B8A6L, region = ServiceRegion.UK))
        add(apkm("uk_national_rail", "National Rail Enquiries", ServiceCategory.GOOGLE_PIXEL, "rail-delivery-group/national-rail", 0xFF3B82F6L, region = ServiceRegion.UK))
        add(apkm("uk_tesco", "Tesco Grocery", ServiceCategory.SELF_HOSTED, "tesco-stores-limited/tesco-groceries", 0xFF2563EBL, region = ServiceRegion.UK))
        add(apkm("uk_nhs", "NHS App", ServiceCategory.SECURITY_PRIVACY, "nhs-digital/nhs-app", 0xFF0EA5E9L, region = ServiceRegion.UK))
        add(apkm("uk_sky_go", "Sky Go", ServiceCategory.SMART_HOME_MEDIA, "sky-uk-limited/sky-go", 0xFF60A5FAL, region = ServiceRegion.UK))

        // =====================================================================
        // 15. ASIA REGIONAL SERVICES (12)
        // =====================================================================
        add(apkm("asia_wechat", "WeChat", ServiceCategory.MESSAGING_SOCIAL, "tencent-technology-shenzhen-company-ltd/wechat", 0xFF22C55EL, region = ServiceRegion.ASIA))
        add(apkm("asia_line", "LINE", ServiceCategory.MESSAGING_SOCIAL, "line-corporation/line", 0xFF10B981L, region = ServiceRegion.ASIA))
        add(apkm("asia_kakaotalk", "KakaoTalk", ServiceCategory.MESSAGING_SOCIAL, "kakao-corporation/kakaotalk", 0xFFFBBF24L, region = ServiceRegion.ASIA))
        add(apkm("asia_grab", "Grab", ServiceCategory.GOOGLE_PIXEL, "grabtaxi-holdings/grab", 0xFF22C55EL, region = ServiceRegion.ASIA))
        add(apkm("asia_gojek", "Gojek", ServiceCategory.GOOGLE_PIXEL, "pt-aplikasi-karya-anak-bangsa/gojek", 0xFF10B981L, region = ServiceRegion.ASIA))
        add(apkm("asia_shopee", "Shopee", ServiceCategory.SELF_HOSTED, "shopee/shopee", 0xFFF97316L, region = ServiceRegion.ASIA))
        add(apkm("asia_lazada", "Lazada", ServiceCategory.SELF_HOSTED, "lazada-mobile/lazada", 0xFF6366F1L, region = ServiceRegion.ASIA))
        add(apkm("asia_paytm", "Paytm", ServiceCategory.SECURITY_PRIVACY, "one97-communications/paytm", 0xFF0EA5E9L, region = ServiceRegion.ASIA))
        add(apkm("asia_phonepe", "PhonePe", ServiceCategory.SECURITY_PRIVACY, "phonepe/phonepe", 0xFF8B5CF6L, region = ServiceRegion.ASIA))
        add(apkm("asia_bilibili", "Bilibili", ServiceCategory.SMART_HOME_MEDIA, "bilibili/bilibili", 0xFFF472B6L, region = ServiceRegion.ASIA))
        add(apkm("asia_rakuten", "Rakuten", ServiceCategory.SELF_HOSTED, "rakuten-inc/rakuten", 0xFFEF4444L, region = ServiceRegion.ASIA))
        add(apkm("asia_jiohotstar", "JioHotstar", ServiceCategory.SMART_HOME_MEDIA, "novi-digital/hotstar", 0xFF2563EBL, region = ServiceRegion.ASIA))

        // =====================================================================
        // 16. GITLAB RELEASES (8)
        // =====================================================================
        add(gl("gl_aurora_store", "Aurora Store (GitLab)", ServiceCategory.ANDROID_POWER, "AuroraOSS/AuroraStore", 0xFF38BDF8L))
        add(gl("gl_fdroid_client", "F-Droid Client (GitLab)", ServiceCategory.ANDROID_POWER, "fdroid/fdroidclient", 0xFF22C55EL))
        add(gl("gl_inkscape", "Inkscape (GitLab)", ServiceCategory.PC_DEV, "inkscape/inkscape", 0xFFE2E8F0L))
        add(gl("gl_gitlab_runner", "GitLab Runner", ServiceCategory.PC_DEV, "gitlab-org/gitlab-runner", 0xFFF97316L))
        add(gl("gl_gitlab_cli", "GitLab CLI (glab)", ServiceCategory.PC_DEV, "gitlab-org/cli", 0xFFF97316L))
        add(gl("gl_organic_maps", "Organic Maps (GitLab)", ServiceCategory.GOOGLE_PIXEL, "organicmaps/organicmaps", 0xFF10B981L))
        add(gl("gl_veloren", "Veloren (GitLab)", ServiceCategory.GAMING_EMULATION, "veloren/veloren", 0xFF38BDF8L))
        add(gl("gl_postmarketos", "biktorgj / PinePhone Modem", ServiceCategory.ANDROID_POWER, "postmarketOS/pmbootstrap", 0xFF22C55EL))

        // =====================================================================
        // 17. NPM REGISTRY PACKAGES (12)
        // =====================================================================
        add(npm("npm_claude_code", "Claude Code CLI (npm)", ServiceCategory.AI_LLM, "@anthropic-ai/claude-code", 0xFFF97316L))
        add(npm("npm_openai_codex", "OpenAI Codex CLI (npm)", ServiceCategory.AI_LLM, "@openai/codex", 0xFF10B981L))
        add(npm("npm_gemini_cli", "Google Gemini CLI (npm)", ServiceCategory.AI_LLM, "@google/gemini-cli", 0xFF60A5FAL))
        add(npm("npm_typescript", "TypeScript (npm)", ServiceCategory.PC_DEV, "typescript", 0xFF3B82F6L))
        add(npm("npm_nextjs", "Next.js (npm)", ServiceCategory.PC_DEV, "next", 0xFFE2E8F0L))
        add(npm("npm_react", "React (npm)", ServiceCategory.PC_DEV, "react", 0xFF38BDF8L))
        add(npm("npm_vite", "Vite (npm)", ServiceCategory.PC_DEV, "vite", 0xFFA855F7L))
        add(npm("npm_tailwindcss", "Tailwind CSS (npm)", ServiceCategory.PC_DEV, "tailwindcss", 0xFF06B6D4L))
        add(npm("npm_pnpm", "pnpm (npm)", ServiceCategory.PC_DEV, "pnpm", 0xFFFBBF24L))
        add(npm("npm_eslint", "ESLint (npm)", ServiceCategory.PC_DEV, "eslint", 0xFF818CF8L))
        add(npm("npm_prisma", "Prisma ORM (npm)", ServiceCategory.PC_DEV, "prisma", 0xFF2DD4BFL))
        add(npm("npm_playwright", "Playwright (npm)", ServiceCategory.PC_DEV, "playwright", 0xFF22C55EL))

        // =====================================================================
        // 18. PYPI PYTHON PACKAGES (12)
        // =====================================================================
        add(pypi("pypi_openai", "openai (PyPI)", ServiceCategory.AI_LLM, "openai", 0xFF10B981L))
        add(pypi("pypi_anthropic", "anthropic (PyPI)", ServiceCategory.AI_LLM, "anthropic", 0xFFF97316L))
        add(pypi("pypi_google_genai", "google-genai (PyPI)", ServiceCategory.AI_LLM, "google-genai", 0xFF60A5FAL))
        add(pypi("pypi_langchain", "langchain (PyPI)", ServiceCategory.AI_LLM, "langchain", 0xFF22C55EL))
        add(pypi("pypi_transformers", "transformers (PyPI)", ServiceCategory.AI_LLM, "transformers", 0xFFFBBF24L))
        add(pypi("pypi_torch", "PyTorch (PyPI)", ServiceCategory.AI_LLM, "torch", 0xFFEF4444L))
        add(pypi("pypi_fastapi", "FastAPI (PyPI)", ServiceCategory.PC_DEV, "fastapi", 0xFF14B8A6L))
        add(pypi("pypi_pydantic", "Pydantic (PyPI)", ServiceCategory.PC_DEV, "pydantic", 0xFFF43F5EL))
        add(pypi("pypi_uv", "uv (PyPI)", ServiceCategory.PC_DEV, "uv", 0xFFA855F7L))
        add(pypi("pypi_ruff", "Ruff (PyPI)", ServiceCategory.PC_DEV, "ruff", 0xFFD946EFL))
        add(pypi("pypi_yt_dlp", "yt-dlp (PyPI)", ServiceCategory.SMART_HOME_MEDIA, "yt-dlp", 0xFFEF4444L))
        add(pypi("pypi_homeassistant", "Home Assistant Core (PyPI)", ServiceCategory.SMART_HOME_MEDIA, "homeassistant", 0xFF38BDF8L))

        // =====================================================================
        // 19. DOCKER HUB IMAGES (12)
        // =====================================================================
        add(docker("docker_nginx", "Nginx (Docker Hub)", ServiceCategory.SELF_HOSTED, "nginx", 0xFF22C55EL))
        add(docker("docker_postgres", "PostgreSQL (Docker Hub)", ServiceCategory.SELF_HOSTED, "postgres", 0xFF3B82F6L))
        add(docker("docker_redis", "Redis (Docker Hub)", ServiceCategory.SELF_HOSTED, "redis", 0xFFEF4444L))
        add(docker("docker_traefik", "Traefik Proxy (Docker Hub)", ServiceCategory.SELF_HOSTED, "traefik", 0xFF06B6D4L))
        add(docker("docker_portainer", "Portainer CE (Docker Hub)", ServiceCategory.SELF_HOSTED, "portainer/portainer-ce", 0xFF38BDF8L))
        add(docker("docker_uptime_kuma", "Uptime Kuma (Docker Hub)", ServiceCategory.SELF_HOSTED, "louislam/uptime-kuma", 0xFF22C55EL))
        add(docker("docker_jellyfin", "Jellyfin (Docker Hub)", ServiceCategory.SMART_HOME_MEDIA, "jellyfin/jellyfin", 0xFFA855F7L))
        add(docker("docker_plex", "Plex Media Server (Docker Hub)", ServiceCategory.SMART_HOME_MEDIA, "linuxserver/plex", 0xFFFBBF24L))
        add(docker("docker_qbittorrent", "qBittorrent (Docker Hub)", ServiceCategory.SELF_HOSTED, "linuxserver/qbittorrent", 0xFF60A5FAL))
        add(docker("docker_homeassistant", "Home Assistant (Docker Hub)", ServiceCategory.SMART_HOME_MEDIA, "homeassistant/home-assistant", 0xFF38BDF8L))
        add(docker("docker_dozzle", "Dozzle Log Viewer (Docker Hub)", ServiceCategory.SELF_HOSTED, "amir20/dozzle", 0xFF14B8A6L))
        add(docker("docker_caddy", "Caddy Web Server (Docker Hub)", ServiceCategory.SELF_HOSTED, "caddy", 0xFF10B981L))

        // =====================================================================
        // 20. CLOUD & AI STATUS FEEDS (6)
        // =====================================================================
        add(rss("status_github", "GitHub Status", ServiceCategory.PC_DEV, "https://www.githubstatus.com/history.rss", 0xFFE2E8F0L))
        add(rss("status_cloudflare", "Cloudflare Status", ServiceCategory.SECURITY_PRIVACY, "https://www.cloudflarestatus.com/history.rss", 0xFFF97316L))
        add(rss("status_openai", "OpenAI Status", ServiceCategory.AI_LLM, "https://status.openai.com/history.rss", 0xFF10B981L))
        add(rss("status_anthropic", "Anthropic (Claude) Status", ServiceCategory.AI_LLM, "https://status.anthropic.com/history.rss", 0xFFF97316L))
        add(rss("status_discord", "Discord Status", ServiceCategory.MESSAGING_SOCIAL, "https://discordstatus.com/history.rss", 0xFF818CF8L))
        add(rss("status_vercel", "Vercel Status", ServiceCategory.PC_DEV, "https://www.vercel-status.com/history.rss", 0xFFE2E8F0L))
    }
}
