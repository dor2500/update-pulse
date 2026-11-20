package com.dor2500.updatepulse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dor2500.updatepulse.data.model.ServiceCategory
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
    val includePreReleases: Boolean = false,
    val jsonKey: String? = null,
    val regexPattern: String? = null,
    val accentColorHex: Long = 0xFF60A5FAL,
    val isCustom: Boolean = false,
    val isSubscribed: Boolean = false,
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
