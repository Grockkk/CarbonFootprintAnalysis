// SharedViewModel.kt
package com.study.mastersdegree.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    // Zmienna String
    private val _globalString = MutableLiveData<String>()
    val globalString: LiveData<String> get() = _globalString

    // Zmienna Double
    private val _globalDouble = MutableLiveData<Double>()
    val globalDouble: LiveData<Double> get() = _globalDouble

    // Metody do ustawiania wartości zmiennych
    fun setGlobalString(value: String) {
        _globalString.value = value
    }

    fun setGlobalDouble(value: Double) {
        _globalDouble.value = value
    }
}
