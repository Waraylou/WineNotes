package com.example.winenotes

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        if (purpose == "View") {
            val id = intent.getLongExtra("id", 0L)
            viewNote(id)
        }
        else if(purpose == "Add"){

        }
    }

    fun viewNote(id: Long) {
        binding.noteEditText.isEnabled = false
        binding.titleEditText.isEnabled = false
        if (id != 0L) {
            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.noteDao()

                val note = dao.getNote(id)
                withContext(Dispatchers.Main) {
                    binding.noteEditText.setText(note.notes)
                    binding.titleEditText.setText(note.title)

                    var dateString = note.lastModified
                    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    parser.setTimeZone(TimeZone.getTimeZone("UTC"))

                    val dateInDatabase: Date = parser.parse(dateString)
                    val displayFormat = SimpleDateFormat("MM/dd/yyyy HH:mm a")
                    val displayDate = displayFormat.format(dateInDatabase)

                    binding.dateTextView.setText(displayDate)
                }
            }
        } else {
            binding.noteEditText.setText("ERROR")
            binding.titleEditText.setText("ERROR")
        }
    }

    fun deleteNote(){
        val listener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    purpose = "Delete"
                    CoroutineScope(Dispatchers.IO).launch {

                        val db = AppDatabase.getDatabase(applicationContext)
                        val dao = db.noteDao()
                        val intent = intent
                        val id = intent.getLongExtra("id", 0L)

                        dao.deleteNote(id)

                        withContext(Dispatchers.Main) {
                            onBackPressed()
                        }
                    }
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    return;
                }
            }
        }


        val builder = AlertDialog.Builder(binding.root.context)
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you want to delete this Note?")
        builder.setPositiveButton("Yes", listener)
        builder.setNegativeButton(android.R.string.cancel, listener)
        builder.show()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_menu, menu)
        if(purpose == "Add"){
            menu?.findItem(R.id.edit_menu_item)?.isVisible = false
            menu?.findItem(R.id.delete_menu_item)?.isVisible = false
            menu?.findItem(R.id.cancel_menu_item)?.isVisible = true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_menu_item) {
            binding.noteEditText.isEnabled = true
            binding.titleEditText.isEnabled = true

            purpose = "Update"

            setTitle("Edit Note")
            // setting a note to edit mode removes the option from the menu as you are already doing that
            item.isVisible = false

        } else if (item.itemId == R.id.delete_menu_item) {
            deleteNote()
        } else if(item.itemId == R.id.cancel_menu_item){
            purpose = "Canceled"
            Log.i(purpose, "Note addition canceled")
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        if (purpose == "Add") {
            val title = binding.titleEditText.text.toString()
            if (title == "") {
                val builder = AlertDialog.Builder(binding.root.context)
                builder.setTitle("Title Empty")
                builder.setMessage("This note must have a title to be saved")

                builder.show()
                return;
            }
            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.noteDao()
                val title = binding.titleEditText.text.toString()

                val text = binding.noteEditText.text.toString()
                val now = Date()
                val databaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                databaseDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
                var dateString: String = databaseDateFormat.format(now)

                var note = Note(-1, title, text, dateString)

                dao.insertNote(note)
                Log.i(purpose, "added note")
            }
        } else if (purpose == "Update") {
            val title = binding.titleEditText.text.toString()
            if (title == "") {
                Toast.makeText(
                    applicationContext,
                    "Please enter a title for this note",
                    Toast.LENGTH_LONG
                ).show()
                return;
            }
            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.noteDao()

                val text = binding.noteEditText.text.toString()
                val now = Date()
                val databaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                databaseDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
                var dateString: String = databaseDateFormat.format(now)
                val intent = intent
                val id = intent.getLongExtra("id", 0L)

                var note = Note(id, title, text, dateString)

                dao.updateNote(note)
                Log.i(purpose, "updated note")
            }
        }
        super.onBackPressed()
    }
}