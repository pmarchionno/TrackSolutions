package com.example.tracksolutions.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zona")
data class ZonaEntity(
    @PrimaryKey(autoGenerate = true) val idZona: Int = 0,
    val zona: String
)
