package com.example.pixzeleria.network

import com.example.pixzeleria.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("pizzas")
    suspend fun obtenerPizzas(): List<PizzaResponse>

    @POST("pedidos")
    suspend fun crearPedido(@Body pedido: PedidoRequest): Response<PedidoResponse>

    @GET("pedidos/cliente/{id}")
    suspend fun obtenerPedidosPorCliente(@Path("id") id: Long): List<PedidoResponse>

    @DELETE("pedidos/{id}")
    suspend fun eliminarPedido(@Path("id") id: Long): Response<Void>

    @POST("clientes/guardar")
    suspend fun guardarPerfil(@Body cliente: User): Response<User>

    @DELETE("clientes/0")
    suspend fun eliminarCliente(): Response<Void>

    @POST("clientes/login")
    suspend fun login(@Body credenciales: Map<String, String>): Response<User>

    @POST("clientes/registro")
    suspend fun registrar(@Body usuario: User): Response<User>
}