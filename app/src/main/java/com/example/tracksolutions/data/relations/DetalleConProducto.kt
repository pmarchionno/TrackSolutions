package com.example.tracksolutions.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tracksolutions.data.entity.DetallePedidoEntity
import com.example.tracksolutions.data.entity.ProductoEntity

data class DetalleConProducto(
    @Embedded val detalle: DetallePedidoEntity,
    @Relation(
        parentColumn = "idProducto",
        entityColumn = "idProducto"
    )
    val producto: ProductoEntity
)