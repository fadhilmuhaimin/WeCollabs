package com.app.core.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    var id: String = "",
    var title: String? = null,
    var imageUrl: String? = null,
    var eventDate: String? = null,
    var description: String? = null,
    var address: String? = null,
    var addressCoordinate: Map<String, Double?>? = null,
    var contactNumber: String? = null,
    var viewerCounter: Long? = null,
    var organizationId: String? = null,
    var organizationName: String? = null,
    var createdAt: String? = null,
    var isTrending: Boolean? = null,
    var isComingSoon: Boolean? = null
) : Parcelable {
    companion object {
        const val PROPERTY_TITLE = "title"
        const val PROPERTY_IMAGE = "imageUrl"
        const val PROPERTY_DATE = "eventDate"
        const val PROPERTY_DESCRIPTION = "description"
        const val PROPERTY_ADDRESS = "address"
        const val PROPERTY_COORDINATE = "addressCoordinate"
        const val PROPERTY_CONTACT = "contactNumber"
    }
}