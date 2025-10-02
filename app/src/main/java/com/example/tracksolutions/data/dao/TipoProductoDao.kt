package com.example.tracksolutions.data.dao

import androidx.room.*
import com.example.tracksolutions.data.entity.TipoProductoEntity

@Dao
interface TipoProductoDao {

    // ====== CREATE / UPSERT ======

    /** Inserta un tipo de producto. Devuelve el id autogenerado. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: TipoProductoEntity): Long

    /** Inserta varios. Devuelve ids (o -1 si IGNORE). Útil para pre-poblado. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<TipoProductoEntity>): List<Long>

    /** Upsert requiere Room 2.6+ (reemplaza si existe por PK). */
    @Upsert
    suspend fun upsert(item: TipoProductoEntity): Long

    @Upsert
    suspend fun upsertAll(items: List<TipoProductoEntity>): List<Long>

    // ====== READ ======

    @Query("SELECT * FROM tipo_producto ORDER BY tipoProducto")
    suspend fun listar(): List<TipoProductoEntity>

    @Query("SELECT * FROM tipo_producto WHERE idTipoProducto = :id LIMIT 1")
    suspend fun porId(id: Int): TipoProductoEntity?

    @Query("SELECT * FROM tipo_producto WHERE tipoProducto LIKE '%' || :q || '%' ORDER BY tipoProducto")
    suspend fun buscar(q: String): List<TipoProductoEntity>

    @Query("SELECT COUNT(*) FROM tipo_producto")
    suspend fun count(): Int

    // ====== UPDATE / DELETE ======

    @Update
    suspend fun update(item: TipoProductoEntity): Int

    @Delete
    suspend fun delete(item: TipoProductoEntity): Int

    @Query("DELETE FROM tipo_producto WHERE idTipoProducto = :id")
    suspend fun deleteById(id: Int): Int
}
