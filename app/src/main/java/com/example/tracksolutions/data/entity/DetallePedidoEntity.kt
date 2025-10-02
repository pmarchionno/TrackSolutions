package com.example.tracksolutions.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
@Entity(
    tableName = "detalle_pedido",
    foreignKeys = [
        ForeignKey(
            entity = PedidoEntity::class,
            parentColumns = ["idPedido"],
            childColumns = ["idPedido"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["idProducto"],
            childColumns = ["idProducto"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("idPedido"), Index("idProducto")]
)
data class DetallePedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val idDetallePedido: Int = 0,   // PK autoincremental

    val idPedido: Int,              // FK a Pedido
    val idProducto: Int,            // FK a Producto
    val cantidad: Int,
    @ColumnInfo(name = "importeVenta")
    val importeVenta: Double        // total de la línea
)
