package com.study.mastersdegree.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.study.mastersdegree.GoogleLoginActivity
import com.study.mastersdegree.R
import com.study.mastersdegree.databinding.FragmentSettingsBinding
import com.study.mastersdegree.ui.shared.SharedViewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Konfiguracja Google Sign-In Client
        googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)

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

        // Ustawienie EditText na zapisanej wartości
        sharedViewModel.globalGoal.observe(viewLifecycleOwner) { goalValue ->
            binding.editGoal.setText(goalValue.toString())
        }

        // Obsługa przycisku Save
        binding.buttonSave.setOnClickListener {
            val selectedText = spinner.selectedItem.toString()
            sharedViewModel.setGlobalString(selectedText)

            val doubleInput: EditText = binding.editDouble
            val doubleValue = doubleInput.text.toString().toDoubleOrNull()

            val goalInput: EditText = binding.editGoal
            val goalValue = goalInput.text.toString().toIntOrNull()

            if (doubleValue != null && goalValue != null) {
                sharedViewModel.setGlobalDouble(doubleValue)
                Toast.makeText(requireContext(), "Values saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter a valid number $goalValue", Toast.LENGTH_SHORT).show()
            }
        }

        // Obsługa przycisku Logout
        binding.buttonLogout.setOnClickListener {
            signOut()
        }

        return binding.root
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(requireContext(), "Successfully signed out", Toast.LENGTH_SHORT).show()
            // Przekierowanie do ekranu logowania
            val intent = Intent(requireContext(), GoogleLoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
