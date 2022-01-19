package com.notes.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.data.NoteDatabase
import com.notes.data.NoteDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailsViewModel @Inject constructor(
    private var noteDatabase: NoteDatabase
) : ViewModel() {

    fun saveNote(note: NoteDbo, isNew: Boolean = true): LiveData<Boolean> {
        val res = MutableLiveData(false)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isNew) {
                    noteDatabase.noteDao().insertAll(note)
                    res.postValue(true)
                }
                else {
                    val i = noteDatabase.noteDao().update(note)
                    res.postValue(i > 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                res.postValue(false)
            }
        }
        return res
    }

    fun deleteNote(note: NoteDbo): LiveData<Boolean> {
        val res = MutableLiveData(false)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val i = noteDatabase.noteDao().delete(note.id)
                res.postValue(i > 0)
            } catch (e: Exception) {
                e.printStackTrace()
                res.postValue(false)
            }
        }
        return res
    }


}