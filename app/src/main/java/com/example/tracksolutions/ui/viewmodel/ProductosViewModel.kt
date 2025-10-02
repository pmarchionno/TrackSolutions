package com.example.tracksolutions.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracksolutions.data.AppDb
import com.example.tracksolutions.data.dao.ProductoDao
import com.example.tracksolutions.data.dao.TipoProductoDao
import com.example.tracksolutions.data.entity.ProductoEntity
import com.example.tracksolutions.data.entity.TipoProductoEntity
import com.example.tracksolutions.data.relations.ProductoConTipo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//class ProductosViewModel(app: Application) : AndroidViewModel(app) {
//    private val dao = AppDb.get(app).productoDao()
//    private val _items = MutableStateFlow<List<ProductoConTipo>>(emptyList())
//    val items: StateFlow<List<ProductoConTipo>> = _items
//
//    fun refresh(q: String? = null) = viewModelScope.launch {
//        _items.value = if (q.isNullOrBlank()) dao.listarConTipo() else
//            dao.buscar(q!!).map { ProductoConTipo(it, /* necesitás resolver tipo: */
//                TipoProductoEntity(it.idTipoProducto, "")
//            ) }
//        // Para búsqueda con tipo incluído, preferí otra @Query con JOIN y un DTO.
//    }
//
//    fun add(nombre: String, precio: Double, idTipo: Int) = viewModelScope.launch {
//        dao.insert(ProductoEntity(producto = nombre, precio = precio, idTipoProducto = idTipo)); refresh()
//    }
//}

class ProductosViewModel(app: Application) : AndroidViewModel(app) {

    private val productoDao: ProductoDao by lazy { AppDb.get(app).productoDao() }
    private val _productos = MutableStateFlow<List<ProductoEntity>>(emptyList())
    val productos: StateFlow<List<ProductoEntity>> = _productos

    private val tipoProductoDao: TipoProductoDao by lazy { AppDb.get(app).tipoProductoDao() }
    private val _tipos = MutableStateFlow<List<TipoProductoEntity>>(emptyList())
    val tipos: StateFlow<List<TipoProductoEntity>> = _tipos

    fun refreshProductos() = viewModelScope.launch {
        _productos.value = productoDao.listar()
    }

    fun refreshTipos() = viewModelScope.launch {
        _tipos.value = tipoProductoDao.listar()
    }

    fun crearProducto(nombre: String, precio: Double, idTipoProducto: Int) = viewModelScope.launch {
        productoDao.insert(
            ProductoEntity(
                producto = nombre.trim(),
                precio = precio,
                idTipoProducto = idTipoProducto
            )
        )
        refreshProductos()
    }

    fun borrarProducto(p: ProductoEntity) = viewModelScope.launch {
        productoDao.delete(p)
        refreshProductos()
    }
}
