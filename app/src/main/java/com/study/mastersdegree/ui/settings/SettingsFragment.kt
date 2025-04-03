package com.study.mastersdegree.ui.settings

import android.content.Intent
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

        googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)

        val spinner: Spinner = binding.spinnerString
        val options = resources.getStringArray(R.array.spinner_options)
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)

        sharedViewModel.globalString.observe(viewLifecycleOwner) { selectedOption ->
            val position = options.indexOf(selectedOption)
            if (position >= 0) spinner.setSelection(position)
        }

        sharedViewModel.globalDouble.observe(viewLifecycleOwner) { doubleValue ->
            binding.kosztPaliwa.setText("Koszt paliwa (obecnie $doubleValue zł):")
            binding.editDouble.setText("")
        }

        sharedViewModel.globalConsumption.observe(viewLifecycleOwner) { consumptionValue ->
            binding.srednieSpalanie.setText("Średnie spalanie na 100km (obecnie $consumptionValue l):")
            binding.editSpalanie.setText("")
        }

        sharedViewModel.globalGoal.observe(viewLifecycleOwner) { goalValue ->
            binding.dziennyCel.setText("Cel dziennego dystansu (obecnie $goalValue m):")
            binding.editGoal.setText("")
        }

        binding.buttonSave.setOnClickListener {
            val selectedText = spinner.selectedItem.toString()
            var informationString = "Wartości: "
            informationString += "Typ paliwa: $selectedText, "
            sharedViewModel.setGlobalString(selectedText)

            val doubleInput: EditText = binding.editDouble
            val doubleValue = doubleInput.text.toString().toDoubleOrNull()

            val consumptionInput: EditText = binding.editSpalanie
            val consumptionValue = consumptionInput.text.toString().toDoubleOrNull()

            val goalInput: EditText = binding.editGoal
            val goalValue = goalInput.text.toString().toIntOrNull()


            if (doubleValue != null) {
                sharedViewModel.setGlobalDouble(doubleValue)
                informationString += "Koszt paliwa: $doubleValue, "
            }
            if (goalValue != null) {
                sharedViewModel.setGlobalGoal(goalValue)
                informationString += "Cel dystansu: $goalValue" + "m, "
            }
            if (consumptionValue != null) {
                sharedViewModel.setGlobalConsumption(consumptionValue)
                informationString += "Średnie spalanie: $consumptionValue, "
            }
            informationString += "zostały zmienione"
            Toast.makeText(requireContext(), informationString, Toast.LENGTH_LONG).show()
        }

        binding.buttonLogout.setOnClickListener {
            signOut()
        }

        return binding.root
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(requireContext(), "Pomyślnie wylogowano", Toast.LENGTH_SHORT).show()
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
