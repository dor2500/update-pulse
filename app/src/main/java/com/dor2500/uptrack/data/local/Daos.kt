package com.dor2500.uptrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dor2500.uptrack.data.model.ServiceCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedServiceDao {
    @Query("SELECT * FROM tracked_services ORDER BY isCustom DESC, name ASC")
    fun observeAll(): Flow<List<TrackedServiceEntity>>

    @Query("SELECT * FROM tracked_services WHERE isCustom = 1 ORDER BY name ASC")
    fun observeCustom(): Flow<List<TrackedServiceEntity>>

    @Query("SELECT * FROM tracked_services WHERE isSubscribed = 1")
    suspend fun getSubscribed(): List<TrackedServiceEntity>

    @Query("SELECT * FROM tracked_services")
    suspend fun getAll(): List<TrackedServiceEntity>

    @Query("SELECT * FROM tracked_services WHERE id = :id")
    suspend fun getById(id: String): TrackedServiceEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnoreAll(services: List<TrackedServiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(service: TrackedServiceEntity)

    @Update
    suspend fun update(service: TrackedServiceEntity)

    @Query("UPDATE tracked_services SET isSubscribed = :subscribed WHERE id = :id")
    suspend fun setSubscribed(id: String, subscribed: Boolean)

    @Query("UPDATE tracked_services SET notificationsEnabled = :enabled WHERE id = :id")
    suspend fun setNotificationsEnabled(id: String, enabled: Boolean)

    @Query("UPDATE tracked_services SET isSubscribed = 1, notificationsEnabled = 1 WHERE id IN (:ids)")
    suspend fun enableTrackingAndNotifications(ids: List<String>)

    @Query("UPDATE tracked_services SET category = :category WHERE id IN (:ids) AND isCustom = 0")
    suspend fun updateCuratedCategory(ids: List<String>, category: ServiceCategory)

    @Query("DELETE FROM tracked_services WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface UpdateEventDao {
    @Query("SELECT * FROM update_events ORDER BY detectedAt DESC")
    fun observeAll(): Flow<List<UpdateEventEntity>>

    @Query("SELECT COUNT(*) FROM update_events WHERE isRead = 0")
    fun observeUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: UpdateEventEntity): Long

    @Query("UPDATE update_events SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: Long)

    @Query("UPDATE update_events SET isRead = 1")
    suspend fun markAllRead()

    @Query("DELETE FROM update_events")
    suspend fun deleteAll()

    @Query("DELETE FROM update_events WHERE serviceId = :serviceId")
    suspend fun deleteForService(serviceId: String)
}
