package com.example.tracksolutions.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracksolutions.data.AppDb
import com.example.tracksolutions.data.dao.CatalogoDao
import com.example.tracksolutions.data.dao.PaisDao
import com.example.tracksolutions.data.entity.PaisEntity
import com.example.tracksolutions.data.entity.ZonaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaisesViewModel(app: Application) : AndroidViewModel(app) {

    private val paisDao: PaisDao by lazy { AppDb.get(app).paisDao() }
    private val catalogoDao: CatalogoDao by lazy { AppDb.get(app).catalogoDao() }

    private val _paises = MutableStateFlow<List<PaisEntity>>(emptyList())
    val paises: StateFlow<List<PaisEntity>> = _paises

    private val _zonas = MutableStateFlow<List<ZonaEntity>>(emptyList())
    val zonas: StateFlow<List<ZonaEntity>> = _zonas

    /** Carga/recarga listado de países. */
    fun refreshPaises() = viewModelScope.launch {
        _paises.value = paisDao.listar()
    }

    /** Carga/recarga catálogo de zonas (para el selector). */
    fun refreshZonas() = viewModelScope.launch {
        _zonas.value = catalogoDao.zonas()
    }

    /** Alta de país. Pasa el idZona seleccionado desde la UI. */
    fun crearPais(nombre: String, idZona: Int) = viewModelScope.launch {
        paisDao.insert(PaisEntity(pais = nombre.trim(), idZona = idZona))
        refreshPaises()
    }

    /** Borrado de país. */
    fun borrarPais(p: PaisEntity) = viewModelScope.launch {
        paisDao.delete(p)
        refreshPaises()
    }
}
