package com.example.tracksolutions.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Upsert
import com.example.tracksolutions.data.entity.PaisEntity

@Dao
interface PaisDao {

    // ---------- INSERT / UPSERT ----------

    /** Inserta un país; falla si ya existe misma PK (id) y reemplaza si mismo nombre con UNIQUE si lo definiste así. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pais: PaisEntity): Long

    /** Inserta en lote; útil para pre-poblado. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<PaisEntity>): List<Long>

    /** Reemplaza o inserta (requiere Room 2.6+). */
    @Upsert
    suspend fun upsert(pais: PaisEntity): Long

    /** Upsert en lote. */
    @Upsert
    suspend fun upsertAll(items: List<PaisEntity>): List<Long>

    // ---------- UPDATE / DELETE ----------

    @Update
    suspend fun update(pais: PaisEntity): Int

    @Delete
    suspend fun delete(pais: PaisEntity): Int

    @Query("DELETE FROM pais WHERE idPais = :id")
    suspend fun deleteById(id: Int): Int

    // ---------- QUERIES ----------

    @Query("SELECT * FROM pais ORDER BY pais")
    suspend fun listar(): List<PaisEntity>

    @Query("SELECT * FROM pais WHERE idPais = :id LIMIT 1")
    suspend fun porId(id: Int): PaisEntity?

    @Query("SELECT * FROM pais WHERE pais LIKE '%' || :q || '%' ORDER BY pais")
    suspend fun buscar(q: String): List<PaisEntity>

    @Query("SELECT COUNT(*) FROM pais")
    suspend fun count(): Int
}
