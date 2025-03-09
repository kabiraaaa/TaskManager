package com.ddt.app.task_manager.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.ddt.app.task_manager.R
import com.ddt.app.task_manager.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences(
            "app_prefs",
            Context.MODE_PRIVATE
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        applySavedTheme()

        binding.btnChangeColor.setOnClickListener {
            showColorPicker()
        }
    }

    private fun showColorPicker() {
        val colors = arrayOf("Red", "Blue", "Green", "Purple")
        val colorValues = arrayOf("#FF0000", "#0000FF", "#008000", "#6200EE")

        AlertDialog.Builder(requireContext())
            .setTitle("Choose Primary Color")
            .setItems(colors) { _, which ->
                saveColorPreference(colorValues[which])
                applyTheme(colorValues[which])
            }
            .show()
    }

    private fun saveColorPreference(color: String) {
        sharedPreferences.edit().putString("primary_color", color).apply()
    }

    private fun applyTheme(colorHex: String) {
        val colorInt = Color.parseColor(colorHex)
        requireActivity().window.statusBarColor = colorInt
    }

    private fun applySavedTheme() {
        val savedColor = sharedPreferences.getString("primary_color", "#6200EE")
        applyTheme(savedColor ?: "#6200EE")
    }
}
