package com.an.trailers.data.remote.model

import android.os.Parcelable
import androidx.room.TypeConverters
import com.an.trailers.data.local.converter.CastListTypeConverter
import com.an.trailers.data.local.converter.CrewListTypeConverter
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class CreditResponse(
    @TypeConverters(CrewListTypeConverter::class)
    var crew: List<Crew> = ArrayList(),
    @TypeConverters(CastListTypeConverter::class)
    var cast: List<Cast> = ArrayList()
) : Parcelable
