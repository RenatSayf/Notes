package com.notes.di

import android.app.Application
import com.notes.ui.details.NoteDetailsFragment
import com.notes.ui.list.NoteListFragment


class DependencyManager private constructor(
    application: Application
) {

    companion object {
        private lateinit var instance: DependencyManager

        fun init(application: Application) {
            instance = DependencyManager(application)
        }

        fun noteListViewModel() = instance.rootComponent.getNoteListViewModel()
        fun inject(fragment: NoteListFragment) = instance.appComponent.inject(fragment)
        fun inject(fragment: NoteDetailsFragment) = instance.appComponent.inject(fragment)
    }

    private val appComponent = DaggerAppComponent.factory().create(application)

    private val rootComponent = DaggerRootComponent.factory().create(appComponent)

}
