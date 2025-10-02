package com.example.tracksolutions.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "canal_venta")
data class CanalVentaEntity(
    @PrimaryKey(autoGenerate = true) val idCanalVenta: Int = 0,
    val canalVenta: String
)