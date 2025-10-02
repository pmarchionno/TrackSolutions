package com.example.tracksolutions.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tipo_producto")
data class TipoProductoEntity(
    @PrimaryKey(autoGenerate = true) val idTipoProducto: Int = 0,
    val tipoProducto: String
)
