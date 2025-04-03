// SharedViewModel.kt
package com.study.mastersdegree.ui.shared

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.study.mastersdegree.helpers.EmissionCalculator

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _globalString = MutableLiveData<String>().apply {
        value = prefs.getString("global_string", "Benzyna")
    }
    val globalString: LiveData<String> get() = _globalString

    private val _globalDouble = MutableLiveData<Double>().apply {
        value = prefs.getFloat("global_double", 0.0f).toDouble()
    }
    val globalDouble: LiveData<Double> get() = _globalDouble

    private val _globalConsumption = MutableLiveData<Double>().apply {
        value = prefs.getFloat("global_Consumption", 0.0f).toDouble()
    }
    val globalConsumption: LiveData<Double> get() = _globalConsumption

    private val _globalGoal = MutableLiveData<Int>().apply {
        value = prefs.getInt("global_goal", 0)
    }
    val globalGoal: LiveData<Int> get() = _globalGoal

    fun setGlobalString(value: String) {
        _globalString.value = value
        prefs.edit().putString("global_string", value).apply()
    }

    fun setGlobalDouble(value: Double) {
        _globalDouble.value = value
        prefs.edit().putFloat("global_double", value.toFloat()).apply()
    }

    fun setGlobalGoal(value: Int) {
        _globalGoal.value = value
        prefs.edit().putInt("global_goal", value).apply()
    }

    fun setGlobalConsumption(value: Double) {
        _globalConsumption.value = value
        prefs.edit().putFloat("global_Consumption", value.toFloat()).apply()
    }

    fun calculateEmissionAndCostForDistance(distanceInKm: Double): Pair<Double, Double> {
        val fuelType = _globalString.value ?: "Benzyna"
        val fuelPrice = _globalDouble.value ?: 5.0
        val fuelConsumption = _globalConsumption.value ?: 5.0

        val calculator = EmissionCalculator()
        return calculator.calculateEmissionsAndCost(distanceInKm, fuelType, fuelPrice, fuelConsumption)
    }
}
