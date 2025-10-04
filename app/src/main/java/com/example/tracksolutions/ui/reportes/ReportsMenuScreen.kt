package com.example.tracksolutions.ui.reportes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReportsMenuScreen(
    onPlanEstimado: () -> Unit,
    onPlanReal: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Reportes (SQLite / SupportSQLiteDatabase)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(8.dp))

        Text(
            "Plan estimado (EXPLAIN QUERY PLAN)",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onPlanEstimado)
                .padding(vertical = 12.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Divider()
        Text(
            "Plan real (tiempo y filas)",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onPlanReal)
                .padding(vertical = 12.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Divider()
    }
}
