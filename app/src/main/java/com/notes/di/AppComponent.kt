package com.notes.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.notes.data.NoteDatabase
import com.notes.ui.details.NoteDetailsFragment
import com.notes.ui.list.NoteListFragment
import com.notes.ui.list.NoteListViewModel
import dagger.*
import javax.inject.Singleton

@Singleton
@Component(modules = [ AppModule::class,])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
        ): AppComponent
    }

    fun getNoteDatabase(): NoteDatabase
    fun inject(fragment: NoteListFragment)
    fun inject(fragment: NoteDetailsFragment)

}

@Module(includes = [AppModule.Binding::class])
class AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(
        context: Context
    ) = Room.databaseBuilder(
        context,
        NoteDatabase::class.java, "database-note.db"
    ).createFromAsset("database-note.db")
        .build()

    @Module
    interface Binding {

        @Binds
        fun bindContext(application: Application): Context

    }

    @Singleton
    @Provides
    fun provideViewModelFactory(db: NoteDatabase): ViewModelFactory {
        return ViewModelFactory(db)
    }


}