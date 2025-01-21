package com.study.mastersdegree.data

data class AirQualityResponse(
    val list: List<AirQualityData>
)

data class AirQualityData(
    val main: MainData,
    val components: ComponentsData,
    val dt: Long
)

data class WeatherResponse(
    val weather: List<WeatherDescription>,
    val main: WeatherMain,
    val wind: Wind,
    val rain: Rain?,
    val snow: Snow?
)

data class WeatherDescription(
    val description: String
)

