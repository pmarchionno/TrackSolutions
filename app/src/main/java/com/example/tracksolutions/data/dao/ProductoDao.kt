package com.example.tracksolutions.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.tracksolutions.data.entity.ProductoEntity
import com.example.tracksolutions.data.relations.ProductoConTipo

@Dao
interface ProductoDao {
    @Insert
    suspend fun insert(p: ProductoEntity): Long
    @Update
    suspend fun update(p: ProductoEntity): Int
    @Delete
    suspend fun delete(p: ProductoEntity): Int

    @Transaction
    @Query("SELECT * FROM producto ORDER BY producto")
    suspend fun listarConTipo(): List<ProductoConTipo>

    @Query("SELECT * FROM producto WHERE producto LIKE '%'||:q||'%' ORDER BY producto")
    suspend fun buscar(q: String): List<ProductoEntity>

    // Listado básico (ProductosScreen usa esto)
    @Query("SELECT * FROM producto ORDER BY producto")
    suspend fun listar(): List<ProductoEntity>
}