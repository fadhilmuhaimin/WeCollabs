package com.app.organizationpanel.ui.organizationPanel.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.core.data.repository.EventRepository

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {

    fun getEvents(organizationId: String) = eventRepository.getEvents(organizationId)

}