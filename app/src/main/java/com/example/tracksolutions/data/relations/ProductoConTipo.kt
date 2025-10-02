package com.example.tracksolutions.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tracksolutions.data.entity.ProductoEntity
import com.example.tracksolutions.data.entity.TipoProductoEntity

data class ProductoConTipo(
    @Embedded val producto: ProductoEntity,
    @Relation(
        parentColumn = "idTipoProducto",
        entityColumn = "idTipoProducto"
    )
    val tipo: TipoProductoEntity
)