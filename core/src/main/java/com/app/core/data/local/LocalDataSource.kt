package com.app.core.data.local

import com.app.core.data.local.entity.EventEntity
import com.app.core.data.local.room.EventDao

class LocalDataSource(private val eventDao: EventDao) {
    fun getAllEvent() = eventDao.getFavoriteEvent()
    suspend fun insertEvent(eventEntity: EventEntity) = eventDao.insertEvent(eventEntity)
    suspend fun deleteEvent(eventEntity: EventEntity) = eventDao.deleteEvent(eventEntity)
    suspend fun isEventFavorite(eventId: String) = eventDao.isEventFavorite(eventId)
}