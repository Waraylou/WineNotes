package com.example.winenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winenotes.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var data = mutableListOf<Note>()
    private lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.myRecyclerView.setLayoutManager(layoutManager)

        val divider = DividerItemDecoration(
            applicationContext, layoutManager.orientation
        )
        binding.myRecyclerView.addItemDecoration(divider)

        adapter = MyAdapter()
        binding.myRecyclerView.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.noteDao()
            val results = dao.getNotesByDate()

            withContext(Dispatchers.Main) {
                data.clear()
                data.addAll(results)
                adapter.notifyDataSetChanged()
            }
        }
    }


    override fun onResume() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.noteDao()
            val results = dao.getNotesByDate()

            withContext(Dispatchers.Main) {
                data.clear()
                data.addAll(results)
                adapter.notifyDataSetChanged()
            }
        }
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_menu_item) {
            val intent = Intent(applicationContext, NoteActivity::class.java)
            intent.putExtra("purpose","Add")

            startActivity(intent)

        } else if (item.itemId == R.id.date_sort_menu_item){
            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.noteDao()
                val results = dao.getNotesByDate()

                withContext(Dispatchers.Main) {
                    data.clear()
                    data.addAll(results)
                    adapter.notifyDataSetChanged()
                }
            }
        } else if (item.itemId == R.id.title_sort_menu_item){
            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.noteDao()
                val results = dao.getNotesByTitle()

                withContext(Dispatchers.Main) {
                    data.clear()
                    data.addAll(results)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    inner class MyViewHolder(val view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.findViewById<View>(R.id.item_constraintLayout)
                .setOnClickListener(this)
        }

        fun setText(text: String, date:String) {
            view.findViewById<TextView>(R.id.item_title_textView).setText(text)
            view.findViewById<TextView>(R.id.item_date_textView).setText(date)
        }

        override fun onClick(view: View?) {
            if (view != null) {
                val intent = Intent(view.context, NoteActivity::class.java)
                intent.putExtra("purpose","View")
                intent.putExtra("id", data[adapterPosition].noteId)
                startActivity(intent)
            }
        }
    }


    inner class MyAdapter() : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            var title = data[position].title
            var date = data[position].lastModified

            holder.setText(title,date)
        }

        override fun getItemCount(): Int {
            return data.size
            return 0;
        }

    }
}