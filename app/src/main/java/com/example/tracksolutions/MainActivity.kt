package com.example.tracksolutions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.tracksolutions.ui.theme.TrackSolutionsTheme
import kotlinx.coroutines.launch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.tracksolutions.data.AppDb
import com.example.tracksolutions.data.sqlite.DbProvider
import com.example.tracksolutions.data.sqlite.SqlReportsRepository
import com.example.tracksolutions.ui.menu.AppDrawer
import com.example.tracksolutions.ui.navigation.AppNavHost
import com.example.tracksolutions.ui.navigation.Screen
import net.sqlcipher.database.SQLiteDatabase


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SQLiteDatabase.loadLibs(this)
        setContent {
            TrackSolutionsTheme {
                AppScaffold()
            }
        }
        SQLiteDatabase.loadLibs(this)                  // 1) cargar SQLCipher
        val roomDb = AppDb.get(applicationContext)     // 2) crea/abre apptrack.db cifrada

        if (com.example.tracksolutions.BuildConfig.DEBUG) {
            // 3) verifica con SQLCipher (cipher_version + integrity_check + clave incorrecta)
            com.example.tracksolutions.debug.PlainDbProbe.verifyAppDbIsEncrypted(this)
            // alternativa de laboratorio (no toca apptrack.db):
            // com.example.tracksolutions.debug.PlainDbProbe.createEncryptedProbeAndVerify(this)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val context = LocalContext.current
    val dbProvider = remember { DbProvider(context) }
    val repo = remember { SqlReportsRepository(dbProvider.open()) }

    val items = listOf(
        Screen.Clientes,
        Screen.Productos,
        Screen.Pedidos,
        Screen.Reportes,
        Screen.Zonas,
        Screen.Paises,
        Screen.ReportsMenu
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                drawerState = drawerState,
                scope = scope,
                items = items
            )
        }
    ) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("TrackSolutions") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            Surface(Modifier.padding(innerPadding)) {
                AppNavHost(navController = navController)
            }
        }
    }
}
