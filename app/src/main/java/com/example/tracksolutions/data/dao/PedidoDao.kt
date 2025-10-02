package com.example.tracksolutions.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.tracksolutions.data.entity.PedidoEntity
import com.example.tracksolutions.data.dto.TopCliente
import com.example.tracksolutions.data.dto.TopProducto
import com.example.tracksolutions.data.entity.DetallePedidoEntity
import com.example.tracksolutions.data.relations.PedidoConTodo

@Dao
interface PedidoDao {
    @Insert
    suspend fun insertPedido(p: PedidoEntity): Long
    @Insert suspend fun insertDetalles(d: Array<DetallePedidoEntity>): List<Long>

    @Transaction
    @Query("""
        SELECT * FROM pedido 
        WHERE (:desde IS NULL OR fechaPedido >= :desde)
          AND (:hasta IS NULL OR fechaPedido < :hasta)
        ORDER BY fechaPedido DESC
    """)
    suspend fun listarPedidosConTodo(desde: Long?, hasta: Long?): List<PedidoConTodo>

    // KPIs simples
    @Query("""
        SELECT pr.producto as nombre, SUM(dp.cantidad) as unidades
        FROM detalle_pedido dp 
        JOIN producto pr ON pr.idProducto = dp.idProducto
        GROUP BY pr.idProducto ORDER BY unidades DESC LIMIT 10
    """)
    suspend fun topProductosPorUnidades(): List<TopProducto>

    @Query("""
        SELECT c.cliente as nombre, SUM(dp.importeVenta) as total
        FROM pedido p 
        JOIN cliente c ON c.idCliente = p.idCliente
        JOIN detalle_pedido dp ON dp.idPedido = p.idPedido
        GROUP BY c.idCliente ORDER BY total DESC LIMIT 10
    """)
    suspend fun topClientesPorFacturacion(): List<TopCliente>
}
