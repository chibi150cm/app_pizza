package com.example.pixzeleria.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pizza(
    val id: String,
    val nombrePizza: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val ingredientes: List<String> = emptyList(),
    val disponible: Boolean = true
) : Parcelable

@Parcelize
data class Carrito(
    val pizza: Pizza,
    var cantidad: Int = 1,
    val custom: String = ""
) : Parcelable {
    val subtotal: Double
        get() = pizza.precio * cantidad
}

@Parcelize
data class Pedido(
    val id: String,
    val items: List<Carrito>,
    val nombreCliente: String,
    val numeroCliente: String,
    val emailCliente: String,
    val direccionDeli: String,
    val instruccionEspecial: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val estado: pedidoStatus = pedidoStatus.PENDIENTE,
    val total: Double
) : Parcelable

enum class pedidoStatus {
    PENDIENTE, CONFIRMADO, PREPARANDO, ENVIADO, COMPLETO, CANCELADO
}

data class User(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = ""
)