package com.example.darbo_uzduotis.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.darbo_uzduotis.data.ForecastDataDB

@Dao
interface WeatherForecastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveWeatherData(forecastDataDB: MutableList<ForecastDataDB>)

    @Query("SELECT * FROM forecastDataDB")
    fun getAll(): LiveData<ForecastDataDB>

    @Query("DELETE FROM forecastDataDB")
    fun deleteAll()

    @Query("SELECT * FROM forecastDataDB WHERE date = :currentDate ORDER BY city ASC")
    fun getCityWeather(currentDate: String): LiveData<List<ForecastDataDB>>

    @Query("SELECT * FROM forecastDataDB WHERE city = :city AND date BETWEEN :startDate AND :endDate")
    fun getCityTemperature(city: String, startDate: String, endDate: String): LiveData<List<ForecastDataDB>>

    @Query("SELECT COUNT(*) FROM forecastDataDB")
    fun getRowCount(): Int
}