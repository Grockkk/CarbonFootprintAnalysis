package com.study.mastersdegree.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.study.mastersdegree.databinding.FragmentStatsBinding
import com.study.mastersdegree.helpers.Charts
import com.study.mastersdegree.helpers.HealthConnect
import com.study.mastersdegree.helpers.EmissionCalculator
import com.study.mastersdegree.ui.shared.SharedViewModel
import kotlinx.coroutines.launch

class FragmentStats : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var healthConnect: HealthConnect
    private val charts = Charts()
    private val emissionCalculator = EmissionCalculator()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var weeklyDistance: Map<String, Double>
    private lateinit var monthlyDistance: Map<String, Double>

    private var currentModeBar: Mode = Mode.DISTANCE
    private var currentModeLine: Mode = Mode.DISTANCE

    enum class Mode {
        DISTANCE, CO2, COST
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        healthConnect = HealthConnect(requireContext())

        lifecycleScope.launch {
            weeklyDistance = healthConnect.getDistanceForLast7Days().mapKeys { it.key.toString() }
            monthlyDistance = healthConnect.getDistanceForLast30Days().mapKeys { it.key.toString() }

            updateBarUI()
            updateLineUI()
        }

        setupClickListeners()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.idDistStatsBar.setOnClickListener {
            currentModeBar = Mode.DISTANCE
            updateBarUI()
        }
        binding.idCo2StatsBar.setOnClickListener {
            currentModeBar = Mode.CO2
            updateBarUI()
        }
        binding.idCostStatsBar.setOnClickListener {
            currentModeBar = Mode.COST
            updateBarUI()
        }

        binding.idDistStatsLine.setOnClickListener {
            currentModeLine = Mode.DISTANCE
            updateLineUI()
        }
        binding.idCo2StatsLine.setOnClickListener {
            currentModeLine = Mode.CO2
            updateLineUI()
        }
        binding.idCostStatsLine.setOnClickListener {
            currentModeLine = Mode.COST
            updateLineUI()
        }
    }

    private fun updateLineUI() {
        sharedViewModel.globalString.observe(viewLifecycleOwner) { fuelType ->
            sharedViewModel.globalDouble.observe(viewLifecycleOwner) { fuelPrice ->
                sharedViewModel.globalConsumption.observe(viewLifecycleOwner) { consumption ->
                    val monthlyData = convertData(monthlyDistance, fuelType, fuelPrice,consumption, currentModeLine)


                    val maxEntryLine = monthlyData.maxByOrNull { it.value }
                    val minEntryLine = monthlyData.minByOrNull { it.value }
                    val sumDistanceLine = monthlyData.values.sum()

                    binding.maxLineValue.text = formatValue(maxEntryLine?.value, currentModeLine)
                    binding.maxLineDate.text = maxEntryLine?.key ?: "—"

                    binding.minLineValue.text = formatValue(minEntryLine?.value, currentModeLine)
                    binding.minLineDate.text = minEntryLine?.key ?: "—"

                    binding.sumLineValue.text = formatValue(sumDistanceLine, currentModeLine)

                    charts.createLineChart(requireContext(), binding.monthlyLineChart, monthlyData, getChartTitle(currentModeLine))
                }
            }
        }
    }

    private fun updateBarUI() {
        sharedViewModel.globalString.observe(viewLifecycleOwner) { fuelType ->
            sharedViewModel.globalDouble.observe(viewLifecycleOwner) { fuelPrice ->
                sharedViewModel.globalConsumption.observe(viewLifecycleOwner) { consumption ->
                    val weeklyData = convertData(
                        weeklyDistance,
                        fuelType,
                        fuelPrice,
                        consumption,
                        currentModeBar
                    )

                    val maxEntryBar = weeklyData.maxByOrNull { it.value }
                    val minEntryBar = weeklyData.minByOrNull { it.value }
                    val sumDistanceBar = weeklyData.values.sum()

                    binding.maxBarValue.text = formatValue(maxEntryBar?.value, currentModeBar)
                    binding.maxBarDate.text = maxEntryBar?.key ?: "—"

                    binding.minBarValue.text = formatValue(minEntryBar?.value, currentModeBar)
                    binding.minBarDate.text = minEntryBar?.key ?: "—"

                    binding.sumBarValue.text = formatValue(sumDistanceBar, currentModeBar)

                    charts.createBarChart(
                        requireContext(),
                        binding.weeklyBarChart,
                        weeklyData,
                        getChartTitle(currentModeBar)
                    )
                }
            }
        }
    }

    private fun convertData(distanceData: Map<String, Double>, fuelType: String, fuelPrice: Double, consumption: Double, mode: Mode): Map<String, Double> {
        return when (mode) {
            Mode.DISTANCE -> distanceData
            Mode.CO2 -> distanceData.mapValues {
                sharedViewModel.calculateEmissionAndCostForDistance(it.value / 1000).first
            }
            Mode.COST -> distanceData.mapValues {
                sharedViewModel.calculateEmissionAndCostForDistance(it.value / 1000).second
            }
        }
    }

    private fun formatValue(value: Double?, mode: Mode): String {
        return when (mode) {
            Mode.DISTANCE -> String.format("%.2f m", value ?: 0.0)
            Mode.CO2 -> String.format("%.2f kg", value ?: 0.0)
            Mode.COST -> String.format("%.2f zł", value ?: 0.0)
        }
    }

    private fun getChartTitle(mode: Mode): String {
        return when (mode) {
            Mode.DISTANCE -> "Dystans [m]"
            Mode.CO2 -> "CO₂ [kg]"
            Mode.COST -> "Koszt [zł]"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
