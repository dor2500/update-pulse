package com.dor2500.updatepulse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dor2500.updatepulse.data.model.ServiceCategory
import com.dor2500.updatepulse.data.model.ServiceRegion
import com.dor2500.updatepulse.data.model.SourceType
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tracked_services")
data class TrackedServiceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: ServiceCategory,
    val sourceType: SourceType,
    val sourceUrlOrRepo: String,
    val region: ServiceRegion = ServiceRegion.GLOBAL,
    val includePreReleases: Boolean = false,
    val jsonKey: String? = null,
    val regexPattern: String? = null,
    val accentColorHex: Long = 0xFF60A5FAL,
    val isCustom: Boolean = false,
    val isSubscribed: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val lastKnownVersion: String? = null,
    val lastCheckedAt: Long? = null,
    val lastError: String? = null
)

@Entity(tableName = "update_events")
data class UpdateEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serviceId: String,
    val serviceName: String,
    val category: ServiceCategory,
    val version: String,
    val releaseTitle: String,
    val changelog: String,
    val releaseUrl: String,
    val detectedAt: Long,
    val isRead: Boolean = false,
    val accentColorHex: Long = 0xFF60A5FAL
)

private val BETA_CHANNEL_REGEX = Regex(
    """\b(beta|canary|nightly|alpha|insider|insiders|preview|pre-release)\b""",
    RegexOption.IGNORE_CASE
)

fun TrackedServiceEntity.isBetaChannel(): Boolean =
    includePreReleases ||
        category == ServiceCategory.BETA ||
        BETA_CHANNEL_REGEX.containsMatchIn(name)

fun UpdateEventEntity.isBetaChannel(): Boolean =
    category == ServiceCategory.BETA ||
        BETA_CHANNEL_REGEX.containsMatchIn(serviceName) ||
        BETA_CHANNEL_REGEX.containsMatchIn(version)
