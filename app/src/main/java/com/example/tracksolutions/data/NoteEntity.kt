package com.example.tracksolutions.data

import androidx.room.*

@Entity(
    tableName = "notes",
    indices = [Index("updated_at"), Index("is_done")]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name="title") val title: String,
    @ColumnInfo(name="content") val content: String? = null,
    @ColumnInfo(name="is_done") val isDone: Boolean = false,
    @ColumnInfo(name="created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name="updated_at") val updatedAt: Long = System.currentTimeMillis()
)
