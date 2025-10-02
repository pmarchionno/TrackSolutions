package com.example.tracksolutions.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Upsert
import com.example.tracksolutions.data.entity.ZonaEntity

@Dao
interface ZonaDao {

    // ---------- INSERT / UPSERT ----------

    /** Inserta un zona; falla si ya existe misma PK (id) y reemplaza si mismo nombre con UNIQUE si lo definiste así. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(zona: ZonaEntity): Long

    /** Inserta en lote; útil para pre-poblado. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<ZonaEntity>): List<Long>

    /** Reemplaza o inserta (requiere Room 2.6+). */
    @Upsert
    suspend fun upsert(zona: ZonaEntity): Long

    /** Upsert en lote. */
    @Upsert
    suspend fun upsertAll(items: List<ZonaEntity>): List<Long>

    // ---------- UPDATE / DELETE ----------

    @Update
    suspend fun update(zona: ZonaEntity): Int

    @Delete
    suspend fun delete(zona: ZonaEntity): Int

    @Query("DELETE FROM zona WHERE idZona = :id")
    suspend fun deleteById(id: Int): Int

    // ---------- QUERIES ----------

    @Query("SELECT * FROM zona ORDER BY zona")
    suspend fun listar(): List<ZonaEntity>

    @Query("SELECT * FROM zona WHERE idZona = :id LIMIT 1")
    suspend fun porId(id: Int): ZonaEntity?

    @Query("SELECT * FROM zona WHERE zona LIKE '%' || :q || '%' ORDER BY zona")
    suspend fun buscar(q: String): List<ZonaEntity>

    @Query("SELECT COUNT(*) FROM zona")
    suspend fun count(): Int
}
