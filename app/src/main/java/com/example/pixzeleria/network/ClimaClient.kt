package com.example.pixzeleria.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApiService {
    // Pedimos clima actual
    @GET("forecast?current_weather=true")
    suspend fun obtenerClima(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double
    ): Response<ClimaResponse>
}

object ClimaRetrofitClient {
    private const val BASE_URL = "https://api.open-meteo.com/v1/"

    val instance: ClimaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClimaApiService::class.java)
    }
}