package com.example.tracksolutions.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pedido",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["idCliente"],
            childColumns = ["idCliente"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CanalVentaEntity::class,
            parentColumns = ["idCanalVenta"],
            childColumns = ["idCanalVenta"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("idCliente"), Index("idCanalVenta"), Index("fechaPedido")]
)
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val idPedido: Int = 0,
    val fechaPedido: Long,   // epoch millis
    val fechaEnvio: Long?,   // puede ser null si no se envió
    val idCliente: Int,
    val idCanalVenta: Int
)