package com.app.wecollabs.ui.eventDetail

import androidx.lifecycle.ViewModel
import com.app.core.data.local.entity.EventEntity
import com.app.core.data.repository.EventRepository

class DetailEventViewModel(private val eventRepository: EventRepository): ViewModel() {
    suspend fun deleteFavoriteEvent(eventEntity: EventEntity) = eventRepository.deleteFavoriteEvent(eventEntity)
    suspend fun insertFavoriteEvent(eventEntity: EventEntity) = eventRepository.insertFavoriteEvent(eventEntity)
    suspend fun isEventFavorite(eventId: String) = eventRepository.isEventFavorite(eventId)
}