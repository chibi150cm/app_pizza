package com.example.pixzeleria.utils

import com.pixzeleria.R

object ImageUtils {
    fun getPizzaImageResource(imageUrl: String): Int {
        return when (imageUrl) {
            "margherita" -> R.drawable.margherita
            "pepperoni" -> R.drawable.pepperoni
            "quattro_formaggi" -> R.drawable.quattro_formaggi
            "vegetariana" -> R.drawable.vegetariana
            "bbq_chicken" -> R.drawable.bbq_chicken
            "hawaiana" -> R.drawable.hawaiana
            "italiana" -> R.drawable.italiana
            "mexicana" -> R.drawable.mexicana
            else -> R.drawable.pepperoni // La pizza por defecto
        }
    }
}