package com.app.core.data.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventEntity(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "imageUrl")
    var imageUrl: String? = null,

    @ColumnInfo(name = "date")
    var date: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "address")
    var address: String? = null,

    @ColumnInfo(name = "latitude")
    var latitude: Double? = null,

    @ColumnInfo(name = "longitude")
    var longitude: Double? = null,

    @ColumnInfo(name = "contactNumber")
    var contactNumber: String? = null,

    @ColumnInfo(name = "organizationId")
    var organizationId: String? = null,

    @ColumnInfo(name = "organizationName")
    var organizationName: String? = null,

    @ColumnInfo(name = "isTrending")
    var isTrending: Boolean? = null,

    @ColumnInfo(name = "isComingSoon")
    var isComingSoon: Boolean? = null
)