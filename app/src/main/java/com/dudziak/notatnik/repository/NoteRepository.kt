package com.dudziak.notatnik.repository

import com.dudziak.notatnik.dao.NoteDao
import com.dudziak.notatnik.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {

    val allNotes: Flow<List<Note>> = dao.getAllNotes()

    suspend fun insert(note: Note) {
        dao.insert(note)
    }

    suspend fun delete(note: Note) {
        dao.delete(note)
    }

    suspend fun update(note: Note) {
        dao.update(note)
    }
}