package com.ddt.app.task_manager.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ddt.app.task_manager.R
import com.ddt.app.task_manager.databinding.FragmentMyWorkBinding

class MyWorkFragment : Fragment(R.layout.fragment_my_work) {

    private lateinit var binding: FragmentMyWorkBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyWorkBinding.bind(view)
    }
}