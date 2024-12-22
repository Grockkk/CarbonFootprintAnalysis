package com.study.mastersdegree.helpers

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class HealthConnect(private val context: Context) {
    private val healthConnectClient = HealthConnectClient.getOrCreate(context)

    // Metoda 1: Pobiera dystans z dzisiejszego dnia
    suspend fun getDistanceForToday(): Double {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val startOfDay = now.toLocalDate().atStartOfDay(ZoneId.systemDefault())

        val request = AggregateRequest(
            metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(startOfDay.toInstant(), now.toInstant())
        )

        val result = healthConnectClient.aggregate(request)
        return result[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
    }

    // Metoda 2: Pobiera dystanse z ostatnich 7 dni (z podziałem na dni)
    suspend fun getDistanceForLast7Days(): Map<LocalDate, Double> {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val startOfWeek = now.minusDays(6).toLocalDate().atStartOfDay(ZoneId.systemDefault())

        val results = mutableMapOf<LocalDate, Double>()

        for (i in 0..6) {
            val date = startOfWeek.toLocalDate().plusDays(i.toLong())
            val startOfDay = date.atStartOfDay(ZoneId.systemDefault())
            val endOfDay = startOfDay.plus(Duration.ofDays(1))

            val request = AggregateRequest(
                metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay.toInstant(), endOfDay.toInstant())
            )

            val result = healthConnectClient.aggregate(request)
            results[date] = result[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
        }

        return results
    }

    // Metoda 3: Pobiera dystanse z ostatnich 30 dni (z podziałem na dni)
    suspend fun getDistanceForLast30Days(): Map<LocalDate, Double> {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val startOfMonth = now.minusDays(29).toLocalDate().atStartOfDay(ZoneId.systemDefault())

        val results = mutableMapOf<LocalDate, Double>()

        for (i in 0..29) {
            val date = startOfMonth.toLocalDate().plusDays(i.toLong())
            val startOfDay = date.atStartOfDay(ZoneId.systemDefault())
            val endOfDay = startOfDay.plus(Duration.ofDays(1))

            val request = AggregateRequest(
                metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay.toInstant(), endOfDay.toInstant())
            )

            val result = healthConnectClient.aggregate(request)
            results[date] = result[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
        }

        return results
    }
}
