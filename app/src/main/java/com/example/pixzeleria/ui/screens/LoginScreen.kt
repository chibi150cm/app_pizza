package com.example.pixzeleria.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.pixzeleria.ui.viewmodel.MainViewModel

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onNavigateToCocina: () -> Unit,
    onNavigateToCliente: () -> Unit
) {
    val rolState = viewModel.loginRol.collectAsState()
    val errorState = viewModel.loginError.collectAsState()

    val rol = rolState.value
    val error = errorState.value
    // 2. Estados locales para los campos de texto
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // 3. EFECTO DE NAVEGACIÓN (El Cerebro 🧠)
    // Apenas 'rol' cambie (y no sea null), ejecutamos esto:
    LaunchedEffect(rol) {
        when (rol) {
            "COCINERO", "ADMIN" -> onNavigateToCocina() // Staff a la cocina
            "USER", "CLIENTE" -> onNavigateToCliente()  // Clientes al menú
        }
    }

    // 4. La Interfaz Gráfica
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "🍕 Pixzeleria",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Bienvenido al sistema",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Password (con ojito para ver)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Ver contraseña")
                    }
                }
            )

            // Mensaje de Error (solo si existe)
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Entrar
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "INGRESAR", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- ZONA DE DESARROLLADOR (Bórrala cuando termines la app) ---
            Divider()
            Text("Accesos Rápidos (Dev Mode)", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = {
                    email = "cocina@pixzeleria.com"
                    password = "123" // Ojo con la pass que pusiste en el DataSeeder
                }) {
                    Text("Soy Sanji 🧑‍🍳")
                }

                TextButton(onClick = {
                    email = "cliente@pixzeleria.com"
                    password = "admin" // O la pass que tenga el cliente
                }) {
                    Text("Soy Cliente 👤")
                }
            }
        }
    }
}