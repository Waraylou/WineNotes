package com.example.winenotes

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface NoteDao {

    @Insert
    fun insertNote(note: Note) : Long
}