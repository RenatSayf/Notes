package com.notes.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notes.data.NoteDatabase
import com.notes.ui.details.DetailsViewModel
import com.notes.ui.list.NoteListViewModel
import java.lang.ClassCastException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ViewModelFactory @Inject constructor(private val db: NoteDatabase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass) {
            NoteListViewModel::class.java -> {
                NoteListViewModel(db) as T
            }
            DetailsViewModel::class.java -> {
                DetailsViewModel(db) as T
            }
            else -> {
                throw ClassCastException("$modelClass cannot be cast to ${ViewModel::class.java.simpleName}")
            }
        }
    }
}