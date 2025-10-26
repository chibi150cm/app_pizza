package com.example.pixzeleria.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.pixzeleria.data.model.Carrito
import com.example.pixzeleria.data.model.Pedido
import com.example.pixzeleria.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pixzeleria_prefs")

class DataStoreManager(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val USUARIO_NOMBRE = stringPreferencesKey("usuario_nombre")
        private val USUARIO_EMAIL = stringPreferencesKey("usuario_email")
        private val USUARIO_TELEFONO = stringPreferencesKey("usuario_telefono")
        private val USUARIO_DIRECCION = stringPreferencesKey("usuario_direccion")
        private val CARRO_PIZZAS = stringPreferencesKey("carro_pizzas")
        private val HISTORIAL_PEDIDOS = stringPreferencesKey("historial_pedidos")
        private val PIZZAS_FAVORITAS = stringPreferencesKey("pizzas_favoritas")
    }

    // Info del usuariosss
    suspend fun guardarUsuario(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USUARIO_NOMBRE] = user.nombre
            preferences[USUARIO_EMAIL] = user.email
            preferences[USUARIO_TELEFONO] = user.telefono
            preferences[USUARIO_DIRECCION] = user.direccion
        }
    }

    val usuarioFlow: Flow<User> = context.dataStore.data.map { preferences ->
        User(
            nombre = preferences[USUARIO_NOMBRE] ?: "",
            email = preferences[USUARIO_EMAIL] ?: "",
            telefono = preferences[USUARIO_TELEFONO] ?: "",
            direccion = preferences[USUARIO_DIRECCION] ?: ""
        )
    }

    // Carrito
    suspend fun guardarCarrito(items: List<Carrito>) {
        context.dataStore.edit { preferences ->
            preferences[CARRO_PIZZAS] = gson.toJson(items)
        }
    }

    val carritoFlow: Flow<List<Carrito>> = context.dataStore.data.map { preferences ->
        val json = preferences[CARRO_PIZZAS] ?: "[]"
        val type = object : TypeToken<List<Carrito>>() {}.type
        gson.fromJson(json, type) ?: emptyList()
    }

    // Historial de las órdenes
    suspend fun guardarPedido(pedido: Pedido) {
        context.dataStore.edit { preferences ->
            val historialActual = preferences[HISTORIAL_PEDIDOS] ?: "[]"
            val type = object : TypeToken<MutableList<Pedido>>() {}.type
            val pedidos: MutableList<Pedido> = gson.fromJson(historialActual, type) ?: mutableListOf()
            pedidos.add(0, pedido)
            preferences[HISTORIAL_PEDIDOS] = gson.toJson(pedidos)
        }
    }

    val pedidoHistorialFlow: Flow<List<Pedido>> = context.dataStore.data.map { preferences ->
        val json = preferences[HISTORIAL_PEDIDOS] ?: "[]"
        val type = object : TypeToken<List<Pedido>>() {}.type
        gson.fromJson(json, type) ?: emptyList()
    }

    // Favoritos
    suspend fun guardarPizzaFavorita(pizzaId: String) {
        context.dataStore.edit { preferences ->
            val actualFavoritas = preferences[PIZZAS_FAVORITAS] ?: "[]"
            val type = object : TypeToken<MutableList<String>>() {}.type
            val favoritas: MutableList<String> = gson.fromJson(actualFavoritas, type) ?: mutableListOf()

            if (favoritas.contains(pizzaId)) {
                favoritas.remove(pizzaId)
            } else {
                favoritas.add(pizzaId)
            }

            preferences[PIZZAS_FAVORITAS] = gson.toJson(favoritas)
        }
    }

    val favoritasFlow: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val json = preferences[PIZZAS_FAVORITAS] ?: "[]"
        val type = object : TypeToken<List<String>>() {}.type
        gson.fromJson(json, type) ?: emptyList()
    }

    suspend fun limpiarCarrito() {
        context.dataStore.edit { preferences ->
            preferences[CARRO_PIZZAS] = "[]"
        }
    }
}