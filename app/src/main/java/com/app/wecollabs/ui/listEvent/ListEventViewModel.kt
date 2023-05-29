package com.app.wecollabs.ui.listEvent

import androidx.lifecycle.ViewModel
import com.app.core.data.repository.EventRepository

class ListEventViewModel(
    private val eventRepository: EventRepository
): ViewModel() {

    fun getEvents() = eventRepository.getEvents()

}