package com.example.pixzeleria.utils

import com.pixzeleria.R

object ImageUtils {
    fun getPizzaImageResource(imageUrl: String): Int {
        return when (imageUrl) {
            "margherita","Marguerita Clásica" -> R.drawable.margherita
            "pepperoni","Pepperoni Suprema" -> R.drawable.pepperoni
            "quattro_formaggi","Cuatro Quesos" -> R.drawable.quattro_formaggi
            "vegetariana","Vegetariana Deluxe" -> R.drawable.vegetariana
            "bbq_chicken","BBQ Chicken" -> R.drawable.bbq_chicken
            "hawaiana","Hawaiana" -> R.drawable.hawaiana
            "italiana","Italiana" -> R.drawable.italiana
            "mexicana","Mexicana Picante" -> R.drawable.mexicana
            else -> R.drawable.pepperoni // La pizza por defecto
        }
    }
}