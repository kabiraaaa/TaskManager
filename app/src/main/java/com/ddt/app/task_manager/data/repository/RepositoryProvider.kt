package com.ddt.app.task_manager.data.repository

import com.ddt.app.task_manager.MyApp

object RepositoryProvider {
    val repository: TaskRepository by lazy {
        TaskRepository(MyApp.database.taskDao())
    }
}