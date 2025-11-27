package com.example.pixzeleria

import com.example.pixzeleria.data.model.Carrito
import com.example.pixzeleria.data.model.Pizza
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
    fun `calcular cantidad total de items para el badge`() {
        // GIVEN
        val pizza1 = Pizza(id="1", nombrePizza="P1", descripcion="", precio=100.0, imagenUrl="", categoria="")

        val listaCarrito = listOf(
            Carrito(pizza1, cantidad = 5), // 5 pizzas
            Carrito(pizza1, cantidad = 3)  // 3 pizzas más
        )

        val cantidadTotal = listaCarrito.sumOf { it.cantidad }

        assertEquals(8, cantidadTotal)
    }

    @Test
    fun `calcular total de carrito vacio da cero`() {
        // GIVEN
        val listaCarrito = emptyList<Carrito>()

        // WHEN
        val totalCalculado = listaCarrito.sumOf { it.subtotal }

        // THEN
        assertEquals(0.0, totalCalculado, 0.0)
    }

    @Test
    fun `calcular total final incluyendo costo de envio`() {
        val precioPizza = 10000.0
        val costoDelivery = 2500.0

        val pizza = Pizza(id="1", nombrePizza="P1", descripcion="", precio=precioPizza, imagenUrl="", categoria="")
        val itemCarrito = Carrito(pizza, cantidad = 1)

        val subtotal = itemCarrito.subtotal
        val totalFinal = subtotal + costoDelivery

        assertEquals(12500.0, totalFinal, 0.0)
    }
}