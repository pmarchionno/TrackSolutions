package com.example.tracksolutions.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

    // Marcar cambios locales
    @Query("UPDATE cliente SET pendingSync = 1, updatedAt = :ts WHERE idCliente = :id")
    suspend fun marcarPendiente(id: Int, ts: Long)

    // Pendientes de subir
    @Query("SELECT * FROM cliente WHERE pendingSync = 1")
    suspend fun pendientes(): List<ClienteEntity>

    // Buscar por remoteId
    @Query("SELECT * FROM cliente WHERE remoteId = :rid LIMIT 1")
    suspend fun porRemoteId(rid: String): ClienteEntity?

    // Upsert manual por remoteId (resolución de pull)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: ClienteEntity)
}