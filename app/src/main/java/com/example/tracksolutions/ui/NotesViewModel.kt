package com.example.tracksolutions.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracksolutions.data.AppDb
import com.example.tracksolutions.data.NoteEntity
import com.example.tracksolutions.data.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotesViewModel(app: Application) : AndroidViewModel(app) {
    private val dao by lazy { AppDb.get(app).notesDao() }
    private val repo by lazy { NotesRepository(dao) }

    private val _items = MutableStateFlow<List<NoteEntity>>(emptyList())
    val items: StateFlow<List<NoteEntity>> = _items

    fun refresh(q: String? = null, onlyPending: Boolean? = null) = viewModelScope.launch {
        _items.value = repo.list(q, onlyPending)
    }

    fun add(title: String, content: String?) = viewModelScope.launch {
        repo.add(title, content); refresh()
    }

    fun update(id: Long, t: String, c: String?, done: Boolean) = viewModelScope.launch {
        repo.update(id, t, c, done); refresh()
    }

    fun remove(id: Long) = viewModelScope.launch {
        repo.remove(id); refresh()
    }
}
