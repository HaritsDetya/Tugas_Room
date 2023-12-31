package com.example.tugas_room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.tugas_room.database.Note
import com.example.tugas_room.database.NoteDao
import com.example.tugas_room.database.NoteRoomDatabase
import com.example.tugas_room.databinding.AddItemBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding: AddItemBinding
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
        binding = AddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        with(binding) {
            btnAdd.setOnClickListener(View.OnClickListener {
                val title = titleInput.text.toString()
                val genre = genreInput.text.toString()
                val rilis = rilisInput.text.toString()

                insert(
                    Note(
                        title = title,
                        description = genre,
                        date = rilis
                    )
                )

                val intent = Intent(this@AddItemActivity, MainActivity::class.java)
                intent.putExtra(EXTRA_TITLE, title)
                intent.putExtra(EXTRA_DESC, genre)
                intent.putExtra(EXTRA_DATE, rilis)
                startActivity(intent)

                setEmptyField()
            })
        }
    }

    private fun insert(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }

    private fun setEmptyField() {
        with(binding) {
            titleInput.setText("")
            genreInput.setText("")
            rilisInput.setText("")
        }
    }
}
