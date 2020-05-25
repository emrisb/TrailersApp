package com.an.trailers.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Crew(
    val id: Long,
    @SerializedName("credit_id")
    val creditId: String,
    var name: String?,
    @SerializedName("profile_path")
    var profilePath: String?,
    val job: String?,
    val department: String
) : Parcelable