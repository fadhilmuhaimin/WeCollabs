package com.app.wecollabs.ui.main.fragments.favorite

import androidx.lifecycle.ViewModel
import com.app.core.data.repository.EventRepository

class FavoriteViewModel(private val eventRepository: EventRepository) : ViewModel() {

    fun getFavoriteEvents() = eventRepository.getFavoriteEvents()

}