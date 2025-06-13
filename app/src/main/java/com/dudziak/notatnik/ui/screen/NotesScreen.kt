package com.dudziak.notatnik.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dudziak.notatnik.R
import com.dudziak.notatnik.viewModel.NoteViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(navController: NavController, modifier: Modifier = Modifier, viewModel: NoteViewModel) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_notes)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("menu") }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.menu_notes))
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.currentNote = null
                navController.navigate("noteVoodoo")}
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.note_add))
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(notes) { note ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.currentNote = note
                            navController.navigate("noteVoodoo")
                        }
                        .padding(16.dp)
                ) {
                    Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                }
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteVoodooScreen(navController: NavController, noteViewModel: NoteViewModel, modifier: Modifier = Modifier)
{
    val edit = noteViewModel.currentNote != null;

    var title by remember { mutableStateOf(if (edit) noteViewModel.currentNote?.title.toString() else "") }
    var content by remember { mutableStateOf(if (edit) noteViewModel.currentNote?.content.toString() else "") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (edit) R.string.note_edit else R.string.note_add)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack("notes", inclusive = false) }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.note_title)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(stringResource(R.string.note_contents)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(scrollState),
                maxLines = Int.MAX_VALUE,
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row (modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    if (noteViewModel.currentNote != null) {
                        val updatedNote = noteViewModel.currentNote!!.copy(
                            title = title.ifBlank { "untitled" },
                            content = content.ifBlank { "" }
                        )
                        noteViewModel.updateNote(updatedNote)
                    }
                    else {
                        noteViewModel.addNote(
                            title = title.ifBlank { "untitled" },
                            content = content.ifBlank { "" }
                        )
                    }
                    navController.popBackStack("notes", inclusive = false)
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.note_confirm))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    //always is in that case but kotlin doesnt like me
                    if (noteViewModel.currentNote != null) {
                        noteViewModel.deleteNote(noteViewModel.currentNote!!)
                    };
                    navController.popBackStack("notes", inclusive = false)
                },
                    enabled = edit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.note_delete))
                }
            }
        }
    }
}


