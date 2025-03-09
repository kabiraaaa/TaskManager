package com.ddt.app.task_manager.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ddt.app.task_manager.data.models.Task
import com.ddt.app.task_manager.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(private val onTaskClick: (Task) -> Unit) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)

        with(holder.binding) {
            tvTaskName.text = task.title
            tvTaskDesc.text = task.description ?: "No description"
            tvPriority.text = "Priority: ${task.priority}"
            tvStatus.text = if (task.isCompleted) "Status: Completed" else "Status: Pending"
            tvDueDate.text = "Due Date: ${formatDate(task.dueDate)}"

            val priorityLevel = task.priority.lowercase()
            val strokeColor = when (priorityLevel) {
                "high" -> Color.RED
                "medium" -> Color.YELLOW
                "low" -> Color.GREEN
                else -> Color.TRANSPARENT
            }

            cvTask.strokeColor = strokeColor
            cvTask.strokeWidth = 4

            cvTask.setOnClickListener {
                onTaskClick(task)
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    class TaskDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }
}