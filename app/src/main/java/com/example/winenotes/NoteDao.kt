package com.example.winenotes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {

    @Insert
    fun insertNote(note: Note) : Long

    @Query("SELECT * FROM Note ORDER BY title")
    fun getNotesByTitle(): List<Note>

    @Query("SELECT * FROM Note ORDER BY lastModified")
    fun getNotesByDate(): List<Note>

    @Update
    fun updateNote(note: Note)
}