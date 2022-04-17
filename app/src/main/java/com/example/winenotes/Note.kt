package com.example.winenotes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Long,
    @ColumnInfo val title: String,
    @ColumnInfo val notes: String,
    @ColumnInfo val lastModified: String
){
    override fun toString(): String {
        return "{title = ${title} \n notes = ${notes} \n time = ${lastModified}}"
    }
}
