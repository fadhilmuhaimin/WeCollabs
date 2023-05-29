package com.app.organizationpanel.ui.addEvent

import androidx.lifecycle.ViewModel
import com.app.core.data.models.Event
import com.app.core.data.repository.EventRepository
import java.io.File

class AddEventViewModel(private val eventRepository: EventRepository): ViewModel() {

    var selectedEvent: Event? = null

    var imageFile: File? = null
    var title = ""
    var desc = ""
    var contactNumber = ""
    var date: String? = null
    var address: String? = null
    var addressCoordinate: Map<String, Double?>? = null
    var isComingSoon = false

    fun addEvent(event: Event, image: File) = eventRepository.addEvent(event, image)

    fun updateEvent(event: Event, image: File?) = eventRepository.updateEvent(event, image)

    fun deleteEvent(eventId: String) = eventRepository.deleteEvent(eventId)

}