package com.app.core.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Goal(
    var id: String? = null,
    var name: Map<String, String>? = null,
    var iconUrl: String? = null
) : Parcelable