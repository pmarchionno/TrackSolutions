package com.example.tracksolutions.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracksolutions.data.AppDb
import com.example.tracksolutions.data.dao.PedidoDao
import com.example.tracksolutions.data.dto.TopCliente
import com.example.tracksolutions.data.dto.TopProducto
import com.example.tracksolutions.data.relations.PedidoConTodo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportesViewModel(app: Application) : AndroidViewModel(app) {

    private val pedidoDao: PedidoDao by lazy { AppDb.get(app).pedidoDao() }

    private val _topProductos = MutableStateFlow<List<TopProducto>>(emptyList())
    val topProductos: StateFlow<List<TopProducto>> = _topProductos

    private val _topClientes = MutableStateFlow<List<TopCliente>>(emptyList())
    val topClientes: StateFlow<List<TopCliente>> = _topClientes

    private val _pedidos = MutableStateFlow<List<PedidoConTodo>>(emptyList())
    val pedidos: StateFlow<List<PedidoConTodo>> = _pedidos

    /** KPIs sin filtro de fecha (usa las @Query del DAO) */
    fun refreshKPIs() = viewModelScope.launch {
        _topProductos.value = pedidoDao.topProductosPorUnidades()
        _topClientes.value = pedidoDao.topClientesPorFacturacion()
    }

    /** Listado de pedidos con rango opcional (epoch millis) */
    fun refreshPedidos(desde: Long? = null, hasta: Long? = null) = viewModelScope.launch {
        _pedidos.value = pedidoDao.listarPedidosConTodo(desde, hasta)
    }
}
