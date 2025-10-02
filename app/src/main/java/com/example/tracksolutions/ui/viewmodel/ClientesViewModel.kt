package com.example.tracksolutions.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracksolutions.data.AppDb
import com.example.tracksolutions.data.dao.ClienteDao
import com.example.tracksolutions.data.dao.CatalogoDao
import com.example.tracksolutions.data.entity.ClienteEntity
import com.example.tracksolutions.data.entity.PaisEntity
import com.example.tracksolutions.data.entity.ZonaEntity
import com.example.tracksolutions.data.relations.ClienteConPaisZona
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientesViewModel(app: Application) : AndroidViewModel(app) {

    private val clienteDao: ClienteDao by lazy { AppDb.get(app).clienteDao() }
    private val catalogoDao: CatalogoDao by lazy { AppDb.get(app).catalogoDao() }

    // Estado expuesto a la UI
    private val _clientes = MutableStateFlow<List<ClienteConPaisZona>>(emptyList())
    val clientes: StateFlow<List<ClienteConPaisZona>> = _clientes

    private val _zonas = MutableStateFlow<List<ZonaEntity>>(emptyList())
    val zonas: StateFlow<List<ZonaEntity>> = _zonas

    private val _paises = MutableStateFlow<List<PaisEntity>>(emptyList())
    val paises: StateFlow<List<PaisEntity>> = _paises

    fun refreshClientes(q: String? = null) = viewModelScope.launch {
        _clientes.value = clienteDao.listarConPaisZona(q)
    }

    fun refreshCatalogos() = viewModelScope.launch {
        _zonas.value = catalogoDao.zonas()
        _paises.value = catalogoDao.paises()
    }

    fun crearCliente(nombre: String, email: String, idPais: Int) = viewModelScope.launch {
        // (simple) validación: podés mejorarla según tus reglas
        if (nombre.isNotBlank() && email.isNotBlank()) {
            clienteDao.insert(ClienteEntity(cliente = nombre.trim(), email = email.trim(), idPais = idPais))
            refreshClientes()
        }
    }

    fun actualizarCliente(c: ClienteEntity) = viewModelScope.launch {
        clienteDao.update(c)
        refreshClientes()
    }

    fun borrarCliente(c: ClienteEntity) = viewModelScope.launch {
        clienteDao.delete(c)
        refreshClientes()
    }
}
