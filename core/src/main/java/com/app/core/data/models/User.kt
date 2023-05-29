package com.app.core.data.models

data class User(
    var id: String? = null,
    var name: String? = null,
    var gender: Int? = null,
    var dateOfBirth: String? = null,
    var phoneNumber: String? = null,
    var email: String? = null,
    var imageUrl: String? = null,
) {
    companion object {
        var currentUser: User? = null
    }
}