package com.ddt.app.task_manager.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ddt.app.task_manager.R
import com.ddt.app.task_manager.data.local.TaskDatabase
import com.ddt.app.task_manager.data.models.Task
import com.ddt.app.task_manager.data.repository.TaskRepository
import com.ddt.app.task_manager.databinding.FragmentTaskViewBinding
import com.ddt.app.task_manager.ui.view_model_factory.TaskViewModelFactory
import com.ddt.app.task_manager.ui.viewmodels.TaskViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskViewFragment : Fragment(R.layout.fragment_task_view) {

    private lateinit var binding: FragmentTaskViewBinding
    private lateinit var taskViewModel: TaskViewModel
    private var taskId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTaskViewBinding.bind(view)

        val taskDao = TaskDatabase.getDatabase(requireContext()).taskDao()
        val repository = TaskRepository(taskDao)
        taskViewModel =
            ViewModelProvider(this, TaskViewModelFactory(repository))[TaskViewModel::class.java]

        taskId = arguments?.getInt("task_id")!!

        taskViewModel.getTaskById(taskId).observe(viewLifecycleOwner) { task ->
            task?.let {
                bindTaskDetails(it)
                initClickListeners(it)
            }
        }
    }

    private fun initClickListeners(task: Task) {
        binding.btnChangeDueDate.setOnClickListener {
            showDatePicker(task)
        }

        binding.btnChangePriority.setOnClickListener {
            showPriorityDialog()
        }

        if (task.isCompleted) {
            binding.btnMarkAsDone.visibility = View.GONE
            binding.btnChangePriority.visibility = View.GONE
            binding.btnChangeDueDate.visibility = View.GONE
        } else {
            binding.btnMarkAsDone.visibility = View.VISIBLE
            binding.btnChangePriority.visibility = View.VISIBLE
            binding.btnChangeDueDate.visibility = View.VISIBLE
        }
        binding.btnMarkAsDone.setOnClickListener { markTaskAsDone() }

        binding.btnDelete.setOnClickListener {
            taskViewModel.deleteTask(task)
            findNavController().popBackStack()
        }
    }

    private fun bindTaskDetails(task: Task) {
        binding.taskTitle.text = task.title
        binding.taskDescription.text = task.description ?: "No description"
        binding.taskPriority.text = "Priority: ${task.priority}"
        binding.taskDueDate.text = "Due Date: ${formatDate(task.dueDate)}"
        binding.taskStatus.text = if (task.isCompleted) "Status: Completed" else "Status: Pending"
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun showDatePicker(task: Task) {
        val initialSelection = task.dueDate

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Due Date")
            .setSelection(initialSelection)
            .setCalendarConstraints(constraints)
            .build()

        picker.show(parentFragmentManager, "DATE_PICKER")

        picker.addOnPositiveButtonClickListener { selectedDate ->
            updateDueDate(selectedDate)
            binding.taskDueDate.text = "Due Date: ${formatDate(selectedDate)}"
        }
    }

    private fun updateDueDate(newDueDate: Long) {
        taskViewModel.getTaskById(taskId).observe(viewLifecycleOwner) { task ->
            task?.let {
                val updatedTask = it.copy(dueDate = newDueDate)
                taskViewModel.updateTask(updatedTask)
            }
        }
    }

    private fun showPriorityDialog() {
        val priorities = arrayOf("Low", "Medium", "High")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Priority")
            .setItems(priorities) { _, which ->
                val selectedPriority = priorities[which]
                updatePriority(selectedPriority)
                binding.taskPriority.text = "Priority: $selectedPriority"
            }
            .show()
    }

    private fun updatePriority(priority: String) {
        taskViewModel.getTaskById(taskId).observe(viewLifecycleOwner) { task ->
            task?.let {
                val updatedTask = it.copy(priority = priority)
                taskViewModel.updateTask(updatedTask)
            }
        }
    }

    private fun markTaskAsDone() {
        taskViewModel.getTaskById(taskId).observe(viewLifecycleOwner) { task ->
            task?.let {
                val updatedTask = it.copy(isCompleted = true)
                taskViewModel.updateTask(updatedTask)
            }
        }
    }
}
