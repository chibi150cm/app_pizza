package com.example.pixzeleria.network

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    val name: String,
    val sprites: PokemonSprites
)

data class PokemonSprites(
    @SerializedName("front_default")
    val frontDefault: String?
)