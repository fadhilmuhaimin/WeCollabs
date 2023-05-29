package com.app.core.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    var id: String? = null,
    var title: String? = null,
    var imageUrl: String? = null,
    var content: String? = null,
    var publishedAt: String? = null,
    var publisherName: String? = null,
    var source: String? = null
) : Parcelable