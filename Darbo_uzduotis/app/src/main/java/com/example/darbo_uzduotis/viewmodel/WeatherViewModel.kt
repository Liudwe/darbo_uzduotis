package com.example.darbo_uzduotis.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.darbo_uzduotis.GlobalData
import com.example.darbo_uzduotis.network.APIClient
import com.example.darbo_uzduotis.network.APIInterface
import com.example.darbo_uzduotis.data.CityWeatherDetailsItem
import com.example.darbo_uzduotis.data.ForecastData
import com.example.darbo_uzduotis.data.ForecastDataDB
import com.example.darbo_uzduotis.data.WeekDaysDetailsItem
import com.example.darbo_uzduotis.database.WeatherForecastDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    application: Application
) :
    AndroidViewModel(application) {

    private val service = APIClient.getClient()?.create(APIInterface::class.java)
    private val weatherForecastDatabase = WeatherForecastDatabase.getInstance(context = application)
    private val _temperatureScale = MutableLiveData<String>()
    val temperatureScale: LiveData<String>
        get() = _temperatureScale

    private val sharedPreferences =
        application.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)

    fun autoUpdate() {
        val isAutoUpdateEnabled = sharedPreferences.getBoolean("autoUpdate", false)
        if (isAutoUpdateEnabled) {
            val lastUpdate = sharedPreferences.getLong("lastUpdate", 0L)
            val currentTime = System.currentTimeMillis()
            val timeSinceLastUpdate = currentTime - lastUpdate
            val oneHourInMs = 60 * 60 * 1000
            if (timeSinceLastUpdate > oneHourInMs) {
                initializeWeatherData()
                val editor = sharedPreferences.edit()
                editor.putLong("lastUpdate", currentTime).apply()
                Log.d("Updated?", "Yes")
            } else {
                Log.d("timeSince: ", timeSinceLastUpdate.toString())
                Log.d("oneHour: ", oneHourInMs.toString())
                Log.d("Updated?", "No")
            }
        }
    }

    fun initializeWeatherData() {
        val startingDate: String = LocalDate.now().minusDays(1).toString()
        val endingDate: String = LocalDate.now().plusDays(14).toString()
        CoroutineScope(Dispatchers.IO).launch {
            weatherForecastDatabase.weatherForecastDao()
                .deleteAll()
        }

        val endpoints = mapOf(
            "Vilnius" to Triple(54.64, 25.07, "Vilnius"),
            "Kaunas" to Triple(54.90, 23.91, "Kaunas"),
            "Klaipėda" to Triple(55.71, 21.14, "Klaipėda"),
            "Šiauliai" to Triple(55.93, 23.32, "Šiauliai")
        )

        for ((_, coords) in endpoints) {
            val call = service?.doGetForecastData(
                coords.first,
                coords.second,
                "temperature_2m",
                startingDate,
                endingDate
            )
            call?.enqueue(object : Callback<ForecastData> {
                override fun onResponse(
                    call: Call<ForecastData>,
                    response: Response<ForecastData>
                ) {
                    val result = response.body()
                    if (result != null) {
                        val weatherForecastList: MutableList<ForecastDataDB> = mutableListOf()
                        for ((hour, temperature) in result.hourly.time.zip(result.hourly.temperature_2m)) {
                            val forecastData: ForecastDataDB = result.let {
                                ForecastDataDB(
                                    id = 0,
                                    city = coords.third,
                                    date = hour,
                                    temperatureC = temperature,
                                    temperatureF = temperature * 1.8 + 32
                                )
                            }
                            weatherForecastList.add(forecastData)
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            weatherForecastDatabase.weatherForecastDao()
                                .saveWeatherData(weatherForecastList)
                        }
                    }
                }

                override fun onFailure(call: Call<ForecastData>, t: Throwable) {
                }
            })
        }

    }

    fun getCityWeekWeather(
        city: String,
        startDate: String,
        endDate: String,
        temperatureScale: String
    ): LiveData<List<WeekDaysDetailsItem>> {
        val forecastData =
            weatherForecastDatabase.weatherForecastDao()
                .getCityTemperature(city, startDate, endDate)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val startDateDateTime = LocalDateTime.parse(startDate, formatter)
        return Transformations.map(forecastData) { forecastList ->
            val weekDaysDetailsList = mutableListOf<WeekDaysDetailsItem>()
            val weekDayDetails = WeekDaysDetailsItem(
                day = "Day",
                day_temperature = "At Day",
                night_temperature = "At Night"
            )
            weekDaysDetailsList.add(weekDayDetails)
            for (i in 1..7) {
                val weekDayDateTime = startDateDateTime.plusDays(i.toLong())
                val nightStartDateTime = weekDayDateTime.withHour(18).withMinute(0).minusDays(1)
                val nightEndDateTime = weekDayDateTime.withHour(6).withMinute(0)
                val dayStartDateTime = weekDayDateTime.withHour(6).withMinute(0)
                val dayEndDateTime = weekDayDateTime.withHour(18).withMinute(0)
                val dayTemperatures = mutableListOf<Double>()
                val nightTemperatures = mutableListOf<Double>()
                forecastList.forEach { forecast ->
                    val forecastDateTime = LocalDateTime.parse(forecast.date, formatter)
                    if (forecastDateTime.isAfter(nightStartDateTime) && forecastDateTime.isBefore(
                            nightEndDateTime
                        )
                    ) {
                        nightTemperatures.add(forecast.temperatureC)
                    } else {
                        dayTemperatures.add(forecast.temperatureC)
                    }
                }
                var dayTemperature =
                    if (dayTemperatures.isNotEmpty()) dayTemperatures.average() else null
                var nightTemperature =
                    if (nightTemperatures.isNotEmpty()) nightTemperatures.average() else null
                if (temperatureScale != "Celsius") {
                    if (dayTemperature != null && nightTemperature != null) {
                        dayTemperature = dayTemperature * 1.8 + 32
                        nightTemperature = nightTemperature * 1.8 + 32
                    }
                    val weekDayDetails = WeekDaysDetailsItem(
                        day = dayStartDateTime.dayOfWeek.toString(),
                        day_temperature = String.format(
                            "%.1f",
                            dayTemperature,
                        ) + "F",
                        night_temperature = String.format(
                            "%.1f",
                            nightTemperature
                        ) + "F"
                    )
                    weekDaysDetailsList.add(weekDayDetails)
                } else {
                    val weekDayDetails = WeekDaysDetailsItem(
                        day = dayStartDateTime.dayOfWeek.toString(),
                        day_temperature = String.format(
                            "%.1f",
                            dayTemperature
                        ) + "C",
                        night_temperature = String.format(
                            "%.1f",
                            nightTemperature
                        ) + "C"
                    )
                    weekDaysDetailsList.add(weekDayDetails)
                }
            }

            weekDaysDetailsList
        }
    }

    fun getCityDayWeather(
        city: String,
        startDate: String,
        endDate: String,
        temperatureScale: String
    ): LiveData<List<WeekDaysDetailsItem>> {
        val forecastData =
            weatherForecastDatabase.weatherForecastDao()
                .getCityTemperature(city, startDate, endDate)
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return Transformations.map(forecastData) { forecastList ->
            forecastList.map { forecast ->
                val dateTime = LocalDateTime.parse(forecast.date, dateTimeFormatter)
                if (temperatureScale == "Celsius") {
                    WeekDaysDetailsItem(
                        day = formatter.format(dateTime),
                        day_temperature = forecast.temperatureC.toString() + "C",
                        night_temperature = ""
                    )
                } else
                    WeekDaysDetailsItem(
                        day = formatter.format(dateTime),
                        day_temperature = String.format("%.1f", forecast.temperatureF) + "F",
                        night_temperature = ""
                    )
            }
        }
    }

    fun getCityWeather(
        currentHour: String,
        temperatureScale: String
    ): LiveData<List<CityWeatherDetailsItem>> {
        val forecastData = weatherForecastDatabase.weatherForecastDao().getCityWeather(currentHour)
        if (temperatureScale == "Celsius") {
            return Transformations.map(forecastData) { forecastList ->
                forecastList.map { forecast ->
                    CityWeatherDetailsItem(
                        city = forecast.city,
                        temperature = forecast.temperatureC.toString() + "C"
                    )
                }
            }
        } else {
            return Transformations.map(forecastData) { forecastList ->
                forecastList.map { forecast ->
                    CityWeatherDetailsItem(
                        city = forecast.city,
                        temperature = String.format("%.1f", forecast.temperatureF) + "F"
                    )
                }
            }
        }
    }

    fun isDatabaseEmpty(): Int {
        return weatherForecastDatabase.weatherForecastDao().getRowCount()
    }

    fun setTemperatureScale(scale: String) {
        _temperatureScale.value = scale
    }
}


