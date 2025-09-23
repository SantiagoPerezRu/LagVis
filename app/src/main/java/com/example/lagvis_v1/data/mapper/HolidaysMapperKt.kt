// data/mapper/HolidaysMapperKt.kt
package com.example.lagvis_v1.data.mapper

import com.example.lagvis_v1.data.remote.dto.holiday.HolidayDtoKt
import com.example.lagvis_v1.data.remote.dto.holiday.HolidaysResponseKt
import com.example.lagvis_v1.dominio.model.PublicHolidayKt

fun HolidaysResponseKt.toDomain(): List<PublicHolidayKt> =
    (holidays ?: emptyList()).map { it.toDomain(display) }

private fun HolidayDtoKt.toDomain(fallbackProvince: String?): PublicHolidayKt =
    PublicHolidayKt(
        date = date.orEmpty(),
        name = name ?: localName ?: "Festivo",
        scope = scope,
        province = (province ?: fallbackProvince).orEmpty(),
        autonomy = autonomy,
        localName = localName ?: name
    )
