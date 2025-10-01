package com.example.tracksolutions.data

import android.content.Context
import androidx.room.*

@Database(entities = [NoteEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun notesDao(): NotesDao
    companion object {
        @Volatile private var INSTANCE: AppDb? = null
        fun get(ctx: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(ctx, AppDb::class.java, "apptrack.db").build()
                    .also { INSTANCE = it }
            }
    }
}
