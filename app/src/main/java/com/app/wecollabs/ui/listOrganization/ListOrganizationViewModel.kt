package com.app.wecollabs.ui.listOrganization

import androidx.lifecycle.ViewModel
import com.app.core.data.repository.OrganizationRepository

class ListOrganizationViewModel(
    private val organizationRepository: OrganizationRepository
): ViewModel() {

    fun getOrganizations(goalId: String) = organizationRepository.getOrganizations(goalId)

}