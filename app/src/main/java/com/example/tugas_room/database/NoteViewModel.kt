package com.example.tugas_room.database

import androidx.lifecycle.ViewModel

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {

    fun updateNote(note: Note) {
         if (note.id == 0) {
            noteDao.update(note)
        }
    }
}
