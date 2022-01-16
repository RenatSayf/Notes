package com.notes

import android.app.Application
import com.notes.di.AppComponent
import com.notes.di.DaggerAppComponent
import com.notes.di.DependencyManager

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        //DependencyManager.init(this)

        appComponent = DaggerAppComponent.factory().create(this)
    }

}