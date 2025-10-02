package com.example.tracksolutions.ui.pedidos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tracksolutions.data.entity.ClienteEntity
import com.example.tracksolutions.data.entity.ProductoEntity
import com.example.tracksolutions.data.entity.CanalVentaEntity
import com.example.tracksolutions.ui.viewmodel.PedidosViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(vm: PedidosViewModel = viewModel()) {
    // Catálogos
    val clientes    by vm.clientes.collectAsStateWithLifecycle(emptyList())
    val productos   by vm.productos.collectAsStateWithLifecycle(emptyList())
    val canales     by vm.canales.collectAsStateWithLifecycle(emptyList())
    // Carrito temporal en memoria
    val lineas      by vm.lineas.collectAsStateWithLifecycle(emptyList())
    val total       by vm.total.collectAsStateWithLifecycle(0.0)

    // Formulario pedido
    var clienteSel by remember { mutableStateOf<ClienteEntity?>(null) }
    var canalSel   by remember { mutableStateOf<CanalVentaEntity?>(null) }
    var expandClientes by remember { mutableStateOf(false) }
    var expandCanales  by remember { mutableStateOf(false) }

    // Fechas (simple: texto YYYY-MM-DD; el VM las convierte a epoch ms)
    var fechaPedidoTxt by remember { mutableStateOf(TextFieldValue("")) }
    var fechaEnvioTxt  by remember { mutableStateOf(TextFieldValue("")) }

    // Formulario línea
    var productoSel by remember { mutableStateOf<ProductoEntity?>(null) }
    var expandProductos by remember { mutableStateOf(false) }
    var cantidadTxt by remember { mutableStateOf(TextFieldValue("1")) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.cargarCatalogos()
        vm.nuevoPedido() // limpia carrito
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

            Text("Nuevo pedido", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // ----- Cliente -----
            ExposedDropdownMenuBox(
                expanded = expandClientes,
                onExpandedChange = { expandClientes = !expandClientes }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = clienteSel?.cliente ?: "",
                    onValueChange = {},
                    label = { Text("Cliente") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandClientes) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandClientes,
                    onDismissRequest = { expandClientes = false }
                ) {
                    clientes.forEach { c ->
                        DropdownMenuItem(
                            text = { Text("${c.cliente}  •  ${c.email}") },
                            onClick = {
                                clienteSel = c
                                expandClientes = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ----- Canal de venta -----
            ExposedDropdownMenuBox(
                expanded = expandCanales,
                onExpandedChange = { expandCanales = !expandCanales }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = canalSel?.canalVenta ?: "",
                    onValueChange = {},
                    label = { Text("Canal de venta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandCanales) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandCanales,
                    onDismissRequest = { expandCanales = false }
                ) {
                    canales.forEach { cn ->
                        DropdownMenuItem(
                            text = { Text(cn.canalVenta) },
                            onClick = {
                                canalSel = cn
                                expandCanales = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ---- Fechas (YYYY-MM-DD) ----
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fechaPedidoTxt,
                    onValueChange = { fechaPedidoTxt = it },
                    label = { Text("Fecha pedido (YYYY-MM-DD)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = fechaEnvioTxt,
                    onValueChange = { fechaEnvioTxt = it },
                    label = { Text("Fecha envío (YYYY-MM-DD)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            Text("Agregar productos", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // ----- Selector de producto + cantidad -----
            ExposedDropdownMenuBox(
                expanded = expandProductos,
                onExpandedChange = { expandProductos = !expandProductos }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = productoSel?.let { "${it.producto} (${it.precio})" } ?: "",
                    onValueChange = {},
                    label = { Text("Producto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandProductos) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandProductos,
                    onDismissRequest = { expandProductos = false }
                ) {
                    productos.forEach { p ->
                        DropdownMenuItem(
                            text = { Text("${p.producto}  •  $${p.precio}") },
                            onClick = {
                                productoSel = p
                                expandProductos = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cantidadTxt,
                    onValueChange = { cantidadTxt = it },
                    label = { Text("Cantidad") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = {
                        val p = productoSel
                        val cant = cantidadTxt.text.trim().toIntOrNull()
                        if (p == null || cant == null || cant <= 0) {
                            scope.launch { snackbar.showSnackbar("Elegí un producto y una cantidad válida.") }
                        } else {
                            vm.agregarLinea(p, cant)
                            // limpiar campos de línea
                            productoSel = null
                            cantidadTxt = TextFieldValue("1")
                        }
                    },
                    modifier = Modifier.alignByBaseline()
                ) { Text("Añadir") }
            }

            Spacer(Modifier.height(16.dp))
            Text("Líneas del pedido", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // --- Lista de líneas
            if (lineas.isEmpty()) {
                Text("No hay líneas agregadas.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    itemsIndexed(lineas) { idx, li ->
                        LineaRow(
                            nombre = li.producto.producto,
                            precio = li.producto.precio,
                            cantidad = li.cantidad,
                            total = li.cantidad * li.producto.precio,
                            onDelete = { vm.quitarLinea(idx) }
                        )
                        Divider()
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Total: $${"%.2f".format(total)}", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    val cliente = clienteSel
                    val canal   = canalSel
                    if (cliente == null || canal == null || lineas.isEmpty()) {
                        scope.launch { snackbar.showSnackbar("Cliente, canal y al menos una línea son obligatorios.") }
                        return@Button
                    }
                    val ok = vm.guardarPedido(
                        fechaPedidoTxt.text.trim(),
                        fechaEnvioTxt.text.trim().ifEmpty { null },
                        cliente.idCliente,
                        canal.idCanalVenta
                    )
                    scope.launch {
                        if (ok) {
                            snackbar.showSnackbar("Pedido guardado")
                            // limpiar todo para uno nuevo
                            vm.nuevoPedido()
                            clienteSel = null
                            canalSel = null
                            fechaPedidoTxt = TextFieldValue("")
                            fechaEnvioTxt  = TextFieldValue("")
                        } else {
                            snackbar.showSnackbar("Error al guardar el pedido")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar pedido") }
        }
    }
}

@Composable
private fun LineaRow(
    nombre: String,
    precio: Double,
    cantidad: Int,
    total: Double,
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
            Text("Precio: $${"%.2f".format(precio)}  •  Cantidad: $cantidad", style = MaterialTheme.typography.bodyMedium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("$${"%.2f".format(total)}", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Eliminar") }
        }
    }
}
