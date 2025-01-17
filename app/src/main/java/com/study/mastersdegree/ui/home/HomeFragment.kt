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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.study.mastersdegree.R
import com.study.mastersdegree.data.AirQualityResponse
import com.study.mastersdegree.data.WeatherResponse
import com.study.mastersdegree.databinding.FragmentHomeBinding
import com.study.mastersdegree.helpers.HealthConnect
import com.study.mastersdegree.network.RetrofitClient
import com.study.mastersdegree.ui.shared.SharedViewModel
import kotlinx.coroutines.launch
import android.util.Base64

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val encodedApiKey = "NGY5NmU5Yjk4MWMyYzk1NGJmNmUxY2ViNTFmNmI1ODU="
    private val apiKey = decodeApiKey(encodedApiKey)
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Obserwuj dane globalne i aktualizuj interfejs
        fetchDailyDistance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Pobierz lokalizację użytkownika i zaktualizuj dane
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchDailyDistance()
                fetchAirQuality(location.latitude, location.longitude)
                fetchWeather(location.latitude, location.longitude)
            } else {
                binding.textAirQuality.text = "Nie udało się uzyskać lokalizacji"
                binding.textWeather.text = "Nie udało się uzyskać danych o pogodzie"
            }
        }

        return binding.root
    }

    private fun fetchDailyDistance() {
        val healthConnect = HealthConnect(requireContext())
        lifecycleScope.launch {
            val distanceInMeters = healthConnect.getDistanceForToday()
            binding.circularProgressBar.progress = distanceInMeters.toFloat()
            sharedViewModel.globalGoal.observe(viewLifecycleOwner) { goal ->
                val remainingDistance = goal - distanceInMeters
                binding.circularProgressBar.progressMax = goal.toFloat()
                if(remainingDistance <=0){
                    binding.textRemainingDistance.text = "0 m"
                }
                else{
                    binding.textRemainingDistance.text =
                        getString(R.string.remaining_distance, remainingDistance)
                }
            }

            val distanceInKm = distanceInMeters / 1000.0

            val (emissions, cost) = sharedViewModel.calculateEmissionAndCostForDistance(distanceInKm)

            binding.textDailyDistance.text =
                getString(R.string.daily_distance, distanceInKm)
            binding.textDailyEmissions.text =
                getString(R.string.daily_emissions, emissions)
            binding.textDailyCost.text =
                getString(R.string.daily_cost, cost)
        }
    }

    private fun fetchAirQuality(latitude: Double, longitude: Double) {
        val service = RetrofitClient.instance
        service.getAirQuality(latitude, longitude, apiKey).enqueue(object : retrofit2.Callback<AirQualityResponse> {
            override fun onResponse(
                call: retrofit2.Call<AirQualityResponse>,
                response: retrofit2.Response<AirQualityResponse>
            ) {
                if (response.isSuccessful) {
                    val airQualityData = response.body()?.list?.firstOrNull()
                    if (airQualityData != null) {
                        val aqi = airQualityData.main.aqi
                        updateAirQualityUI(aqi)
                    } else {
                        binding.textAirQuality.text = "Brak danych o jakości powietrza"
                    }
                } else {
                    binding.textAirQuality.text = "Nie udało się pobrać jakości powietrza"
                }
            }

            override fun onFailure(call: retrofit2.Call<AirQualityResponse>, t: Throwable) {
                binding.textAirQuality.text = "Błąd: ${t.message}"
            }
        })
    }

    private fun fetchWeather(latitude: Double, longitude: Double) {
        val service = RetrofitClient.instance
        service.getWeather(latitude, longitude, apiKey).enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onResponse(
                call: retrofit2.Call<WeatherResponse>,
                response: retrofit2.Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    if (weatherData != null) {
                        val description = weatherData.weather.firstOrNull()?.description ?: "Brak opisu"
                        val temperature = weatherData.main.temp
                        updateWeatherUI(description, temperature)
                    } else {
                        binding.textWeather.text = "Brak danych o pogodzie"
                    }
                } else {
                    binding.textWeather.text = "Nie udało się pobrać danych o pogodzie"
                }
            }

            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                binding.textWeather.text = "Błąd: ${t.message}"
            }
        })
    }

    private val weatherIconMap = mapOf(
        "bezchmurnie" to R.drawable.sun,
        "zachmurzenie" to R.drawable.cloudy,
        "scattered clouds" to R.drawable.cloud,
        "broken clouds" to R.drawable.cloud,
        "shower rain" to R.drawable.rain,
        "opady deszczu" to R.drawable.rain,
        "burza" to R.drawable.thunderstorm,
        "opady śniegu" to R.drawable.snowy,
        "zamglenia" to R.drawable.fog
    )

    private fun updateWeatherUI(description: String, temperature: Double) {
        val weatherText = "$description, Temp: ${temperature}°C"
        binding.textWeather.text = weatherText

        val iconResId = weatherIconMap.entries.firstOrNull { description.contains(it.key, ignoreCase = true) }?.value
            ?: R.drawable.cloud // Domyślna ikona

        binding.weatherImage.setImageResource(iconResId)
    }

    private fun updateAirQualityUI(aqi: Int) {
        val airQualityText: String
        val iconResId: Int
        val colorResId: Int

        when (aqi) {
            1 -> { // Dobre
                airQualityText = "Dobre"
                iconResId = R.drawable.happy_face
                colorResId = R.color.face_dark_green
            }
            2 -> { // Umiarkowane
                airQualityText = "Umiarkowane"
                iconResId = R.drawable.happy_face
                colorResId = R.color.face_light_green
            }
            3 -> { // Niezdrowe dla wrażliwych grup
                airQualityText = "Niezdrowe dla wrażliwych grup"
                iconResId = R.drawable.neutral_face
                colorResId = R.color.face_yellow
            }
            4 -> { // Niezdrowe
                airQualityText = "Niezdrowe"
                iconResId = R.drawable.sad_face
                colorResId = R.color.face_red
            }
            5 -> { // Bardzo niezdrowe
                airQualityText = "Bardzo niezdrowe"
                iconResId = R.drawable.sad_face
                colorResId = R.color.face_purple
            }
            else -> { // Nieznane
                airQualityText = "Nieznane"
                iconResId = R.drawable.neutral_face
                colorResId = R.color.last_color
            }
        }

        binding.textAirQuality.text = "Jakość powietrza: $airQualityText"
        binding.faceImage.setImageResource(iconResId)
        binding.faceImage.setColorFilter(
            requireContext().getColor(colorResId),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    fun decodeApiKey(encodedApiKey: String): String {
        val decodedBytes = Base64.decode(encodedApiKey, Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}