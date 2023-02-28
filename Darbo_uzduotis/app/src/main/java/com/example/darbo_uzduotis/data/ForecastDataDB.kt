package com.example.darbo_uzduotis.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ForecastDataDB(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val city: String,
    val date: String,
    val temperatureC: Double,
    val temperatureF: Double,
)
