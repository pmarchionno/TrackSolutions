package com.example.tracksolutions.data

import androidx.room.*

@Dao
interface NotesDao {
    @Insert suspend fun insert(note: NoteEntity): Long

    @Query("""UPDATE notes SET 
      title=:title, content=:content, is_done=:done, 
      updated_at=:updatedAt WHERE id=:id""")
    suspend fun update(id: Long, title: String, content: String?, done: Boolean, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("DELETE FROM notes WHERE id=:id")
    suspend fun delete(id: Long): Int

    @Query("""SELECT * FROM notes
     WHERE (:q IS NULL OR title LIKE '%'||:q||'%' OR content LIKE '%'||:q||'%')
     AND (:onlyPending IS NULL OR is_done = (CASE WHEN :onlyPending THEN 0 ELSE 1 END))
     ORDER BY updated_at DESC""")
    suspend fun list(q: String?, onlyPending: Boolean?): List<NoteEntity>
}
