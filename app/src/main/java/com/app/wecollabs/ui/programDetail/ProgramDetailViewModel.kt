package com.app.wecollabs.ui.programDetail

import androidx.lifecycle.ViewModel
import com.app.core.data.repository.OrganizationRepository

class ProgramDetailViewModel(private val organizationRepository: OrganizationRepository): ViewModel() {
    fun getOrganization(id: String) = organizationRepository.getOrganization(id)
}