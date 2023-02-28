package com.example.darbo_uzduotis.network

import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json

object APIClient {
    fun getClient(): Retrofit? {
        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
        return retrofit
    }
}