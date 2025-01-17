package com.study.mastersdegree.network

import com.study.mastersdegree.data.AirQualityResponse
import com.study.mastersdegree.data.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityService {
    @GET("air_pollution")
    fun getAirQuality(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Call<AirQualityResponse>

    @GET("weather")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric", // Opcjonalne: domyślnie metryczne jednostki
        @Query("lang") language: String = "pl"   // Opcjonalne: polska wersja językowa
    ): Call<WeatherResponse>
}