package com.example.pixzeleria.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixzeleria.data.model.*
import com.example.pixzeleria.data.local.DataStoreManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    // State flows
    private val _pizzas = MutableStateFlow<List<Pizza>>(emptyList())
    val pizzas: StateFlow<List<Pizza>> = _pizzas.asStateFlow()

    private val _carro = MutableStateFlow<List<Carrito>>(emptyList())
    val carro: StateFlow<List<Carrito>> = _carro.asStateFlow()

    private val _usuario = MutableStateFlow(User())
    val usuario: StateFlow<User> = _usuario.asStateFlow()

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    private val _favoritas = MutableStateFlow<List<String>>(emptyList())
    val favoritas: StateFlow<List<String>> = _favoritas.asStateFlow()

    val carroTotal: StateFlow<Double> = _carro.map { items ->
        items.sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val cartItemCount: StateFlow<Int> = _carro.map { items ->
        items.sumOf { it.cantidad }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    init {
        loadInitialData()
        observeDataStore()
    }

    private fun loadInitialData() {
        // Ya llegaron las pipshas
        _pizzas.value = listOf(
            Pizza(
                id = "1",
                nombrePizza = "Margherita Clásica",
                descripcion = "Tomate, mozzarella fresca, albahaca y aceite de oliva",
                precio = 12990.0,
                imagenUrl = "margherita",
                categoria = "Clásicas",
                ingredientes = listOf("Tomate", "Mozzarella", "Albahaca", "Aceite de oliva")
            ),
            Pizza(
                id = "2",
                nombrePizza = "Pepperoni Suprema",
                descripcion = "Doble pepperoni, mozzarella y salsa especial",
                precio = 14990.0,
                imagenUrl = "pepperoni",
                categoria = "Clásicas",
                ingredientes = listOf("Pepperoni", "Mozzarella", "Salsa de tomate")
            ),
            Pizza(
                id = "3",
                nombrePizza = "Cuatro Quesos",
                descripcion = "Mozzarella, parmesano, gorgonzola y provolone",
                precio = 15990.0,
                imagenUrl = "quattro_formaggi",
                categoria = "Gourmet",
                ingredientes = listOf("Mozzarella", "Parmesano", "Gorgonzola", "Provolone")
            ),
            Pizza(
                id = "4",
                nombrePizza = "Vegetariana Deluxe",
                descripcion = "Champiñones, pimientos, aceitunas, cebolla y tomate",
                precio = 13990.0,
                imagenUrl = "vegetariana",
                categoria = "Veggies",
                ingredientes = listOf("Champiñones", "Pimientos", "Aceitunas", "Cebolla", "Tomate")
            ),
            Pizza(
                id = "5",
                nombrePizza = "BBQ Chicken",
                descripcion = "Pollo marinado, cebolla morada, bacon y salsa BBQ",
                precio = 16990.0,
                imagenUrl = "bbq_chicken",
                categoria = "Especiales",
                ingredientes = listOf("Pollo", "Cebolla morada", "Bacon", "Salsa BBQ")
            ),
            Pizza(
                id = "6",
                nombrePizza = "Hawaiana",
                descripcion = "Jamón, piña, mozzarella y salsa de tomate",
                precio = 13990.0,
                imagenUrl = "hawaiana",
                categoria = "Clásicas",
                ingredientes = listOf("Jamón", "Piña", "Mozzarella")
            ),
            Pizza(
                id = "7",
                nombrePizza = "Italiana",
                descripcion = "Prosciutto, rúcula, tomates cherry y parmesano",
                precio = 17990.0,
                imagenUrl = "italiana",
                categoria = "Gourmet",
                ingredientes = listOf("Prosciutto", "Rúcula", "Tomates cherry", "Parmesano")
            ),
            Pizza(
                id = "8",
                nombrePizza = "Mexicana Picante",
                descripcion = "Carne molida, jalapeños, nachos, queso cheddar",
                precio = 15990.0,
                imagenUrl = "mexicana",
                categoria = "Especiales",
                ingredientes = listOf("Carne molida", "Jalapeños", "Nachos", "Queso cheddar")
            )
        )
    }

    private fun observeDataStore() {
        viewModelScope.launch {
            dataStoreManager.carritoFlow.collect { _carro.value = it }
        }
        viewModelScope.launch {
            dataStoreManager.usuarioFlow.collect { _usuario.value = it }
        }
        viewModelScope.launch {
            dataStoreManager.pedidoHistorialFlow.collect { _pedidos.value = it }
        }
        viewModelScope.launch {
            dataStoreManager.favoritasFlow.collect { _favoritas.value = it }
        }
    }

    // Carrito operaciones
    fun agregarCarro(pizza: Pizza, quantity: Int = 1) {
        viewModelScope.launch {
            val currentCart = _carro.value.toMutableList()
            val existingItem = currentCart.find { it.pizza.id == pizza.id }

            if (existingItem != null) {
                existingItem.cantidad += quantity
            } else {
                currentCart.add(Carrito(pizza, quantity))
            }

            dataStoreManager.guardarCarrito(currentCart)
        }
    }

    fun eliminarCarro(carrito: Carrito) {
        viewModelScope.launch {
            val currentCart = _carro.value.toMutableList()
            currentCart.remove(carrito)
            dataStoreManager.guardarCarrito(currentCart)
        }
    }

    fun actualizarCtdCarro(carrito: Carrito, newQuantity: Int) {
        viewModelScope.launch {
            val currentCart = _carro.value.toMutableList()
            val index = currentCart.indexOf(carrito)
            if (index != -1) {
                if (newQuantity > 0) {
                    currentCart[index] = carrito.copy(cantidad = newQuantity)
                } else {
                    currentCart.removeAt(index)
                }
                dataStoreManager.guardarCarrito(currentCart)
            }
        }
    }

    fun limpiarCarro() {
        viewModelScope.launch {
            dataStoreManager.limpiarCarrito()
        }
    }

    // Guarda el usuario al rellenar la información. Además que debe rellenarla sí o sí
    fun guardarUsuario(user: User) {
        viewModelScope.launch {
            dataStoreManager.guardarUsuario(user)
        }
    }

    // Operaciones de los pedidosss
    fun crearOrden(
        nombreUsuario: String,
        telefonoUsuario: String,
        emailUsuario: String,
        direccionUsuario: String,
        instruccionEspecial: String
    ) {
        viewModelScope.launch {
            val pedido = Pedido(
                id = UUID.randomUUID().toString(),
                items = _carro.value,
                nombreCliente = nombreUsuario,
                numeroCliente = telefonoUsuario,
                emailCliente = emailUsuario,
                direccionDeli = direccionUsuario,
                instruccionEspecial = instruccionEspecial,
                total = carroTotal.value
            )

            dataStoreManager.guardarPedido(pedido)
            limpiarCarro()
        }
    }

    // A favoritosss
    fun toggleFavoritos(pizzaId: String) {
        viewModelScope.launch {
            dataStoreManager.guardarPizzaFavorita(pizzaId)
        }
    }

    fun esFavorita(pizzaId: String): Boolean {
        return _favoritas.value.contains(pizzaId)
    }
}