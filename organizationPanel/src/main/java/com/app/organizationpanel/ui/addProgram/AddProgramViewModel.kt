package com.app.organizationpanel.ui.addProgram

import androidx.lifecycle.ViewModel
import com.app.core.data.models.Program
import com.app.core.data.repository.ProgramRepository
import java.io.File

class AddProgramViewModel(private val programRepository: ProgramRepository): ViewModel() {

    var selectedProgram: Program? = null

    var imageFile: File? = null
    var title = ""
    var desc = ""
    var contactNumber = ""
    var date: String? = null
    var address: String? = null
    var addressCoordinate: Map<String, Double?>? = null

    fun addProgram(program: Program, image: File) = programRepository.addProgram(program, image)

    fun updateProgram(program: Program, image: File?) = programRepository.updateProgram(program, image)

    fun deleteProgram(programId: String) = programRepository.deleteProgram(programId)

}