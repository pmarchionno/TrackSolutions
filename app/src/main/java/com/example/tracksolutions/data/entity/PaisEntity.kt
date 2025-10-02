package com.example.tracksolutions.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pais",
    foreignKeys = [
        ForeignKey(
            entity = ZonaEntity::class,
            parentColumns = ["idZona"],
            childColumns = ["idZona"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("idZona")]
)
data class PaisEntity(
    @PrimaryKey(autoGenerate = true) val idPais: Int = 0,
    val pais: String,
    val idZona: Int
)
