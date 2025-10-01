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
import androidx.compose.ui.tooling.preview.Preview
import com.example.tracksolutions.ui.NotesScreen
import com.example.tracksolutions.ui.theme.TrackSolutionsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            TrackSolutionsTheme {
//
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
            TrackSolutionsTheme {
                AppScaffold()
//                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
//                Scaffold(
//                    modifier = Modifier.fillMaxSize(),
//                    topBar = { CenterAlignedTopAppBar(title = { Text("AppTrack Notes") },
//                        scrollBehavior = scrollBehavior) }
//                ) { innerPadding ->
//                    Surface(modifier = Modifier.padding(innerPadding)) {
//                        NotesScreen()   // <- acá va tu pantalla
//                    }
//                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    // Scroll behavior experimental de Material3
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AppTrack Notes") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            NotesScreen()   // tu pantalla Compose con la lista/alta de notas
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrackSolutionsTheme {
        Greeting("Android")
    }
}