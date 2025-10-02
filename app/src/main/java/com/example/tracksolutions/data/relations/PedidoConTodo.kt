package com.example.tracksolutions.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tracksolutions.data.entity.ClienteEntity
import com.example.tracksolutions.data.entity.DetallePedidoEntity
import com.example.tracksolutions.data.entity.PedidoEntity

data class PedidoConTodo(
    @Embedded val pedido: PedidoEntity,
    @Relation(
        parentColumn = "idCliente",
        entityColumn = "idCliente"
    )
    val cliente: ClienteEntity,
    @Relation(
        parentColumn = "idPedido",
        entityColumn = "idPedido",
        entity = DetallePedidoEntity::class
    )
    val detalles: List<DetalleConProducto>
)