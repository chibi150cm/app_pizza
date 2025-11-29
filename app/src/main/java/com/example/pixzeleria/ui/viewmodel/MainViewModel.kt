package com.example.pixzeleria.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixzeleria.data.model.*
import com.example.pixzeleria.data.local.DataStoreManager
import com.example.pixzeleria.network.DetallePedidoRequest
import com.example.pixzeleria.network.PedidoRequest
import com.example.pixzeleria.network.RetrofitClient
import com.example.pixzeleria.network.toUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    // Estado de los datos
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

    // Estados de las apis externas
    private val _temperatura = MutableStateFlow<Double?>(null)
    val temperatura: StateFlow<Double?> = _temperatura.asStateFlow()

    private val _pokemonNombre = MutableStateFlow<String>("")
    val pokemonNombre: StateFlow<String> = _pokemonNombre.asStateFlow()

    private val _pokemonImagen = MutableStateFlow<String>("")
    val pokemonImagen: StateFlow<String> = _pokemonImagen.asStateFlow()

    // Estados de logueo
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    val carroTotal: StateFlow<Double> = _carro.map { items -> items.sumOf { it.subtotal } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val cartItemCount: StateFlow<Int> = _carro.map { items -> items.sumOf { it.cantidad } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    init {
        loadInitialData()
        observeDataStore()
        cargarMascotaDelDia()
        cargarClima()
    }

    // Aquí se cargan los datitos
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val pizzasBackend = RetrofitClient.instance.obtenerPizzas()
                    val pizzasUi = pizzasBackend.map { it.toUiModel() }
                    _pizzas.value = pizzasUi
                }
            } catch (e: Exception) {
                Log.e("API", "Error cargando menú: ${e.message}")
                _pizzas.value = emptyList()
            }
        }
    }

    private fun observeDataStore() {
        viewModelScope.launch { dataStoreManager.carritoFlow.collect { _carro.value = it } }
        viewModelScope.launch {
            dataStoreManager.usuarioFlow.collect { storedUser ->
                _usuario.value = storedUser
                if (storedUser.email.isNotEmpty()) {
                    _isLoggedIn.value = true
                    _esAdmin.value = (storedUser.rol == "ADMIN")

                    if (storedUser.id != null) {
                        cargarHistorialReal()
                    }
                } else {
                    _isLoggedIn.value = false
                    _esAdmin.value = false
                    _pedidos.value = emptyList()
                }
            }
        }

        viewModelScope.launch { dataStoreManager.favoritasFlow.collect { _favoritas.value = it } }
    }

    // Gestión de registro/login

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            try {
                _loginError.value = null
                val credenciales = mapOf("email" to email, "password" to pass)
                val response = RetrofitClient.instance.login(credenciales)

                if (response.isSuccessful && response.body() != null) {
                    val usuarioRecibido = response.body()!!
                    guardarUsuario(usuarioRecibido)
                } else {
                    _loginError.value = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                _loginError.value = "Error de conexión"
            }
        }
    }

    fun registrarse(usuario: User) {
        viewModelScope.launch {
            try {
                _loginError.value = null
                val response = RetrofitClient.instance.registrar(usuario)
                if (response.isSuccessful && response.body() != null) {
                    val usuarioCreado = response.body()!!
                    guardarUsuario(usuarioCreado)
                } else {
                    _loginError.value = "El correo ya está registrado"
                }
            } catch (e: Exception) {
                _loginError.value = "Error al registrar"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val usuarioVacio = User()
            guardarUsuario(usuarioVacio)
        }
    }

    // Gestión del perfil

    fun guardarUsuario(user: User) {
        viewModelScope.launch { dataStoreManager.guardarUsuario(user) }
    }

    fun actualizarPerfil(nuevoUsuario: User) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.guardarPerfil(nuevoUsuario)
                if (response.isSuccessful) {
                    Log.d("API", "Perfil actualizado")
                }
                guardarUsuario(nuevoUsuario)
            } catch (e: Exception) {
                Log.e("API", "Error conexión: ${e.message}")
                guardarUsuario(nuevoUsuario)
            }
        }
    }

    fun eliminarCuenta() {
        viewModelScope.launch {
            try {
                val miId = _usuario.value.id
                if (miId != null) {
                    val response = RetrofitClient.instance.eliminarCliente(miId)
                    if(response.isSuccessful) Log.d("API", "Cuenta eliminada")
                }
            } catch (e: Exception) {
                Log.e("API", "Error borrando: ${e.message}")
            } finally {
                logout()
            }
        }
    }

    // Gestión de los pedidos

    fun cargarHistorialReal() {
        viewModelScope.launch {
            try {
                val idCliente = _usuario.value.id

                if (idCliente != null) {
                    val pedidosReales = RetrofitClient.instance.obtenerPedidosPorCliente(idCliente)

                    val pedidosUi = pedidosReales.map { p ->
                        Pedido(
                            id = p.id.toString(),
                            total = p.total,
                            estado = try { pedidoStatus.valueOf(p.estado) } catch (e: Exception) { pedidoStatus.PENDIENTE },
                            fecha = System.currentTimeMillis(),
                            nombreCliente = _usuario.value.nombre,
                            numeroCliente = _usuario.value.telefono,
                            emailCliente = _usuario.value.email,
                            direccionDeli = _usuario.value.direccion,
                            items = p.items.map { item ->
                                Carrito(
                                    pizza = Pizza(id="0", nombrePizza=item.nombrePizza, descripcion="", precio=item.precio, imagenUrl="", categoria=""),
                                    cantidad = item.cantidad
                                )
                            }
                        )
                    }
                    _pedidos.value = pedidosUi.reversed()
                }
            } catch (e: Exception) {
                Log.e("API", "Error historial: ${e.message}")
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
                    DetallePedidoRequest(
                        nombrePizza = carritoItem.pizza.nombrePizza,
                        pizzaId = carritoItem.pizza.id.toLongOrNull() ?: 0L,
                        precio = carritoItem.pizza.precio,
                        cantidad = carritoItem.cantidad
                    )
                }

                val idCliente = _usuario.value.id ?: 0L
                val requestBackend = PedidoRequest(clienteId = idCliente, items = itemsRequest)

                withContext(Dispatchers.IO) {
                    val response = RetrofitClient.instance.crearPedido(requestBackend)
                    if (response.isSuccessful) {
                        cargarHistorialReal()
                    }
                }
                limpiarCarro()
            } catch (e: Exception) {
                Log.e("API", "Error pedido: ${e.message}")
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
                        val lista = _pedidos.value.toMutableList()
                        lista.removeAll { it.id == pedidoId }
                        _pedidos.value = lista
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error al eliminar: ${e.message}")
            }
        }
    }

    // Operaciones de carrito
    fun agregarCarro(pizza: Pizza, quantity: Int = 1) {
        viewModelScope.launch {
            val currentCart = _carro.value.toMutableList()
            val existingItem = currentCart.find { it.pizza.id == pizza.id }
            if (existingItem != null) existingItem.cantidad += quantity
            else currentCart.add(Carrito(pizza, quantity))
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
                if (newQuantity > 0) currentCart[index] = carrito.copy(cantidad = newQuantity)
                else currentCart.removeAt(index)
                dataStoreManager.guardarCarrito(currentCart)
            }
        }
    }

    fun limpiarCarro() {
        viewModelScope.launch { dataStoreManager.limpiarCarrito() }
    }

    fun toggleFavoritos(pizzaId: String) {
        viewModelScope.launch { dataStoreManager.guardarPizzaFavorita(pizzaId) }
    }

    fun esFavorita(pizzaId: String): Boolean = _favoritas.value.contains(pizzaId)

    // APIS de Pokemon y Clima
    fun cargarClima() {
        viewModelScope.launch {
            try {
                val response = com.example.pixzeleria.network.ClimaRetrofitClient.instance.obtenerClima(-33.4489, -70.6693)
                if (response.isSuccessful) _temperatura.value = response.body()?.currentWeather?.temperature
            } catch (e: Exception) { Log.e("API", "Error clima") }
        }
    }

    fun cargarMascotaDelDia() {
        viewModelScope.launch {
            try {
                val response = com.example.pixzeleria.network.PokeRetrofitClient.instance.obtenerPokemon((1..151).random())
                if (response.isSuccessful) {
                    val p = response.body()
                    _pokemonNombre.value = p?.name?.replaceFirstChar { it.uppercase() } ?: ""
                    _pokemonImagen.value = p?.sprites?.frontDefault ?: ""
                }
            } catch (e: Exception) { Log.e("API", "Error Pokemon") }
        }
    }
}