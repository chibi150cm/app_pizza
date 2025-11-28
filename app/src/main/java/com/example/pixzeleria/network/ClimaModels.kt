package com.example.pixzeleria.network

import com.google.gson.annotations.SerializedName

data class ClimaResponse(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather
)

data class CurrentWeather(
    val temperature: Double,
    val weathercode: Int
)