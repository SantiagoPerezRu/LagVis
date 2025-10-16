package com.example.lagvis_v1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comunidades")
data class ComunidadEntity(
    @PrimaryKey val id: String,
    val nombre: String
)

@Entity(tableName = "sectores")
data class SectorEntity(
    @PrimaryKey val id: String,
    val nombre: String
)