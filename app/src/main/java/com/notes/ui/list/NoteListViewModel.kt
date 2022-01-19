package com.notes.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.data.NoteDatabase
import com.notes.data.NoteDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject



class NoteListViewModel @Inject constructor(
    private var noteDatabase: NoteDatabase
) : ViewModel() {

    private val _notes = MutableLiveData<List<NoteListItem>?>()
    val notes: LiveData<List<NoteListItem>?> = _notes

    fun setNotes(list: List<NoteListItem>) {
        _notes.value = list
    }

    private val _navigateToNoteCreation = MutableLiveData<Unit?>()
    val navigateToNoteCreation: LiveData<Unit?> = _navigateToNoteCreation

    fun onCreateNoteClick() {
        _navigateToNoteCreation.postValue(Unit)
    }

    fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            _notes.postValue(
                noteDatabase.noteDao().getAll().map {
                    NoteListItem(
                        id = it.id,
                        title = it.title,
                        content = it.content,
                    )
                }
            )
        }
    }

    fun getSortedNotes(isAsc: Boolean): LiveData<List<NoteListItem>?> {
        val notes = MutableLiveData<List<NoteListItem>?>()
        viewModelScope.launch(Dispatchers.IO) {
            notes.postValue(
                noteDatabase.noteDao().getAllSortedByModifiedTime(isAsc).map {
                    NoteListItem(
                        id = it.id,
                        title = it.title,
                        content = it.content,
                    )
                }
            )
        }
        return notes
    }

    fun getNote(id: Long): LiveData<NoteDbo?> {
        val res = MutableLiveData<NoteDbo?>(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val note = noteDatabase.noteDao().get(id)
                res.postValue(note)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return res
    }

}

data class NoteListItem(
    val id: Long,
    val title: String,
    val content: String,
)