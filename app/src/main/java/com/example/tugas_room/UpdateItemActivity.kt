package com.example.tugas_room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.get
import com.example.tugas_room.database.Note
import com.example.tugas_room.database.NoteDao
import com.example.tugas_room.database.NoteRoomDatabase
import com.example.tugas_room.databinding.AddItemBinding
import com.example.tugas_room.databinding.UpdateItemBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UpdateItemActivity : AppCompatActivity() {
    private lateinit var binding: UpdateItemBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService
    private var updateId: Int = 0

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESC = "extra_desc"
        const val EXTRA_DATE = "extra_date"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UpdateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        updateId = intent.getIntExtra("EXTRA_ID", 0)

        with(binding){
            btnUpdate.setOnClickListener {
                val title = titleInput.text.toString()
                val genre = genreInput.text.toString()
                val rilis = rilisInput.text.toString()

                val updatedNote = Note(
                    id = updateId,
                    title = title,
                    description = genre,
                    date = rilis
                )

                update(updatedNote)
                updateId = 0

                val intent = Intent(this@UpdateItemActivity, MainActivity::class.java)
                intent.putExtra(EXTRA_TITLE, title)
                intent.putExtra(EXTRA_DESC, genre)
                intent.putExtra(EXTRA_DATE, rilis)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun update(note: Note) {
        executorService.execute {
            try {
                mNotesDao.update(note)
            } catch (e: Exception) {
                // Handle the exception
            }
        }
    }
}