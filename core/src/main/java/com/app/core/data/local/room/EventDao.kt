package com.app.core.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.core.data.local.entity.EventEntity

@Dao
interface EventDao {

    @Query("SELECT * FROM event")
    fun getFavoriteEvent(): LiveData<List<EventEntity>>

    @Delete
    suspend fun deleteEvent(eventEntity: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(eventEntity: EventEntity)

    @Query("SELECT EXISTS(SELECT * FROM event WHERE id = :eventId)")
    suspend fun isEventFavorite(eventId: String): Boolean
}