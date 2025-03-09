package com.ddt.app.task_manager.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ddt.app.task_manager.R
import com.ddt.app.task_manager.data.local.TaskDatabase
import com.ddt.app.task_manager.data.models.Task
import com.ddt.app.task_manager.data.repository.RepositoryProvider
import com.ddt.app.task_manager.data.repository.TaskRepository
import com.ddt.app.task_manager.databinding.FragmentCreateBinding
import com.ddt.app.task_manager.domain.NotificationWorker
import com.ddt.app.task_manager.ui.view_model_factory.TaskViewModelFactory
import com.ddt.app.task_manager.ui.viewmodels.TaskViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class CreateFragment : Fragment(R.layout.fragment_create) {

    private lateinit var binding: FragmentCreateBinding

    private var selectedDateInMillis: Long? = null
    private lateinit var taskViewModel: TaskViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateBinding.bind(view)

        val viewModelFactory = TaskViewModelFactory(RepositoryProvider.repository)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        initClickListeners()
    }

    private fun validateAndAddTask() {
        val taskName = binding.tfTaskName.editText?.text.toString().trim()
        val taskDescription = binding.tfTaskDesc.editText?.text.toString().trim()
        val taskPriority = getSelectedChipText(binding.cgPriority)

        var isValid = true

        if (taskName.isEmpty()) {
            binding.tfTaskName.error = "Task name is required"
            isValid = false
        } else {
            binding.tfTaskName.error = null
        }

        if (taskPriority == "No priority selected") {
            Toast.makeText(requireContext(), "Please select a priority", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (selectedDateInMillis == null) {
            Toast.makeText(requireContext(), "Please select a due date", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (isValid) {
            val task = Task(
                title = taskName,
                description = taskDescription,
                priority = taskPriority,
                dueDate = selectedDateInMillis!!
            )

            taskViewModel.addTask(task)

            scheduleDueDateNotification(requireContext(), selectedDateInMillis!!)

            Toast.makeText(requireContext(), "Task added successfully", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun initClickListeners() {
        binding.tvSetDate.setOnClickListener {
            openDatePicker()
        }

        binding.btnAddTask.setOnClickListener {
            validateAndAddTask()
        }
    }

    private fun openDatePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val constraintsBuilder = CalendarConstraints.Builder()
            .setStart(today)
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Due Date")
            .setSelection(selectedDateInMillis ?: today)
            .setCalendarConstraints(constraintsBuilder)
            .build()

        datePicker.show(parentFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            if (selection < today) {
                selectedDateInMillis = today
                Toast.makeText(
                    requireContext(),
                    "Selected date is in the past. Reset to today. Click it to change",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                selectedDateInMillis = selection
            }

            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val selectedDate = sdf.format(Date(selectedDateInMillis!!))
            binding.tvSetDate.text = selectedDate
        }

        datePicker.addOnNegativeButtonClickListener {
            Toast.makeText(requireContext(), "Date selection canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSelectedChipText(chipGroup: com.google.android.material.chip.ChipGroup): String {
        val selectedChipId = chipGroup.checkedChipId
        return if (selectedChipId != View.NO_ID) {
            val selectedChip = chipGroup.findViewById<Chip>(selectedChipId)
            selectedChip.text.toString()
        } else {
            "No priority selected"
        }
    }

    private fun scheduleDueDateNotification(context: Context, dueDateMillis: Long) {
        val delay = dueDateMillis - System.currentTimeMillis()

        if (delay > 0) {
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

}