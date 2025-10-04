package com.example.tracksolutions.ui.clientes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.jarjarred.org.antlr.v4.codegen.model.Sync
import com.example.tracksolutions.data.entity.ClienteEntity
import com.example.tracksolutions.data.entity.PaisEntity
import com.example.tracksolutions.ui.viewmodel.ClientesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(vm: ClientesViewModel = viewModel()) {
    // Estados de la VM
    val clientes by vm.clientes.collectAsStateWithLifecycle(emptyList())
    val paises by vm.paises.collectAsStateWithLifecycle(emptyList())

    // Estados de UI (formulario)
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var paisSeleccionado by remember { mutableStateOf<PaisEntity?>(null) }
    var expandPaises by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Primera carga
    LaunchedEffect(Unit) {
        vm.refreshCatalogos()
        vm.refreshClientes()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        // --- Formulario de Alta ---
        Text("Nuevo cliente", style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = { vm.sincronizar() }) {
            Icon(
                Icons.Default.Sync, contentDescription = "Sincronizar"
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Selector de País
        ExposedDropdownMenuBox(
            expanded = expandPaises,
            onExpandedChange = { expandPaises = !expandPaises },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = paisSeleccionado?.pais ?: "",
                onValueChange = {},
                label = { Text("País") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandPaises) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandPaises,
                onDismissRequest = { expandPaises = false }
            ) {
                paises.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(p.pais) },
                        onClick = {
                            paisSeleccionado = p
                            expandPaises = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                val nombreOk = nombre.text.trim()
                val emailOk = email.text.trim()
                val paisOk = paisSeleccionado

                if (nombreOk.isEmpty() || emailOk.isEmpty() || paisOk == null) {
                    scope.launch { snackbarHostState.showSnackbar("Completa nombre, email y país.") }
                } else {
                    vm.crearCliente(nombreOk, emailOk, paisOk.idPais)
                    nombre = TextFieldValue("")
                    email = TextFieldValue("")
                    paisSeleccionado = null
                    scope.launch { snackbarHostState.showSnackbar("Cliente agregado") }
                }
            }
        ) { Text("Agregar") }

        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(8.dp))

        // --- Listado ---
        Text("Clientes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (clientes.isEmpty()) {
            Text("No hay clientes cargados.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(clientes) { item ->
                    ClienteRow(
                        nombre = item.cliente.cliente,
                        email = item.cliente.email,
                        pais = item.pais.pais,
                        zona = item.zona.zona,
                        onDelete = {
                            // Confirmación simple (podés reemplazar por diálogo)
                            scope.launch {
                                vm.borrarCliente(
                                    ClienteEntity(
                                        idCliente = item.cliente.idCliente,
                                        cliente = item.cliente.cliente,
                                        email = item.cliente.email,
                                        idPais = item.cliente.idPais
                                    )
                                )
                                snackbarHostState.showSnackbar("Cliente eliminado")
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }

    // Snackbar host (mensajes)
    Box(Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        )
    }
}

@Composable
private fun ClienteRow(
    nombre: String,
    email: String,
    pais: String,
    zona: String,
    onDelete: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f).padding(end = 8.dp)) {
            Text(nombre, style = MaterialTheme.typography.titleMedium)
            Text(email, style = MaterialTheme.typography.bodyMedium)
            Text("$pais • $zona", style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
        }
    }
}
