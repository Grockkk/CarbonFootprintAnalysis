// SettingsFragment.kt
package com.study.mastersdegree.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.study.mastersdegree.R
import com.study.mastersdegree.databinding.FragmentSettingsBinding
import com.study.mastersdegree.ui.shared.SharedViewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Konfiguracja Spinnera
        val spinner: Spinner = binding.spinnerString
        val options = resources.getStringArray(R.array.spinner_options)
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)

        // Ustawienie Spinnera na zapisanej wartości
        sharedViewModel.globalString.observe(viewLifecycleOwner) { selectedOption ->
            val position = options.indexOf(selectedOption)
            if (position >= 0) spinner.setSelection(position)
        }

        // Ustawienie EditText na zapisanej wartości
        sharedViewModel.globalDouble.observe(viewLifecycleOwner) { doubleValue ->
            binding.editDouble.setText(doubleValue.toString())
        }

        // Obsługa przycisku Save
        binding.buttonSave.setOnClickListener {
            val selectedText = spinner.selectedItem.toString()
            sharedViewModel.setGlobalString(selectedText)

            val doubleInput: EditText = binding.editDouble
            val doubleValue = doubleInput.text.toString().toDoubleOrNull()

            if (doubleValue != null) {
                sharedViewModel.setGlobalDouble(doubleValue)
                Toast.makeText(requireContext(), "Values saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
