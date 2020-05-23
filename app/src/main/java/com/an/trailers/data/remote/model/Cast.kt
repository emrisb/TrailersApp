package com.an.trailers.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cast(
    val id: Long,
    @SerializedName("cast_id")
    val castId: Long,
    var character: String?,
    @SerializedName("credit_id")
    val creditId: String,
    val name: String?,
    @SerializedName("profile_path")
    var profilePath: String?,
    val order: Int
) : Parcelable
