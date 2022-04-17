package com.example.winenotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.winenotes.databinding.ActivityMainBinding
import com.example.winenotes.databinding.ActivityNoteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding

    private var purpose: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent

        purpose = intent.getStringExtra("purpose")

        setTitle("${purpose} Note")

        if(purpose == "View"){
            val id = intent.getLongExtra("id",0L)
            binding.noteEditText.focusable = View.NOT_FOCUSABLE
            binding.titleEditText.focusable = View.NOT_FOCUSABLE
            if(id != 0L){
                // put database coroutine here and get values at this id passed in
                // if id == 0L then Put ERROR in all fields
            }
        }
    }

    override fun onBackPressed() {
        if(purpose == "Add") {
            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.noteDao()
                val title = binding.titleEditText.text.toString()
                if (title == "") {
                    // make a toast to tell user to fill out fields

                } else {
                    val text = binding.noteEditText.text.toString()
                    val now = Date()
                    val databaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    databaseDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
                    var dateString: String = databaseDateFormat.format(now)

                    var note = Note(0, title, text, dateString)

                    dao.insertNote(note)
                }
            }
        } else if(purpose == "Update"){
            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.noteDao()
                val title = binding.titleEditText.text.toString()
                if(title == ""){
                    // make a toast to tell user to fill out fields

                } else {
                    val text = binding.noteEditText.text.toString()
                    val now = Date()
                    val databaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    databaseDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
                    var dateString: String = databaseDateFormat.format(now)

                    var note = Note(0, title, text, dateString)

                    dao.updateNote(note)
                }
            }
        }

        super.onBackPressed()
    }
}