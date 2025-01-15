package com.study.mastersdegree.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.study.mastersdegree.R
import com.study.mastersdegree.databinding.FragmentHomeBinding
import com.study.mastersdegree.helpers.HealthConnect
import com.study.mastersdegree.ui.shared.SharedViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Obserwuj dane globalne i aktualizuj interfejs
        observeGlobalData()

        // Pobierz dystans dzienny i zaktualizuj dane
        fetchDailyDistance()

        return binding.root
    }

    private fun observeGlobalData() {
        sharedViewModel.globalGoal.observe(viewLifecycleOwner) { goal ->
            val dailyDistance = binding.textDailyDistance.text.toString().toDoubleOrNull() ?: 0.0
            val remainingDistance = goal - dailyDistance
            binding.textRemainingDistance.text =
                getString(R.string.remaining_distance, remainingDistance)
        }
    }

    private fun fetchDailyDistance() {
        val healthConnect = HealthConnect(requireContext())
        lifecycleScope.launch {
            val distanceInMeters = healthConnect.getDistanceForToday()
            val distanceInKm = distanceInMeters / 1000.0

            // Oblicz emisję i koszt
            val (emissions, cost) = sharedViewModel.calculateEmissionAndCostForDistance(distanceInKm)

            // Zaktualizuj interfejs
            binding.textDailyDistance.text =
                getString(R.string.daily_distance, distanceInMeters)
            binding.textDailyEmissions.text =
                getString(R.string.daily_emissions, emissions)
            binding.textDailyCost.text =
                getString(R.string.daily_cost, cost)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}