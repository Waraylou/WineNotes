package com.example.winenotes

import androidx.room.*

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

    @Query("SELECT * FROM Note WHERE noteId = :id")
    fun getNote(id: Long): Note

    @Query("DELETE FROM Note WHERE noteId = :id")
    fun deleteNote(id: Long)
}