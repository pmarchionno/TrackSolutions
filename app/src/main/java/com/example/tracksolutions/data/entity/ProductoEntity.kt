package com.example.tracksolutions.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "producto",
    foreignKeys = [
        ForeignKey(
            entity = TipoProductoEntity::class,
            parentColumns = ["idTipoProducto"],
            childColumns = ["idTipoProducto"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("idTipoProducto"), Index("producto")]
)
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val idProducto: Int = 0,
    val producto: String,
    val precio: Double,
    val idTipoProducto: Int
)