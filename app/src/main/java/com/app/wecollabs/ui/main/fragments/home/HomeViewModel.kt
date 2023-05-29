package com.app.wecollabs.ui.main.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.app.core.data.repository.ArticleRepository
import com.app.core.data.repository.EventRepository
import com.app.core.data.repository.GoalRepository

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val goalRepository: GoalRepository,
    private val articleRepository: ArticleRepository
) : ViewModel() {

    fun getTrendingEvents() = eventRepository.getTrendingEvents(5)

    fun getComingSoonEvents() = eventRepository.getComingSoonEvents(3)

    fun getListGoalsWithLimit() = goalRepository.getListGoalsWithLimit(5)

    fun getArticlesWithLimit() = articleRepository.getArticles(3)
}