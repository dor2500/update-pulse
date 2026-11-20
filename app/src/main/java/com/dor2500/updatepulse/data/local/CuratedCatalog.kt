package com.dor2500.updatepulse.data.local

import com.dor2500.updatepulse.data.model.ServiceCategory
import com.dor2500.updatepulse.data.model.SourceType

object CuratedCatalog {
    val defaultServices: List<TrackedServiceEntity> = listOf(
        // 1. Google & Pixel
        TrackedServiceEntity(
            id = "google_android_beta",
            name = "Android / Pixel Beta Program",
            category = ServiceCategory.GOOGLE_PIXEL,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://android-developers.googleblog.com/feeds/posts/default",
            accentColorHex = 0xFF34D399L,
            isSubscribed = true
        ),
        TrackedServiceEntity(
            id = "google_pixel_ota",
            name = "Google Pixel Factory Images & OTA",
            category = ServiceCategory.GOOGLE_PIXEL,
            sourceType = SourceType.WEBPAGE_REGEX,
            sourceUrlOrRepo = "https://developers.google.com/android/ota",
            regexPattern = """(\d+\.\d+\.\d+\s*\([A-Z0-9.]+\))""",
            accentColorHex = 0xFF60A5FAL
        ),
        TrackedServiceEntity(
            id = "google_play_services",
            name = "Google Play Services",
            category = ServiceCategory.GOOGLE_PIXEL,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/google-inc/google-play-services/feed/",
            accentColorHex = 0xFF38BDF8L
        ),
        TrackedServiceEntity(
            id = "google_pixel_camera",
            name = "Pixel Camera (Google Camera)",
            category = ServiceCategory.GOOGLE_PIXEL,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/google-inc/camera/feed/",
            accentColorHex = 0xFF818CF8L
        ),
        TrackedServiceEntity(
            id = "google_webview",
            name = "Android System WebView",
            category = ServiceCategory.GOOGLE_PIXEL,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/google-inc/android-system-webview/feed/",
            accentColorHex = 0xFF2DD4BFL
        ),
        TrackedServiceEntity(
            id = "google_pixel_watch",
            name = "Pixel Watch App Updates",
            category = ServiceCategory.GOOGLE_PIXEL,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/google-inc/google-pixel-watch/feed/",
            accentColorHex = 0xFFA78BFAL
        ),

        // 2. Messaging & Social
        TrackedServiceEntity(
            id = "msg_whatsapp_beta",
            name = "WhatsApp Beta",
            category = ServiceCategory.MESSAGING_SOCIAL,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/whatsapp-inc/whatsapp/feed/",
            accentColorHex = 0xFF22C55EL
        ),
        TrackedServiceEntity(
            id = "msg_whatsapp_stable",
            name = "WhatsApp Business / Stable",
            category = ServiceCategory.MESSAGING_SOCIAL,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/whatsapp-inc/whatsapp-business/feed/",
            accentColorHex = 0xFF10B981L
        ),
        TrackedServiceEntity(
            id = "msg_telegram",
            name = "Telegram",
            category = ServiceCategory.MESSAGING_SOCIAL,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/telegram-fz-llc/telegram/feed/",
            accentColorHex = 0xFF38BDF8L
        ),
        TrackedServiceEntity(
            id = "msg_discord",
            name = "Discord",
            category = ServiceCategory.MESSAGING_SOCIAL,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/discord-inc/discord-chat-for-gamers/feed/",
            accentColorHex = 0xFF818CF8L
        ),
        TrackedServiceEntity(
            id = "msg_signal",
            name = "Signal Android",
            category = ServiceCategory.MESSAGING_SOCIAL,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "signalapp/Signal-Android",
            accentColorHex = 0xFF3B82F6L
        ),
        TrackedServiceEntity(
            id = "msg_instagram",
            name = "Instagram Beta",
            category = ServiceCategory.MESSAGING_SOCIAL,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/instagram/instagram-instagram/feed/",
            accentColorHex = 0xFFF472B6L
        ),

        // 3. Browsers
        TrackedServiceEntity(
            id = "browser_chrome_beta",
            name = "Google Chrome Beta",
            category = ServiceCategory.BROWSERS,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/google-inc/chrome-beta/feed/",
            accentColorHex = 0xFFFBBF24L
        ),
        TrackedServiceEntity(
            id = "browser_firefox_nightly",
            name = "Firefox Nightly",
            category = ServiceCategory.BROWSERS,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/mozilla/firefox-nightly-for-developers/feed/",
            accentColorHex = 0xFFFB923CL
        ),
        TrackedServiceEntity(
            id = "browser_brave",
            name = "Brave Browser",
            category = ServiceCategory.BROWSERS,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "brave/brave-browser",
            accentColorHex = 0xFFF97316L
        ),
        TrackedServiceEntity(
            id = "browser_vivaldi",
            name = "Vivaldi Browser",
            category = ServiceCategory.BROWSERS,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/vivaldi-technologies/vivaldi-browser-beta/feed/",
            accentColorHex = 0xFFEF4444L
        ),

        // 4. Android Power-User & Modding
        TrackedServiceEntity(
            id = "power_magisk",
            name = "Magisk",
            category = ServiceCategory.ANDROID_POWER,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "topjohnwu/Magisk",
            accentColorHex = 0xFF14B8A6L,
            isSubscribed = true
        ),
        TrackedServiceEntity(
            id = "power_kernelsu",
            name = "KernelSU",
            category = ServiceCategory.ANDROID_POWER,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "tiann/KernelSU",
            accentColorHex = 0xFF06B6D4L
        ),
        TrackedServiceEntity(
            id = "power_shizuku",
            name = "Shizuku",
            category = ServiceCategory.ANDROID_POWER,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "RikkaApps/Shizuku",
            accentColorHex = 0xFF6366F1L
        ),
        TrackedServiceEntity(
            id = "power_revanced",
            name = "ReVanced Manager",
            category = ServiceCategory.ANDROID_POWER,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "ReVanced/revanced-manager",
            accentColorHex = 0xFF8B5CF6L
        ),
        TrackedServiceEntity(
            id = "power_obtainium",
            name = "Obtainium",
            category = ServiceCategory.ANDROID_POWER,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "ImranR98/Obtainium",
            accentColorHex = 0xFFA855F7L,
            isSubscribed = true
        ),
        TrackedServiceEntity(
            id = "power_termux",
            name = "Termux",
            category = ServiceCategory.ANDROID_POWER,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "termux/termux-app",
            accentColorHex = 0xFF64748BL
        ),
        TrackedServiceEntity(
            id = "power_adaway",
            name = "AdAway",
            category = ServiceCategory.ANDROID_POWER,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "AdAway/AdAway",
            accentColorHex = 0xFFEF4444L
        ),
        TrackedServiceEntity(
            id = "power_fdroid",
            name = "F-Droid Client",
            category = ServiceCategory.ANDROID_POWER,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://gitlab.com/fdroid/fdroidclient/-/tags?format=atom",
            accentColorHex = 0xFF3B82F6L
        ),

        // 5. PC, OS & Dev Tools
        TrackedServiceEntity(
            id = "dev_win11_insider",
            name = "Windows 11 Insider Preview",
            category = ServiceCategory.PC_DEV,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://blogs.windows.com/windows-insider/feed/",
            accentColorHex = 0xFF0EA5E9L
        ),
        TrackedServiceEntity(
            id = "dev_powertoys",
            name = "Microsoft PowerToys",
            category = ServiceCategory.PC_DEV,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "microsoft/PowerToys",
            accentColorHex = 0xFF38BDF8L,
            isSubscribed = true
        ),
        TrackedServiceEntity(
            id = "dev_vscode",
            name = "Visual Studio Code",
            category = ServiceCategory.PC_DEV,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "microsoft/vscode",
            accentColorHex = 0xFF2563EBL
        ),
        TrackedServiceEntity(
            id = "dev_docker",
            name = "Docker Desktop",
            category = ServiceCategory.PC_DEV,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://docs.docker.com/desktop/release-notes/index.xml",
            accentColorHex = 0xFF0284C7L
        ),
        TrackedServiceEntity(
            id = "dev_ollama",
            name = "Ollama",
            category = ServiceCategory.PC_DEV,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "ollama/ollama",
            accentColorHex = 0xFFE2E8F0L
        ),
        TrackedServiceEntity(
            id = "dev_home_assistant",
            name = "Home Assistant Core",
            category = ServiceCategory.PC_DEV,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "home-assistant/core",
            accentColorHex = 0xFF38BDF8L
        ),
        TrackedServiceEntity(
            id = "dev_pihole",
            name = "Pi-hole",
            category = ServiceCategory.PC_DEV,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "pi-hole/pi-hole",
            accentColorHex = 0xFFF43F5EL
        ),
        TrackedServiceEntity(
            id = "dev_plex",
            name = "Plex Media Server",
            category = ServiceCategory.PC_DEV,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://forums.plex.tv/c/announcements/5.rss",
            accentColorHex = 0xFFF59E0BL
        ),

        // 6. Smart Home & Media
        TrackedServiceEntity(
            id = "media_stremio",
            name = "Stremio",
            category = ServiceCategory.SMART_HOME_MEDIA,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "Stremio/stremio-shell",
            accentColorHex = 0xFF8B5CF6L
        ),
        TrackedServiceEntity(
            id = "media_vlc",
            name = "VLC for Android",
            category = ServiceCategory.SMART_HOME_MEDIA,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/videolabs/vlc/feed/",
            accentColorHex = 0xFFF97316L
        ),
        TrackedServiceEntity(
            id = "media_kodi",
            name = "Kodi",
            category = ServiceCategory.SMART_HOME_MEDIA,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "xbmc/xbmc",
            accentColorHex = 0xFF0EA5E9L
        ),
        TrackedServiceEntity(
            id = "media_jellyfin",
            name = "Jellyfin",
            category = ServiceCategory.SMART_HOME_MEDIA,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "jellyfin/jellyfin",
            accentColorHex = 0xFFA855F7L
        ),
        TrackedServiceEntity(
            id = "media_tailscale",
            name = "Tailscale",
            category = ServiceCategory.SMART_HOME_MEDIA,
            sourceType = SourceType.GITHUB_RELEASES,
            sourceUrlOrRepo = "tailscale/tailscale",
            accentColorHex = 0xFF60A5FAL
        ),
        TrackedServiceEntity(
            id = "media_wireguard",
            name = "WireGuard",
            category = ServiceCategory.SMART_HOME_MEDIA,
            sourceType = SourceType.RSS_ATOM,
            sourceUrlOrRepo = "https://www.apkmirror.com/apk/wireguard-development-team/wireguard/feed/",
            accentColorHex = 0xFFDC2626L
        )
    )
}
