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
                val title = txtTitle.text.toString()
                val desc = txtDesc.text.toString()
                val date = txtDate.text.toString()

                insert(
                    Note(
                        title = title,
                        description = desc,
                        date = date
                    )
                )

                val intent = Intent(this@AddItemActivity, MainActivity::class.java)
                intent.putExtra(EXTRA_TITLE, title)
                intent.putExtra(EXTRA_DESC, desc)
                intent.putExtra(EXTRA_DATE, date)
                startActivity(intent)

                setEmptyField()
            })

            btnUpdate.setOnClickListener {
                val title = txtTitle.text.toString()
                val desc = txtDesc.text.toString()
                val date = txtDate.text.toString()

                update(
                    Note(
                        id = updateId,
                        title = title,
                        description = desc,
                        date = date
                    )
                )
                updateId = 0

                val intent = Intent(this@AddItemActivity, MainActivity::class.java)
                intent.putExtra(EXTRA_TITLE, title)
                intent.putExtra(EXTRA_DESC, desc)
                intent.putExtra(EXTRA_DATE, date)
                startActivity(intent)

                setEmptyField()
            }
        }
    }

    private fun insert(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }

    private fun delete(note: Note) {
        executorService.execute { mNotesDao.delete(note) }
    }

    private fun update(note: Note) {
        executorService.execute {
            try {
                mNotesDao.update(note)
            } catch (e: Exception) {
                Log.e("UpdateNote", "Error updating note: $e")
            }
        }
    }

    private fun setEmptyField() {
        with(binding) {
            txtTitle.setText("")
            txtDesc.setText("")
            txtDate.setText("")
        }
    }
}
