package com.study.mastersdegree.data

data class AirQualityResponse(
    val list: List<AirQualityData>
)

data class AirQualityData(
    val main: MainData,
    val components: ComponentsData,
    val dt: Long
)