package com.app.wecollabs.ui.start.fragments.register.forUser

import androidx.lifecycle.ViewModel
import com.app.core.data.models.User
import com.app.core.data.repository.UserRepository
import java.io.File

class RegisterUserViewModel(private val userRepository: UserRepository) : ViewModel() {

    var selectedImageFile: File? = null
    var selectedGender: Int? = null
    var selectedDateOfBirth: String? = null
    var username = ""
    var phoneNumber = ""
    var email = ""
    var password = ""
    var confirmPassword = ""

    fun register(user: User) = userRepository.register(email, password, selectedImageFile, user)

}