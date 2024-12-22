// SharedViewModel.kt
package com.study.mastersdegree.ui.shared

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _globalString = MutableLiveData<String>().apply {
        value = prefs.getString("global_string", "Option 1") // domyślna wartość
    }
    val globalString: LiveData<String> get() = _globalString

    private val _globalDouble = MutableLiveData<Double>().apply {
        value = prefs.getFloat("global_double", 0.0f).toDouble() // domyślna wartość
    }
    val globalDouble: LiveData<Double> get() = _globalDouble

    private val _globalGoal = MutableLiveData<Int>().apply {
        value = prefs.getInt("global_goal", 0) // domyślna wartość
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
        prefs.edit().putInt("global_goal", value).apply() // Poprawiony klucz
    }
}
