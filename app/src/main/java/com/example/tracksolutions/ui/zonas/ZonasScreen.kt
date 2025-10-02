package com.example.tracksolutions.ui.zonas

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
import com.example.tracksolutions.data.entity.ZonaEntity
import com.example.tracksolutions.ui.viewmodel.ZonasViewModel
import kotlinx.coroutines.launch

@Composable
fun ZonasScreen(vm: ZonasViewModel = viewModel()) {
    val zonas by vm.zonas.collectAsStateWithLifecycle(emptyList())
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { vm.refreshZonas() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text("Nueva zona", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la zona") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    val n = nombre.text.trim()
                    if (n.isEmpty()) {
                        scope.launch { snackbar.showSnackbar("Ingresá un nombre.") }
                    } else {
                        vm.crearZona(n)
                        nombre = TextFieldValue("")
                        scope.launch { snackbar.showSnackbar("Zona agregada") }
                    }
                }
            ) { Text("Agregar") }

            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            Text("Zonas", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (zonas.isEmpty()) {
                Text("No hay zonas cargadas.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(zonas) { z ->
                        ZonaRow(
                            zona = z,
                            onDelete = {
                                scope.launch {
                                    vm.borrarZona(z)
                                    snackbar.showSnackbar("Zona eliminada")
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
private fun ZonaRow(
    zona: ZonaEntity,
    onDelete: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(zona.zona, style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
        }
    }
}
