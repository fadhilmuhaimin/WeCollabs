package com.app.wecollabs.ui.start.fragments.register.forOrganization

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.core.data.models.Goal
import com.app.core.data.models.Organization
import com.app.core.data.repository.GoalRepository
import com.app.core.data.repository.OrganizationRepository
import java.io.File

class ChooseGoalViewModel(
    private val organizationRepository: OrganizationRepository,
    private val goalRepository: GoalRepository
): ViewModel() {

    val selectedGoal = MutableLiveData<List<Goal>>()
    lateinit var chooseGoalArgument: ChooseGoalArgument

    fun getAllGoals() = goalRepository.getAllGoals()

    fun register(email: String, password: String, imageFile: File?, organization: Organization) = organizationRepository.register(email, password, imageFile, organization)
}