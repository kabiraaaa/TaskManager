package com.ddt.app.task_manager.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ddt.app.task_manager.R
import com.ddt.app.task_manager.databinding.FragmentCreateBinding

class CreateFragment : Fragment(R.layout.fragment_create) {

    private lateinit var binding: FragmentCreateBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateBinding.bind(view)
    }
}