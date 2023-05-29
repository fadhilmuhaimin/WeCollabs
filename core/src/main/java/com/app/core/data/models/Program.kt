package com.app.core.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Program(
    var id: String = "",
    var title: String? = null,
    var imageUrl: String? = null,
    var date: String? = null,
    var description: String? = null,
    var address: String? = null,
    var addressCoordinate: Map<String, Double?>? = null,
    var contactNumber: String? = null,
    var viewerCounter: Long? = null,
    var organizationId: String? = null,
    var createdAt: String? = null
) : Parcelable {
    companion object {
        const val PROPERTY_TITLE = "title"
        const val PROPERTY_IMAGE = "imageUrl"
        const val PROPERTY_DATE = "date"
        const val PROPERTY_DESCRIPTION = "description"
        const val PROPERTY_ADDRESS = "address"
        const val PROPERTY_COORDINATE = "addressCoordinate"
        const val PROPERTY_CONTACT = "contactNumber"
    }
}