// FragmentStats.kt
package com.study.mastersdegree.ui.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.study.mastersdegree.databinding.FragmentStatsBinding
import com.study.mastersdegree.ui.shared.SharedViewModel

class FragmentStats : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        // Obserwacja wartości w SharedViewModel i wyświetlanie w TextView
        sharedViewModel.globalString.observe(viewLifecycleOwner) { stringValue ->
            binding.textDashboard.text = "String value: $stringValue"
        }
        sharedViewModel.globalDouble.observe(viewLifecycleOwner) { doubleValue ->
            binding.textDashboard.append("\nDouble value: $doubleValue")
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
