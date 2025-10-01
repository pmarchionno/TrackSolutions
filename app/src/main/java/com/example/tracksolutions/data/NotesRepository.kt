package com.example.tracksolutions.data

class NotesRepository(private val dao: NotesDao) {
    suspend fun add(title: String, content: String?) =
        dao.insert(NoteEntity(title = title, content = content))
    suspend fun update(id: Long, title: String, content: String?, done: Boolean) =
        dao.update(id, title, content, done)
    suspend fun remove(id: Long) = dao.delete(id)
    suspend fun list(q: String?, onlyPending: Boolean?) = dao.list(q, onlyPending)
}
