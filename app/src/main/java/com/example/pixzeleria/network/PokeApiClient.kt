package com.example.pixzeleria.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {
    @GET("pokemon/{id}")
    suspend fun obtenerPokemon(@Path("id") id: Int): Response<PokemonResponse>
}

object PokeRetrofitClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    val instance: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }
}