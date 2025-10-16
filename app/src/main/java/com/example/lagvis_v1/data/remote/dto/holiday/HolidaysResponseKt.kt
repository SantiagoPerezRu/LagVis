// data/remote/dto/holiday/HolidaysResponseKt.kt
package com.example.lagvis_v1.data.remote.dto.holiday

import com.google.gson.annotations.SerializedName

data class HolidaysResponseKt(
    @SerializedName("year")    val year: Int? = null,
    @SerializedName("slug")    val slug: String? = null,
    @SerializedName("display") val display: String? = null,
    @SerializedName("scope")   val scope: String? = null,
    @SerializedName("holidays") val holidays: List<HolidayDtoKt>? = null
)

data class HolidayDtoKt(
    val date: String? = null,
    val name: String? = null,
    val isRegional: Boolean? = null,
    val scope: String? = null,
    val province: String? = null,
    val autonomy: String? = null,
    val localName: String? = null
)
