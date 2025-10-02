package com.example.tracksolutions.ui.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracksolutions.data.AppDb
import com.example.tracksolutions.data.entity.DetallePedidoEntity
import com.example.tracksolutions.data.entity.PedidoEntity
import com.example.tracksolutions.data.relations.PedidoConTodo
import com.example.tracksolutions.data.dao.*
import com.example.tracksolutions.data.entity.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

data class LineaTemp(val producto: ProductoEntity, val cantidad: Int)

class PedidosViewModel(app: Application) : AndroidViewModel(app) {

    private val pedidoDao: PedidoDao by lazy { AppDb.get(app).pedidoDao() }
    private val clienteDao: ClienteDao by lazy { AppDb.get(app).clienteDao() }
    private val productoDao: ProductoDao by lazy { AppDb.get(app).productoDao() }
    private val catalogoDao: CatalogoDao by lazy { AppDb.get(app).catalogoDao() }
    private val detalleDao: DetallePedidoDao by lazy { AppDb.get(app).detallePedidoDao() }

    // 👇 Estos son los que consumís en la UI:
    private val _clientes = MutableStateFlow<List<ClienteEntity>>(emptyList())
    val clientes: StateFlow<List<ClienteEntity>> = _clientes

    private val _productos = MutableStateFlow<List<ProductoEntity>>(emptyList())
    val productos: StateFlow<List<ProductoEntity>> = _productos

    private val _canales = MutableStateFlow<List<CanalVentaEntity>>(emptyList())
    val canales: StateFlow<List<CanalVentaEntity>> = _canales

    private val _lineas = MutableStateFlow<List<LineaTemp>>(emptyList())
    val lineas: StateFlow<List<LineaTemp>> = _lineas

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    fun cargarCatalogos() = viewModelScope.launch {
        _clientes.value = clienteDao.listar()
        _productos.value = productoDao.listar()
        _canales.value = catalogoDao.canales()
    }

    fun nuevoPedido() {
        _lineas.value = emptyList()
        _total.value = 0.0
    }

    fun agregarLinea(prod: ProductoEntity, cantidad: Int) {
        val nuevas = _lineas.value.toMutableList()
        val i = nuevas.indexOfFirst { it.producto.idProducto == prod.idProducto }
        if (i >= 0) nuevas[i] = nuevas[i].copy(cantidad = nuevas[i].cantidad + cantidad)
        else nuevas += LineaTemp(prod, cantidad)
        _lineas.value = nuevas
        recomputarTotal()
    }

    fun quitarLinea(index: Int) {
        val nuevas = _lineas.value.toMutableList()
        if (index in nuevas.indices) {
            nuevas.removeAt(index)
            _lineas.value = nuevas
            recomputarTotal()
        }
    }

    private fun recomputarTotal() {
        _total.value = _lineas.value.sumOf { it.cantidad * it.producto.precio }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun guardarPedido(fPedido: String, fEnvio: String?, idCliente: Int, idCanal: Int): Boolean {
        if (_lineas.value.isEmpty()) return false
        val fechaPedidoMs = parseDate(fPedido) ?: return false
        val fechaEnvioMs  = fEnvio?.let { parseDate(it) }

        viewModelScope.launch {
            val idPedido = pedidoDao.insertPedido(
                PedidoEntity(
                    fechaPedido = fechaPedidoMs,
                    fechaEnvio = fechaEnvioMs,
                    idCliente = idCliente,
                    idCanalVenta = idCanal
                )
            ).toInt()
            _lineas.value.forEach { li ->
                detalleDao.insert(
                    DetallePedidoEntity(
                        idDetallePedido = 0,
                        idPedido = idPedido,
                        idProducto = li.producto.idProducto,
                        cantidad = li.cantidad,
                        importeVenta = li.cantidad * li.producto.precio
                    )
                )
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDate(txt: String) = try {
        LocalDate.parse(txt.trim()).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    } catch (_: Throwable) { null }
}


//class PedidosViewModel(app: Application) : AndroidViewModel(app) {
//    private val dao = AppDb.get(app).pedidoDao()
//    private val _items = MutableStateFlow<List<PedidoConTodo>>(emptyList())
//    val items: StateFlow<List<PedidoConTodo>> = _items
//
//    fun refresh(desde: Long? = null, hasta: Long? = null) = viewModelScope.launch {
//        _items.value = dao.listarPedidosConTodo(desde, hasta)
//    }
//
//    fun crearPedido(
//        fechaPedido: Long,
//        idCliente: Int,
//        idCanal: Int,
//        detalles: List<DetallePedidoEntity>
//    ) = viewModelScope.launch {
//        val id = dao.insertPedido(
//            PedidoEntity(
//                fechaPedido = fechaPedido,
//                fechaEnvio = null,
//                idCliente = idCliente,
//                idCanalVenta = idCanal
//            )
//        ).toInt()
//        dao.insertDetalles(detalles.map { it.copy(idPedido = id) }.toTypedArray())
//        refresh()
//    }
//}