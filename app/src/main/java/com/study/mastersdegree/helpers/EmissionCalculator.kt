package com.study.mastersdegree.helpers

class EmissionCalculator {

    fun calculateEmissionsAndCost(
        distanceInKm: Double,
        fuelType: String,
        fuelPricePerLiter: Double
    ): Pair<Double, Double> {
        // Stałe dla różnych paliw
        val emissionsPerLiter = when (fuelType) {
            "Benzyna" -> 2.31 // kg CO2 per liter
            "Olej napędowy" -> 1.51
            "LPG" -> 2.68
            else -> throw IllegalArgumentException("Nieznany rodzaj paliwa: $fuelType")
        }

        val averageConsumptionPer100Km = when (fuelType) {
            "Benzyna" -> 8.0 // liters per 100 km
            "Olej napędowy" -> 6.0
            "LPG" -> 11.0
            else -> throw IllegalArgumentException("Nieznany rodzaj paliwa: $fuelType")
        }

        // Obliczanie
        val fuelConsumed = (distanceInKm / 100) * averageConsumptionPer100Km
        val totalEmissions = fuelConsumed * emissionsPerLiter
        val totalCost = fuelConsumed * fuelPricePerLiter
        val roundedCost = totalCost
        return Pair(totalEmissions, roundedCost)
    }
}