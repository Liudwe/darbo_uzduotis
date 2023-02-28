package com.example.darbo_uzduotis.network

import com.example.darbo_uzduotis.data.ForecastData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIInterface {
    /*    @GET("/v1/forecast?latitude=54.64&longitude=25.07&hourly=temperature_2m&start_date=2023-02-17&end_date=2023-02-24")
        fun doGetForecastDataVilnius(): Call<ForecastData>*/
    @GET("/v1/forecast?")
    fun doGetForecastData(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Call<ForecastData>
}