package com.app.wecollabs.ui.organizationDetail

import androidx.lifecycle.ViewModel
import com.app.core.data.repository.ProgramRepository

class OrganizationDetailViewModel(private val programRepository: ProgramRepository): ViewModel() {
    fun getPrograms(organizationId: String) = programRepository.getPrograms(organizationId)
}