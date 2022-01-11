package com.notes.di

import com.notes.ui.details.DetailsViewModel
import com.notes.ui.list.NoteListViewModel
import dagger.Component
import dagger.Module
import dagger.Provides

@RootScope
@Component(
    dependencies = [
        AppComponent::class,
    ],
    modules = [
    ]
)
interface RootComponent {

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent
        ): RootComponent
    }


    fun getNoteListViewModel(): NoteListViewModel

    fun getDetailsViewModel(): DetailsViewModel

}