package com.example.tracksolutions.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tracksolutions.data.NoteEntity

@Composable
fun NotesScreen(vm: NotesViewModel = viewModel()) {
    val items by vm.items.collectAsStateWithLifecycle(initialValue = emptyList())

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.refresh() }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title, onValueChange = { title = it },
            label = { Text("Título") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = content, onValueChange = { content = it },
            label = { Text("Contenido") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            if (title.isNotBlank()) {
                vm.add(title.trim(), content.ifBlank { null })
                title = ""; content = ""
            }
        }) { Text("Agregar") }

        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(items) { n -> NoteItem(n) }
        }
    }
}

@Composable
private fun NoteItem(n: NoteEntity) {
    ElevatedCard(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(n.title, style = MaterialTheme.typography.titleMedium)
            if (!n.content.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(n.content!!)
            }
        }
    }
}
