package com.app.organizationpanel.ui.organizationPanel.ui.program

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.core.data.repository.ProgramRepository

class ProgramViewModel(private val programRepository: ProgramRepository) : ViewModel() {
    fun getPrograms(organizationId: String) = programRepository.getPrograms(organizationId)
}