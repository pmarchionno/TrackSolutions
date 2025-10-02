package com.example.tracksolutions.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tracksolutions.data.entity.CanalVentaEntity
import com.example.tracksolutions.data.entity.PaisEntity
import com.example.tracksolutions.data.entity.TipoProductoEntity
import com.example.tracksolutions.data.entity.ZonaEntity

@Dao
interface CatalogoDao {
    @Insert
    suspend fun insertZona(z: ZonaEntity): Long
    @Insert suspend fun insertPais(p: PaisEntity): Long
    @Insert suspend fun insertTipoProducto(tp: TipoProductoEntity): Long
    @Insert suspend fun insertCanal(c: CanalVentaEntity): Long

    @Query("SELECT * FROM zona ORDER BY zona") suspend fun zonas(): List<ZonaEntity>
    @Query("SELECT * FROM pais ORDER BY pais") suspend fun paises(): List<PaisEntity>
    @Query("SELECT * FROM tipo_producto ORDER BY tipoProducto") suspend fun tipos(): List<TipoProductoEntity>
    @Query("SELECT * FROM canal_venta ORDER BY canalVenta") suspend fun canales(): List<CanalVentaEntity>
}
