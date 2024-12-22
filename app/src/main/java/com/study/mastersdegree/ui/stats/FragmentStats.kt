package com.study.mastersdegree.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.study.mastersdegree.databinding.FragmentStatsBinding
import com.study.mastersdegree.helpers.Charts
import com.study.mastersdegree.helpers.HealthConnect
import kotlinx.coroutines.launch

class FragmentStats : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var healthConnect: HealthConnect
    private val charts = Charts()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        healthConnect = HealthConnect(requireContext())

        // Pobierz dane dystansowe i zaktualizuj wykresy
        lifecycleScope.launch {
            val weeklyDistance = healthConnect.getDistanceForLast7Days()
            val monthlyDistance = healthConnect.getDistanceForLast30Days()

            // Rysowanie wykresów
            charts.createBarChart(
                requireContext(),
                binding.weeklyBarChart,
                weeklyDistance.mapKeys { it.key.toString() },
                "Dystans w tygodniu"
            )
            charts.createLineChart(
                requireContext(),
                binding.monthlyLineChart,
                monthlyDistance.mapKeys { it.key.toString() },
                "Dystans w miesiącu"
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
