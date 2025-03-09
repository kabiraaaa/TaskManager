package com.ddt.app.task_manager.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddt.app.task_manager.R
import com.ddt.app.task_manager.data.local.TaskDatabase
import com.ddt.app.task_manager.data.models.Task
import com.ddt.app.task_manager.data.repository.RepositoryProvider
import com.ddt.app.task_manager.data.repository.TaskRepository
import com.ddt.app.task_manager.databinding.FragmentHomeBinding
import com.ddt.app.task_manager.ui.adapters.TaskAdapter
import com.ddt.app.task_manager.ui.view_model_factory.TaskViewModelFactory
import com.ddt.app.task_manager.ui.viewmodels.TaskViewModel
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter

    private var currentSortOption = "Due Date"
    private var currentFilterOption = "All"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        val viewModelFactory = TaskViewModelFactory(RepositoryProvider.repository)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        setupRecyclerView()
        setupSorting()
        setupFiltering()
        initClickListeners()

        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val filteredTasks = filterTasks(tasks, currentFilterOption)
            val sortedTasks = sortTasks(filteredTasks, currentSortOption)
            taskAdapter.submitList(sortedTasks)

            binding.emptyView.visibility = if (sortedTasks.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun initClickListeners() {
        val navController = findNavController()

        val options = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()

        binding.ftbAddTask.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.bounce))
            navController.navigate(R.id.action_homeFragment_to_createFragment, null, options)
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter { task ->
            handleTaskClick(task)
        }
        binding.rvTaskList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
            setupSwipeToDelete(this)
        }
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val deletedTask = taskAdapter.currentList[position]

                taskViewModel.deleteTask(deletedTask)

                Snackbar.make(binding.root, "Task deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        taskViewModel.addTask(deletedTask)
                    }
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.my_light_primary))
                    .show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    private fun setupSorting() {
        val sortOptions = resources.getStringArray(R.array.sort_options)
        val sortAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSortOptions.adapter = sortAdapter

        binding.spinnerSortOptions.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentSortOption = sortOptions[position]
                    taskViewModel.allTasks.value?.let { tasks ->
                        val filteredTasks = filterTasks(tasks, currentFilterOption)
                        val sortedTasks = sortTasks(filteredTasks, currentSortOption)
                        taskAdapter.submitList(sortedTasks)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setupFiltering() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilterOption = when {
                checkedIds.contains(R.id.chipCompleted) -> "Completed"
                checkedIds.contains(R.id.chipPending) -> "Pending"
                else -> "All"
            }

            taskViewModel.allTasks.value?.let { tasks ->
                val filteredTasks = filterTasks(tasks, currentFilterOption)
                val sortedTasks = sortTasks(filteredTasks, currentSortOption)
                taskAdapter.submitList(sortedTasks)
            }
        }
    }

    private fun filterTasks(tasks: List<Task>, filterOption: String): List<Task> {
        return when (filterOption) {
            "Completed" -> tasks.filter { it.isCompleted }
            "Pending" -> tasks.filter { !it.isCompleted }
            else -> tasks
        }
    }

    private fun sortTasks(tasks: List<Task>, sortOption: String): List<Task> {
        return when (sortOption) {
            "Priority" -> tasks.sortedBy { it.priority }
            "Due Date" -> tasks.sortedBy { it.dueDate }
            "Alphabetically" -> tasks.sortedBy { it.title }
            else -> tasks
        }
    }

    private fun handleTaskClick(task: Task) {
        val action = HomeFragmentDirections.actionHomeFragmentToTaskViewFragment(task.id)

        findNavController().navigate(
            action, NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build()
        )
    }
}