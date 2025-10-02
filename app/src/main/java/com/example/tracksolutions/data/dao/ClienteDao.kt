package com.example.tracksolutions.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.tracksolutions.data.entity.ClienteEntity
import com.example.tracksolutions.data.entity.ZonaEntity
import com.example.tracksolutions.data.relations.ClienteConPaisZona

@Dao
interface ClienteDao {
    @Insert
    suspend fun insert(c: ClienteEntity): Long
    @Update
    suspend fun update(c: ClienteEntity): Int
    @Delete
    suspend fun delete(c: ClienteEntity): Int

    @Transaction
    @Query("""
        SELECT * FROM cliente 
        WHERE (:q IS NULL OR cliente LIKE '%'||:q||'%' OR email LIKE '%'||:q||'%')
        ORDER BY cliente
    """)
    suspend fun listarConPaisZona(q: String?): List<ClienteConPaisZona>

    @Query("SELECT * FROM cliente ORDER BY cliente")
    suspend fun listar(): List<ClienteEntity>
}