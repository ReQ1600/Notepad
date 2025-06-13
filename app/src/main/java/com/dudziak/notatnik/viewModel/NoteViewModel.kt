package com.dudziak.notatnik.viewModel

import android.service.quicksettings.Tile
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dudziak.notatnik.model.Note
import com.dudziak.notatnik.repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    val notes = repository.allNotes.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    var currentNote by mutableStateOf<Note?>(null)

    fun addNote(title: String, content: String){
        viewModelScope.launch {
            repository.insert(Note(title = title, content = content))
        }
    }

    fun updateNote(note: Note){
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}
