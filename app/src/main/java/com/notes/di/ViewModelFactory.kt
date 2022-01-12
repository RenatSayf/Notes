package com.notes.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notes.data.NoteDatabase

class ViewModelFactory(val db: NoteDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(NoteDatabase::class.java).newInstance(db)
    }
}