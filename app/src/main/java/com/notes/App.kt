package com.notes

import android.app.Application
import com.notes.di.DependencyManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DependencyManager.init(this)
    }

}