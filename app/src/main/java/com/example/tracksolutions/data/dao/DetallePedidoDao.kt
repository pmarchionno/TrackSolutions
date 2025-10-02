package com.example.tracksolutions.data.dao

import androidx.room.*
import com.example.tracksolutions.data.entity.DetallePedidoEntity

@Dao
interface DetallePedidoDao {

    // === CREATE ===
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(d: DetallePedidoEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(items: List<DetallePedidoEntity>): List<Long>

    // === READ ===
    @Query("SELECT * FROM detalle_pedido ORDER BY idPedido, idDetallePedido")
    suspend fun listar(): List<DetallePedidoEntity>

    @Query("SELECT * FROM detalle_pedido WHERE idPedido = :idPedido ORDER BY idDetallePedido")
    suspend fun porPedido(idPedido: Int): List<DetallePedidoEntity>

    @Query("SELECT COUNT(*) FROM detalle_pedido WHERE idPedido = :idPedido")
    suspend fun contarPorPedido(idPedido: Int): Int

    // === UPDATE ===
    @Update
    suspend fun update(d: DetallePedidoEntity): Int

    // === DELETE ===
    @Delete
    suspend fun delete(d: DetallePedidoEntity): Int

    @Query("DELETE FROM detalle_pedido WHERE idPedido = :idPedido")
    suspend fun deleteByPedido(idPedido: Int): Int
}
