// data/local/LagVisDb.kt
package com.example.lagvis_v1.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lagvis_v1.data.local.dao.LookupDao
import com.example.lagvis_v1.data.local.entity.ComunidadEntity
import com.example.lagvis_v1.data.local.entity.SectorEntity

@Database(
    entities = [ComunidadEntity::class, SectorEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LagVisDb : RoomDatabase() {
    abstract fun lookupDao(): LookupDao
}
