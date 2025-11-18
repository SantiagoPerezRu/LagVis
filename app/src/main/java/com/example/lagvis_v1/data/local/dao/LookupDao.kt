package com.example.lagvis_v1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lagvis_v1.data.local.entity.ComunidadEntity
import com.example.lagvis_v1.data.local.entity.SectorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LookupDao {
    // Comunidades
    @Query("SELECT * FROM comunidades ORDER BY nombre")
    fun observeComunidades(): Flow<List<ComunidadEntity>>

    @Query("SELECT * FROM comunidades ORDER BY nombre")
    suspend fun getComunidades(): List<ComunidadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertComunidades(items: List<ComunidadEntity>)

    @Query("DELETE FROM comunidades")
    suspend fun clearComunidades()

    // Sectores
    @Query("SELECT * FROM sectores ORDER BY nombre")
    fun observeSectores(): Flow<List<SectorEntity>>

    @Query("SELECT * FROM sectores ORDER BY nombre")
    suspend fun getSectores(): List<SectorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSectores(items: List<SectorEntity>)

    @Query("DELETE FROM sectores")
    suspend fun clearSectores()
}
