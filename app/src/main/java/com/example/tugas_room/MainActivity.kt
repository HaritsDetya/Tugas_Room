package com.example.tugas_room

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tugas_room.database.Note
import com.example.tugas_room.database.NoteDao
import com.example.tugas_room.database.NoteRoomDatabase
import com.example.tugas_room.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService
    private lateinit var noteAdapter: NoteAdapter
    private var updateId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        noteAdapter = NoteAdapter(this, emptyList(),
            onItemClick = { note ->
                handleItemClick(note)
            },
            onItemLongClick = { note ->
                handleItemLongClick(note)
            }
        )

        with(binding.recycle) {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        binding.plus.setOnClickListener {
            val intent = Intent(this@MainActivity, AddItemActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAllNotes() {
        mNotesDao.allNotes.observe(this) { notes ->
            // Update the data set in the adapter
            noteAdapter.notes = notes
            noteAdapter.notifyDataSetChanged()
        }
    }



    private fun handleItemClick(note: Note) {
        // Handle item click
        updateId = note.id

        // Redirect to AddItemActivity for update
        val intent = Intent(this@MainActivity, AddItemActivity::class.java)
        intent.putExtra(AddItemActivity.EXTRA_TITLE, note.title)
        intent.putExtra(AddItemActivity.EXTRA_DESC, note.description)
        intent.putExtra(AddItemActivity.EXTRA_DATE, note.date)
        startActivity(intent)
    }

    private fun handleItemLongClick(note: Note) {
        // Handle item long click
        delete(note)
    }

    private fun delete(note: Note) {
        executorService.execute { mNotesDao.delete(note) }
    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }
}

