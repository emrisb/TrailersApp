package com.an.trailers.data.remote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val id: String,
    val key: String
) : Parcelable
