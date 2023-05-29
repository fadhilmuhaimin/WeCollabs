package com.app.wecollabs.ui.searchEvent

import androidx.lifecycle.ViewModel
import com.app.core.data.repository.EventRepository

class SearchEventViewModel(private val eventRepository: EventRepository): ViewModel() {
    fun searchEvents(keyword: String) = eventRepository.searchEvents(keyword)
}