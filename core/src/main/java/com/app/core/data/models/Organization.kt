package com.app.core.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Organization(
    var id: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null,
    var description: String? = null,
    var address: String? = null,
    var addressCoordinate: Map<String, Double>? = null,
    var email: String? = null,
    var goalsId: List<String>? = null,
    var imageUrl: String? = null,
) : Parcelable {
    companion object {
        var currentOrganization: Organization? = null
    }
}