package com.study.mastersdegree.data

import com.google.gson.annotations.SerializedName

data class MainData(
    val aqi: Int // Air Quality Index (1 - Good, 5 - Hazardous)
)

data class WeatherMain(
    val temp: Double,
    val pressure: Double
)

data class Wind(
    val speed: Double
)

data class Rain(
    @SerializedName("1h") val oneHour: Double?
)

data class Snow(
    @SerializedName("1h") val oneHour: Double?
)