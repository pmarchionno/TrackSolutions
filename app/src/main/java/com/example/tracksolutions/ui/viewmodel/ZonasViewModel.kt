package com.example.tracksolutions.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracksolutions.data.AppDb
import com.example.tracksolutions.data.dao.ZonaDao
import com.example.tracksolutions.data.entity.ZonaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ZonasViewModel(app: Application) : AndroidViewModel(app) {

    private val zonaDao: ZonaDao by lazy { AppDb.get(app).zonaDao() }

    private val _zonas = MutableStateFlow<List<ZonaEntity>>(emptyList())
    val zonas: StateFlow<List<ZonaEntity>> = _zonas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Carga/recarga todas las zonas. */
    fun refreshZonas() = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            _zonas.value = zonaDao.listar()
        } catch (t: Throwable) {
            _error.value = t.message
        } finally {
            _isLoading.value = false
        }
    }

    /** Alta de zona. */
    fun crearZona(nombre: String) = viewModelScope.launch {
        val n = nombre.trim()
        if (n.isEmpty()) return@launch
        try {
            zonaDao.insert(ZonaEntity(zona = n))
            refreshZonas()
        } catch (t: Throwable) {
            _error.value = t.message
        }
    }

    /** Borrado de zona. */
    fun borrarZona(z: ZonaEntity) = viewModelScope.launch {
        try {
            zonaDao.delete(z)
            refreshZonas()
        } catch (t: Throwable) {
            _error.value = t.message
        }
    }

    /** (Opcional) Actualización de zona. */
    fun actualizarZona(z: ZonaEntity) = viewModelScope.launch {
        try {
            zonaDao.update(z)
            refreshZonas()
        } catch (t: Throwable) {
            _error.value = t.message
        }
    }

    /** (Opcional) Búsqueda simple por nombre. */
    fun buscarZonas(q: String) = viewModelScope.launch {
        val term = q.trim()
        if (term.isEmpty()) {
            refreshZonas()
        } else {
            try {
                _zonas.value = zonaDao.buscar(term)
            } catch (t: Throwable) {
                _error.value = t.message
            }
        }
    }
}
