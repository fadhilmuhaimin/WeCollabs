package com.app.wecollabs.ui.start.fragments.login

import androidx.lifecycle.ViewModel
import com.app.core.data.USER
import com.app.core.data.repository.OrganizationRepository
import com.app.core.data.repository.UserRepository

class LoginViewModel(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository
    ): ViewModel() {

    fun loginUser(email: String, password: String) = userRepository.login(email, password)

    fun loginOrganization(email: String, password: String) = organizationRepository.login(email, password)

}