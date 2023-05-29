package com.app.wecollabs.ui.start.fragments.register.forOrganization

import android.os.Parcelable
import com.app.core.data.models.Organization
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class ChooseGoalArgument(
    var organization: Organization,
    var imageFile: File?,
    var email: String,
    var password: String
) : Parcelable