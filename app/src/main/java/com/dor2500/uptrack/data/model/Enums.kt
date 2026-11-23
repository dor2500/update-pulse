package com.dor2500.uptrack.data.model

import androidx.annotation.StringRes
import com.dor2500.uptrack.R
import kotlinx.serialization.Serializable

@Serializable
enum class ServiceCategory(@StringRes val labelRes: Int) {
    GOOGLE_PIXEL(R.string.cat_google_pixel),
    MESSAGING_SOCIAL(R.string.cat_messaging_social),
    BROWSERS(R.string.cat_browsers),
    ANDROID_POWER(R.string.cat_android_power),
    AI_LLM(R.string.cat_ai_llm),
    WINDOWS_OS(R.string.cat_windows_os),
    PC_DEV(R.string.cat_pc_dev),
    SMART_HOME_MEDIA(R.string.cat_smart_home_media),
    SECURITY_PRIVACY(R.string.cat_security_privacy),
    SELF_HOSTED(R.string.cat_self_hosted),
    GAMING_EMULATION(R.string.cat_gaming_emulation),
    BETA(R.string.cat_beta),
    CUSTOM(R.string.cat_custom)
}

@Serializable
enum class ServiceRegion(@StringRes val labelRes: Int) {
    GLOBAL(R.string.region_global),
    ISRAEL(R.string.region_israel),
    USA(R.string.region_usa),
    EUROPE(R.string.region_europe),
    UK(R.string.region_uk),
    ASIA(R.string.region_asia)
}

@Serializable
enum class SourceType(@StringRes val labelRes: Int) {
    GITHUB_RELEASES(R.string.source_github),
    GITLAB_RELEASES(R.string.source_gitlab),
    NPM_REGISTRY(R.string.source_npm),
    PYPI_PACKAGE(R.string.source_pypi),
    DOCKER_HUB(R.string.source_docker),
    RSS_ATOM(R.string.source_rss),
    JSON_ENDPOINT(R.string.source_json),
    WEBPAGE_REGEX(R.string.source_webpage)
}

enum class ThemeMode(@StringRes val labelRes: Int) {
    DARK(R.string.theme_dark),
    AMOLED(R.string.theme_amoled),
    LIGHT(R.string.theme_light),
    SYSTEM(R.string.theme_system)
}

enum class PollInterval(val minutes: Long, val label: String) {
    MIN_15(15, "15 min"),
    MIN_30(30, "30 min"),
    HOUR_1(60, "1 hour"),
    HOUR_3(180, "3 hours"),
    HOUR_6(360, "6 hours"),
    HOUR_12(720, "12 hours"),
    HOUR_24(1440, "24 hours")
}

enum class AppLanguage(val tag: String, @StringRes val labelRes: Int) {
    SYSTEM("", R.string.lang_system),
    ENGLISH("en", R.string.lang_english),
    HEBREW("iw", R.string.lang_hebrew)
}

enum class ReleaseChannel(@StringRes val labelRes: Int) {
    ALL(R.string.channel_all),
    REGULAR(R.string.channel_regular),
    BETA(R.string.channel_beta)
}
