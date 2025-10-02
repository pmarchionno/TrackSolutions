package com.example.tracksolutions.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Clientes : Screen("clientes", "Clientes")
    object Productos : Screen("productos", "Productos")
    object Pedidos : Screen("pedidos", "Pedidos")
    object Reportes : Screen("reportes", "Reportes")
    object Zonas : Screen("zonas", "Zonas")
    object Paises    : Screen("paises", "Países")
}
