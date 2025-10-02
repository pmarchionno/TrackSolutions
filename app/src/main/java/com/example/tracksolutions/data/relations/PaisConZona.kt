package com.example.tracksolutions.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tracksolutions.data.entity.PaisEntity
import com.example.tracksolutions.data.entity.ZonaEntity

data class PaisConZona(
    @Embedded val pais: PaisEntity,
    @Relation(
        parentColumn = "idZona",
        entityColumn = "idZona"
    )
    val zona: ZonaEntity
)
