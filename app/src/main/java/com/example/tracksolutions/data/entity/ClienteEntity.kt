package com.example.tracksolutions.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cliente",
    foreignKeys = [
        ForeignKey(
            entity = PaisEntity::class,
            parentColumns = ["idPais"],
            childColumns = ["idPais"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("idPais")]
    // indices = [Index("idPais"), Index("email", unique = true)]
)
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val idCliente: Int = 0,
    val cliente: String,
    val email: String,
    val idPais: Int
)