package com.example.tracksolutions.data.sqlite

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlin.system.measureNanoTime

class SqlReportsRepository(
    private val db: SupportSQLiteDatabase
) {

    /**
     * Reporte "plan estimado" (similar a SHOWPLAN pero en SQLite):
     * Usa EXPLAIN QUERY PLAN y devuelve una lista legible de pasos.
     */
    fun reportPlanEstimado(): String {
        val q = SimpleSQLiteQuery(SqlSchema.EXPLAIN_QUERY_PLAN)
        val cursor = db.query(q)
        val out = StringBuilder()
        out.appendLine("PLAN ESTIMADO — EXPLAIN QUERY PLAN")
        out.appendLine("----------------------------------")

        // En SQLite, las columnas suelen ser: id, parent, notused, detail
        val colDetail = cursor.getColumnIndex("detail").takeIf { it >= 0 } ?: 3
        val colId = cursor.getColumnIndex("id").takeIf { it >= 0 } ?: 0
        val colParent = cursor.getColumnIndex("parent").takeIf { it >= 0 } ?: 1

        while (cursor.moveToNext()) {
            val id = cursor.getInt(colId)
            val parent = cursor.getInt(colParent)
            val detail = cursor.getString(colDetail)
            out.appendLine("[$id <- $parent] $detail")
        }
        cursor.close()
        return out.toString()
    }

    /**
     * Reporte "plan real":
     * - Ejecuta la consulta objetivo
     * - Cronometra tiempo
     * - Devuelve cantidad de filas y totales
     */
    fun reportPlanReal(): String {
        val out = StringBuilder()
        out.appendLine("PLAN REAL — Ejecución y métricas")
        out.appendLine("--------------------------------")

        var rowCount = 0
        val elapsedNs = measureNanoTime {
            val q = SimpleSQLiteQuery(SqlSchema.QUERY_TOTAL_POR_PRODUCTO)
            val c = db.query(q)
            val idxIdProd = c.getColumnIndex("IdProducto")
            val idxProd = c.getColumnIndex("Producto")
            val idxTotal = c.getColumnIndex("TotalVenta")

            out.appendLine("Resultados:")
            while (c.moveToNext()) {
                val id = c.getInt(idxIdProd)
                val nombre = c.getString(idxProd)
                val total = c.getDouble(idxTotal)
                out.appendLine(" - ($id) $nombre => $total")
                rowCount++
            }
            c.close()
        }

        out.appendLine()
        out.appendLine("Filas devueltas: $rowCount")
        out.appendLine("Tiempo ejecutado: ${elapsedNs / 1_000_000.0} ms")
        out.appendLine()
        out.appendLine("Nota: SQLite no expone STATISTICS IO/TIME como SQL Server.")
        out.appendLine("Se reporta tiempo de pared (wall clock) y filas.")
        return out.toString()
    }
}
