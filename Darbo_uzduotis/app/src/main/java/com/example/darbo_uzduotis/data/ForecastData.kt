package com.example.darbo_uzduotis.data

import kotlinx.serialization.Serializable

@Serializable
data class ForecastData(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val hourly_units: Units,
    val hourly:TemperatureData
)

@Serializable
data class Units(
    val time: String,
    val temperature_2m: String
)

@Serializable
data class TemperatureData(
    val time: Array<String>,
    val temperature_2m: Array<Double>
)
