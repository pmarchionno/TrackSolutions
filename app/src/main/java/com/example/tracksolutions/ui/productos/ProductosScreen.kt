package com.example.tracksolutions.ui.productos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tracksolutions.data.entity.TipoProductoEntity
import com.example.tracksolutions.ui.viewmodel.ProductosViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(vm: ProductosViewModel = viewModel()) {
    // Estado proveniente del VM
    val productos by vm.productos.collectAsStateWithLifecycle(emptyList())
    val tipos by vm.tipos.collectAsStateWithLifecycle(emptyList())

    // Estado de formulario
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var precio by remember { mutableStateOf(TextFieldValue("")) }
    var tipoSeleccionado by remember { mutableStateOf<TipoProductoEntity?>(null) }
    var expandTipos by remember { mutableStateOf(false) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Carga inicial
    LaunchedEffect(Unit) {
        vm.refreshTipos()
        vm.refreshProductos()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text("Nuevo producto", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio (ej: 1999.99)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            // Selector de Tipo de Producto
            ExposedDropdownMenuBox(
                expanded = expandTipos,
                onExpandedChange = { expandTipos = !expandTipos },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = tipoSeleccionado?.tipoProducto ?: "",
                    onValueChange = {},
                    label = { Text("Tipo de producto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandTipos) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandTipos,
                    onDismissRequest = { expandTipos = false }
                ) {
                    tipos.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.tipoProducto) },
                            onClick = {
                                tipoSeleccionado = t
                                expandTipos = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    val n = nombre.text.trim()
                    val pTxt = precio.text.trim().replace(',', '.')
                    val t = tipoSeleccionado
                    val p = pTxt.toDoubleOrNull()

                    if (n.isEmpty() || p == null || p < 0.0 || t == null) {
                        scope.launch { snackbar.showSnackbar("Completá nombre, precio válido y tipo.") }
                    } else {
                        vm.crearProducto(n, p, t.idTipoProducto)
                        nombre = TextFieldValue("")
                        precio = TextFieldValue("")
                        tipoSeleccionado = null
                        scope.launch { snackbar.showSnackbar("Producto agregado") }
                    }
                }
            ) { Text("Agregar") }

            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            Text("Productos", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (productos.isEmpty()) {
                Text("No hay productos cargados.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(productos) { prod ->
                        ProductoRow(
                            nombre = prod.producto,
                            precio = prod.precio,
                            tipo = tipos.firstOrNull { it.idTipoProducto == prod.idTipoProducto }?.tipoProducto ?: "—",
                            onDelete = {
                                scope.launch {
                                    vm.borrarProducto(prod)
                                    snackbar.showSnackbar("Producto eliminado")
                                }
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoRow(
    nombre: String,
    precio: Double,
    tipo: String,
    onDelete: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f).padding(end = 8.dp)) {
            Text(nombre, style = MaterialTheme.typography.titleMedium)
            Text("Tipo: $tipo", style = MaterialTheme.typography.bodySmall)
            Text("Precio: $precio", style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
        }
    }
}

