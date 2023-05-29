package com.app.wecollabs.ui.listGoal

import androidx.lifecycle.ViewModel
import com.app.core.data.repository.GoalRepository

class ListGoalViewModel(private val goalRepository: GoalRepository): ViewModel() {
    fun getGoals() = goalRepository.getAllGoals()
}