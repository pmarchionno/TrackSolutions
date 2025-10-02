package com.example.tracksolutions.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tracksolutions.ui.clientes.ClientesScreen
import com.example.tracksolutions.ui.paises.PaisesScreen
import com.example.tracksolutions.ui.productos.ProductosScreen
import com.example.tracksolutions.ui.zonas.ZonasScreen
import com.example.tracksolutions.ui.pedidos.PedidosScreen
import com.example.tracksolutions.ui.reportes.ReportesScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Clientes.route
    ) {
        composable(Screen.Clientes.route) { ClientesScreen() }
        composable(Screen.Productos.route) { ProductosScreen() }
        composable(Screen.Pedidos.route) { PedidosScreen() }
        composable(Screen.Reportes.route) { ReportesScreen() }
        composable(Screen.Zonas.route) { ZonasScreen() }
        composable(Screen.Paises.route) { PaisesScreen() }
    }
}
