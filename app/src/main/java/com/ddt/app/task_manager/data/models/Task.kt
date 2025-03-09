package com.ddt.app.task_manager.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String?,
    val priority: String,
    val dueDate: Long,
    val isCompleted: Boolean = false
)
