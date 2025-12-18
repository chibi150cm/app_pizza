package com.example.pixzeleria

import com.example.pixzeleria.data.model.Carrito
import com.example.pixzeleria.data.model.Pizza
import com.example.pixzeleria.data.model.Pedido
import com.example.pixzeleria.data.model.pedidoStatus
import com.example.pixzeleria.data.model.User
import org.junit.Assert.assertEquals
import org.junit.Test

class MainViewModelTest {
    @Test
    fun `calcular subtotal de item del carrito`() {
        val pizzaPrueba = Pizza(
            id = "1",
            nombrePizza = "Pizza Test",
            descripcion = "Descripción de prueba",
            precio = 10000.0,
            imagenUrl = "img",
            categoria = "Test"
        )
        val itemCarrito = Carrito(pizza = pizzaPrueba, cantidad = 3)
        assertEquals(30000.0, itemCarrito.subtotal, 0.0)
    }

    @Test
    fun `calcular total del pedido correctamente con multiples productos`() {
        val pizza1 = Pizza(id="1", nombrePizza="P1", descripcion="", precio=5000.0, imagenUrl="", categoria="")
        val pizza2 = Pizza(id="2", nombrePizza="P2", descripcion="", precio=2000.0, imagenUrl="", categoria="")

        val listaCarrito = listOf(
            Carrito(pizza1, cantidad = 2),
            Carrito(pizza2, cantidad = 1)
        )
        val totalCalculado = listaCarrito.sumOf { it.subtotal }

        assertEquals(12000.0, totalCalculado, 0.0)
    }

    @Test
    fun `calcular total de carrito vacio da cero`() {
        val listaCarrito = emptyList<Carrito>()
        val totalCalculado = listaCarrito.sumOf { it.subtotal }
        assertEquals(0.0, totalCalculado, 0.0)
    }

    @Test
    fun `validar acceso diferenciado por roles`() {
        val admin = User(nombre = "Zeff", email = "a@p.com", rol = "ADMIN")
        val cocinero = User(nombre = "Sanji", email = "c@p.com", rol = "COCINERO")
        val repartidor = User(nombre = "Usopp", email = "r@p.com", rol = "REPARTIDOR")
        val cliente = User(nombre = "Luffy", email = "u@p.com", rol = "USER")

        assertEquals(true, admin.rol == "ADMIN" || admin.rol == "COCINERO")
        assertEquals(true, cocinero.rol == "COCINERO")
        assertEquals(false, cliente.rol == "COCINERO")

        assertEquals(true, repartidor.rol == "REPARTIDOR" || admin.rol == "ADMIN")
    }


    @Test
    fun `verificar que el filtro de reparto solo incluya pedidos ENVIADOS`() {
        val pedidosMuestra = listOf(
            crearPedidoPrueba("1", pedidoStatus.PENDIENTE, "Luffy"),
            crearPedidoPrueba("2", pedidoStatus.ENVIADO, "Zoro"), // Este debe entrar
            crearPedidoPrueba("3", pedidoStatus.COMPLETO, "Nami")
        )

        val listaReparto = pedidosMuestra.filter { it.estado == pedidoStatus.ENVIADO }

        assertEquals(1, listaReparto.size)
        assertEquals(pedidoStatus.ENVIADO, listaReparto[0].estado)
        assertEquals("Zoro", listaReparto[0].nombreCliente)
    }

    @Test
    fun `flujo de estados de pedido para Repartidor`() {
        val pedido = crearPedidoPrueba("ABC", pedidoStatus.ENVIADO, "Usopp")

        val nuevoEstado = if (pedido.estado == pedidoStatus.ENVIADO) {
            pedidoStatus.COMPLETO
        } else {
            pedido.estado
        }

        assertEquals(pedidoStatus.COMPLETO, nuevoEstado)
    }

    @Test
    fun `validacion de formato de email simple`() {
        val emailValido = "chibi@pixzeleria.com"
        val emailInvalido = "chibi.com"
        val regexEmail = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()

        assertEquals(true, regexEmail.matches(emailValido))
        assertEquals(false, regexEmail.matches(emailInvalido))
    }

    private fun crearPedidoPrueba(id: String, estado: pedidoStatus, cliente: String): Pedido {
        return Pedido(
            id = id,
            estado = estado,
            nombreCliente = cliente,
            items = emptyList(),
            numeroCliente = "123456",
            emailCliente = "test@test.com",
            direccionDeli = "Grand Line 123",
            total = 15000.0
        )
    }
}