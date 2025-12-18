package com.example.pixzeleria.network

import com.google.gson.annotations.SerializedName
import com.example.pixzeleria.data.model.Pizza

data class PizzaResponse(
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    val descripcion: String?,
    val precio: Double,

    @SerializedName("imagenUrl")
    val imagenUrl: String?,

    val categoria: String?,
    val disponible: Boolean
)

data class PedidoResponse(
    val id: Long,
    val total: Double,
    val estado: String,
    val fecha: String? = null,
    val direccion: String? = null,
    val nombreCliente: String? = null,
    val items: List<ItemPedidoResponse> = emptyList()
)

data class ItemPedidoResponse(
    val nombrePizza: String,
    val cantidad: Int,
    val precio: Double
)

data class PedidoRequest(
    val clienteId: Long,
    val items: List<DetallePedidoRequest>
)

data class DetallePedidoRequest(
    val nombrePizza: String,
    val pizzaId: Long,
    val precio: Double,
    val cantidad: Int
)

fun PizzaResponse.toUiModel(): Pizza {
    return Pizza(
        id = this.id.toString(),
        nombrePizza = this.nombre,
        descripcion = this.descripcion ?: "",
        precio = this.precio,
        imagenUrl = this.imagenUrl ?: "",
        categoria = this.categoria ?: "General",
        ingredientes = emptyList(),
    )
}