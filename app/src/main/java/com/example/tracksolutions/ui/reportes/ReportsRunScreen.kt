package com.example.tracksolutions.ui.reportes

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun ReportsRunScreen(
    title: String,
    contentProvider: suspend () -> String
) {
    var content by remember { mutableStateOf("Ejecutando…") }

    LaunchedEffect(Unit) {
        content = try { contentProvider() } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    val h = rememberScrollState()
    val v = rememberScrollState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Divider()
        Spacer(Modifier.height(8.dp))
        SelectionContainer {
            Text(
                content,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(h)
                    .verticalScroll(v)
            )
        }
    }
}
