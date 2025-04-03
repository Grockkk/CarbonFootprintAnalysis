package com.study.mastersdegree.helpers

class EmissionCalculator {

    fun calculateEmissionsAndCost(
        distanceInKm: Double,
        fuelType: String,
        fuelPricePerLiter: Double,
        ConsumptionPer100Km: Double
    ): Pair<Double, Double> {
        val emissionsPerLiter = when (fuelType) {
            "Benzyna" -> 2.31 // kg CO2 per liter
            "Olej napędowy" -> 1.51
            "LPG" -> 2.68
            else -> throw IllegalArgumentException("Nieznany rodzaj paliwa: $fuelType")
        }

        val fuelConsumed = (distanceInKm / 100) * ConsumptionPer100Km
        val totalEmissions = fuelConsumed * emissionsPerLiter
        val totalCost = fuelConsumed * fuelPricePerLiter
        val roundedCost = totalCost
        return Pair(totalEmissions, roundedCost)
    }
}