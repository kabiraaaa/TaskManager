package com.ddt.app.task_manager.data.repository

import androidx.lifecycle.LiveData
import com.ddt.app.task_manager.data.local.TaskDao
import com.ddt.app.task_manager.data.models.Task

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    fun getTaskById(taskId: Int): LiveData<Task> {
        return taskDao.getTaskById(taskId)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}