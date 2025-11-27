package com.example.pixzeleria.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixzeleria.data.model.*
import com.example.pixzeleria.data.local.DataStoreManager
import com.example.pixzeleria.network.RetrofitClient
import com.example.pixzeleria.network.toUiModel // Asegúrate de tener esta función del paso anterior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    // State flowsss
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

    // API Pokemon
    private val _pokemonNombre = MutableStateFlow<String>("")
    val pokemonNombre: StateFlow<String> = _pokemonNombre.asStateFlow()

    private val _pokemonImagen = MutableStateFlow<String>("")
    val pokemonImagen: StateFlow<String> = _pokemonImagen.asStateFlow()

    init {
        loadInitialData()
        observeDataStore()
        cargarMascotaDelDia()
        cargarHistorialReal()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val pizzasBackend = RetrofitClient.instance.obtenerPizzas()

                    val pizzasUi = pizzasBackend.map { it.toUiModel() }

                    _pizzas.value = pizzasUi
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error al conectar con el servidor: ${e.message}")
                _pizzas.value = emptyList()
            }
        }
    }

    private fun observeDataStore() {
        viewModelScope.launch { dataStoreManager.carritoFlow.collect { _carro.value = it } }
        viewModelScope.launch { dataStoreManager.usuarioFlow.collect { _usuario.value = it } }
        viewModelScope.launch { dataStoreManager.favoritasFlow.collect { _favoritas.value = it } }
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

    fun guardarUsuario(user: User) {
        viewModelScope.launch {
            dataStoreManager.guardarUsuario(user)
        }
    }

    fun actualizarPerfil(nuevoUsuario: User) {
        viewModelScope.launch {
            try {
                val response = com.example.pixzeleria.network.RetrofitClient.instance.guardarPerfil(nuevoUsuario)

                if (response.isSuccessful) {
                    android.util.Log.d("API", "Perfil guardado/actualizado con éxito")
                } else {
                    android.util.Log.e("API", "Error guardando: ${response.code()}")
                }
                guardarUsuario(nuevoUsuario)
            } catch (e: Exception) {
                android.util.Log.e("API", "Error conexión: ${e.message}")
                guardarUsuario(nuevoUsuario)
            }
        }
    }

    fun eliminarCuenta() {
        viewModelScope.launch {
            try {
                val response = com.example.pixzeleria.network.RetrofitClient.instance.eliminarCliente()
            } catch (e: Exception) {
            } finally {
                val usuarioVacio = User(nombre = "", email = "", telefono = "", direccion = "")
                guardarUsuario(usuarioVacio)
                _pedidos.value = emptyList()
            }
        }
    }

    fun crearOrden(
        nombreUsuario: String,
        telefonoUsuario: String,
        emailUsuario: String,
        direccionUsuario: String,
        instruccionEspecial: String
    ) {
        viewModelScope.launch {
            try {
                val itemsRequest = _carro.value.map { carritoItem ->
                    com.example.pixzeleria.network.DetallePedidoRequest(
                        nombrePizza = carritoItem.pizza.nombrePizza,
                        pizzaId = carritoItem.pizza.id.toLongOrNull() ?: 0L,
                        precio = carritoItem.pizza.precio,
                        cantidad = carritoItem.cantidad
                    )
                }

                val requestBackend = com.example.pixzeleria.network.PedidoRequest(
                    clienteId = 1,
                    items = itemsRequest
                )

                withContext(Dispatchers.IO) {
                    val response = com.example.pixzeleria.network.RetrofitClient.instance.crearPedido(requestBackend)

                    if (response.isSuccessful) {
                        android.util.Log.d("API", "Pedido creado con éxito en servidor: ${response.body()?.id}")

                        cargarHistorialReal()
                    } else {
                        android.util.Log.e("API", "Error en servidor: ${response.code()}")
                    }
                }

                val pedidoLocal = Pedido(
                    id = UUID.randomUUID().toString(),
                    items = _carro.value,
                    nombreCliente = nombreUsuario,
                    numeroCliente = telefonoUsuario,
                    emailCliente = emailUsuario,
                    direccionDeli = direccionUsuario,
                    instruccionEspecial = instruccionEspecial,
                    total = carroTotal.value
                )

                dataStoreManager.guardarPedido(pedidoLocal)

                limpiarCarro()

            } catch (e: Exception) {
                android.util.Log.e("API", "Error de conexión: ${e.message}")
                // Lógica offline de respaldo
                val pedidoLocalOffline = Pedido(
                    id = UUID.randomUUID().toString(),
                    items = _carro.value,
                    nombreCliente = nombreUsuario,
                    numeroCliente = telefonoUsuario,
                    emailCliente = emailUsuario,
                    direccionDeli = direccionUsuario,
                    instruccionEspecial = instruccionEspecial,
                    total = carroTotal.value
                )
                dataStoreManager.guardarPedido(pedidoLocalOffline)
                limpiarCarro()
            }
        }
    }

    fun cancelarPedido(pedidoId: String) {
        viewModelScope.launch {
            try {
                val idLong = pedidoId.toLongOrNull()
                if (idLong != null) {
                    val response = RetrofitClient.instance.eliminarPedido(idLong)

                    if (response.isSuccessful) {
                        val listaActualizada = _pedidos.value.toMutableList()
                        listaActualizada.removeAll { it.id == pedidoId }
                        _pedidos.value = listaActualizada
                        Log.d("API", "Pedido eliminado con éxito")
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error al eliminar: ${e.message}")
            }
        }
    }

    fun cargarHistorialReal() {
        viewModelScope.launch {
            try {
                val pedidosReales = com.example.pixzeleria.network.RetrofitClient.instance.obtenerPedidosPorCliente(1)

                val pedidosUi = pedidosReales.map { p ->
                    com.example.pixzeleria.data.model.Pedido(
                        id = p.id.toString(),
                        total = p.total,
                        estado = when (p.estado) {
                            "PENDIENTE" -> com.example.pixzeleria.data.model.pedidoStatus.PENDIENTE
                            else -> com.example.pixzeleria.data.model.pedidoStatus.COMPLETO
                        },
                        fecha = System.currentTimeMillis(),
                        nombreCliente = "Cliente",
                        numeroCliente = "",
                        emailCliente = "",
                        direccionDeli = "Dirección Registrada",
                        items = p.items.map { item ->
                            // Carrito falso solo pa visualizar
                            com.example.pixzeleria.data.model.Carrito(
                                pizza = com.example.pixzeleria.data.model.Pizza(
                                    id = "0",
                                    nombrePizza = item.nombrePizza,
                                    descripcion = "",
                                    precio = item.precio,
                                    imagenUrl = "",
                                    categoria = ""
                                ),
                                cantidad = item.cantidad
                            )
                        }
                    )
                }

                _pedidos.value = pedidosUi

            } catch (e: Exception) {
                android.util.Log.e("API", "Error cargando historial: ${e.message}")
            }
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

    // Función para traer un Puchamon Random (de la gen 1 obvio)
    fun cargarMascotaDelDia() {
        viewModelScope.launch {
            try {
                Log.d("POKEAPI", "Iniciando búsqueda de Pokémon...") // Chivato 1

                val randomId = (1..151).random()
                val response = com.example.pixzeleria.network.PokeRetrofitClient.instance.obtenerPokemon(randomId)

                if (response.isSuccessful) {
                    val pokemon = response.body()
                    Log.d("POKEAPI", "¡ÉXITO! Pokémon encontrado: ${pokemon?.name}") // Chivato 2

                    _pokemonNombre.value = pokemon?.name?.replaceFirstChar { it.uppercase() } ?: "Desconocido"
                    _pokemonImagen.value = pokemon?.sprites?.frontDefault ?: ""
                } else {
                    Log.e("POKEAPI", "Error del servidor: ${response.code()}") // Chivato 3
                }
            } catch (e: Exception) {
                Log.e("POKEAPI", "FALLÓ LA CONEXIÓN: ${e.message}") // Chivato 4
                e.printStackTrace()
            }
        }
    }
}