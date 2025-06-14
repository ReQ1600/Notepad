package com.dudziak.notatnik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dudziak.notatnik.repository.NoteRepository
import com.dudziak.notatnik.ui.screen.MenuScreen
import com.dudziak.notatnik.ui.theme.NotepadProjectTheme
import com.dudziak.notatnik.viewModel.NoteViewModel
import com.dudziak.notatnik.viewModel.NoteViewModelFactory
import com.dudziak.notatnik.database.NoteDatabase
import com.dudziak.notatnik.ui.screen.AddScanScreen
import com.dudziak.notatnik.ui.screen.NoteScannerScreen
import com.dudziak.notatnik.ui.screen.NoteVoodooScreen
import com.dudziak.notatnik.ui.screen.NotesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotepadProjectTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    NotepadApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NotepadApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val context = LocalContext.current
    val dao = NoteDatabase.getDB(context).noteDao()
    val repository = NoteRepository(dao)
    val viewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(repository)
    )

    NavHost(navController = navController, startDestination = "menu"){
        composable("menu") { MenuScreen(navController) }
        composable("notes") { NotesScreen(navController =  navController, viewModel = viewModel) }
        composable("noteVoodoo") { NoteVoodooScreen(navController = navController, noteViewModel = viewModel)}
        composable("scan") { NoteScannerScreen(navController = navController) }
        composable("addScan") { AddScanScreen(navController = navController, noteViewModel = viewModel) }
    }
}

