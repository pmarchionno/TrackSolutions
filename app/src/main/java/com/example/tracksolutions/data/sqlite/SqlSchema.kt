package com.example.tracksolutions.data.sqlite

object SqlSchema {

    // Tablas
    const val CREATE_PRODUCTO = """
        CREATE TABLE IF NOT EXISTS Producto(
            IdProducto INTEGER PRIMARY KEY,
            Producto TEXT NOT NULL
        );
    """

    const val CREATE_DETALLE_PEDIDO = """
        CREATE TABLE IF NOT EXISTS DetallePedido(
            IdDetalle INTEGER PRIMARY KEY AUTOINCREMENT,
            IdProducto INTEGER NOT NULL,
            ImporteVenta REAL NOT NULL,
            FOREIGN KEY (IdProducto) REFERENCES Producto(IdProducto)
        );
    """

    // Datos demo (id, nombre)
    val INSERT_PRODUCTO = listOf(
        "INSERT OR IGNORE INTO Producto(IdProducto, Producto) VALUES (1, 'Teclado');",
        "INSERT OR IGNORE INTO Producto(IdProducto, Producto) VALUES (2, 'Mouse');",
        "INSERT OR IGNORE INTO Producto(IdProducto, Producto) VALUES (3, 'Monitor');"
    )

    // Ventas demo
    val INSERT_DETALLE = listOf(
        "INSERT INTO DetallePedido(IdProducto, ImporteVenta) VALUES (1, 12000.0);",
        "INSERT INTO DetallePedido(IdProducto, ImporteVenta) VALUES (1,  8000.0);",
        "INSERT INTO DetallePedido(IdProducto, ImporteVenta) VALUES (2,  5000.0);",
        "INSERT INTO DetallePedido(IdProducto, ImporteVenta) VALUES (2,  5200.0);",
        "INSERT INTO DetallePedido(IdProducto, ImporteVenta) VALUES (3, 60000.0);"
    )

    // Consulta objetivo (equivalente a tu imagen)
    const val QUERY_TOTAL_POR_PRODUCTO = """
        SELECT D.IdProducto, P.Producto, SUM(D.ImporteVenta) AS TotalVenta
        FROM DetallePedido D
        INNER JOIN Producto  P ON D.IdProducto = P.IdProducto
        GROUP BY D.IdProducto, P.Producto
        ORDER BY D.IdProducto;
    """

    // Plan estimado (SQLite) → EXPLAIN QUERY PLAN
    const val EXPLAIN_QUERY_PLAN = "EXPLAIN QUERY PLAN $QUERY_TOTAL_POR_PRODUCTO"
}
