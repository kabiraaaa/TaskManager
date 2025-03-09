package com.ddt.app.task_manager

import android.app.Application
import com.ddt.app.task_manager.data.local.TaskDatabase

class MyApp: Application() {

    companion object {
        lateinit var database: TaskDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = TaskDatabase.getDatabase(this)
    }
}