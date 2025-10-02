package com.example.tracksolutions.ui.paises

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
import com.example.tracksolutions.ui.viewmodel.PaisesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaisesScreen(vm: PaisesViewModel = viewModel()) {
    val paises by vm.paises.collectAsStateWithLifecycle(emptyList())
    val zonas by vm.zonas.collectAsStateWithLifecycle(emptyList())

    // UI state (form)
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var zonaSeleccionada by remember { mutableStateOf<ZonaEntity?>(null) }
    var expandZonas by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    // Primera carga
    LaunchedEffect(Unit) {
        vm.refreshZonas()
        vm.refreshPaises()
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

            Text("Nuevo país", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del país") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Selector de Zona (si tu esquema requiere idZona)
            ExposedDropdownMenuBox(
                expanded = expandZonas,
                onExpandedChange = { expandZonas = !expandZonas },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = zonaSeleccionada?.zona ?: "",
                    onValueChange = {},
                    label = { Text("Zona") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandZonas) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandZonas,
                    onDismissRequest = { expandZonas = false }
                ) {
                    zonas.forEach { z ->
                        DropdownMenuItem(
                            text = { Text(z.zona) },
                            onClick = {
                                zonaSeleccionada = z
                                expandZonas = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    val nombreOk = nombre.text.trim()
                    val zonaOk = zonaSeleccionada

                    if (nombreOk.isEmpty() || zonaOk == null) {
                        scope.launch { snackbar.showSnackbar("Completá nombre y zona.") }
                    } else {
                        vm.crearPais(nombreOk, zonaOk.idZona)
                        nombre = TextFieldValue("")
                        zonaSeleccionada = null
                        scope.launch { snackbar.showSnackbar("País agregado") }
                    }
                }
            ) { Text("Agregar") }

            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            Text("Países", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (paises.isEmpty()) {
                Text("No hay países cargados.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(paises) { p ->
                        PaisRow(
                            nombre = p.pais,
                            zona = zonas.firstOrNull { it.idZona == p.idZona }?.zona ?: "—",
                            onDelete = {
                                scope.launch {
                                    vm.borrarPais(p)
                                    snackbar.showSnackbar("País eliminado")
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
private fun PaisRow(
    nombre: String,
    zona: String,
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
            Text(zona, style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
        }
    }
}

