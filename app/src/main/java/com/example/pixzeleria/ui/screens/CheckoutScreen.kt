package com.example.pixzeleria.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pixzeleria.ui.viewmodel.MainViewModel
import com.example.pixzeleria.utils.formularioValidacion
import com.example.pixzeleria.utils.toErrorMessage
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: MainViewModel,
    onOrderPlaced: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val usuario by viewModel.usuario.collectAsState()
    val carroTotal by viewModel.carroTotal.collectAsState()
    val focusManager = LocalFocusManager.current
    val formatoNumero = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    var nombreUsuario by remember { mutableStateOf(usuario.nombre) }
    var emailUsuario by remember { mutableStateOf(usuario.email) }
    var telefonoUsuario by remember { mutableStateOf(usuario.telefono) }
    var direccionUsuario by remember { mutableStateOf(usuario.direccion) }
    var instruccionEspecial by remember { mutableStateOf("") }

    var nomusuError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    var direccionError by remember { mutableStateOf<String?>(null) }

    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var enProceso by remember { mutableStateOf(false) }

    fun formularioValidacion(): Boolean {
        val validaciones = formularioValidacion.validarCheckout(nombreUsuario, emailUsuario, telefonoUsuario, direccionUsuario)

        nomusuError = validaciones["name"]?.toErrorMessage()
        emailError = validaciones["email"]?.toErrorMessage()
        telefonoError = validaciones["phone"]?.toErrorMessage()
        direccionError = validaciones["address"]?.toErrorMessage()

        return validaciones.values.all { it.isValid }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
            title = { Text("Confirmar Pedido") },
            text = {
                Column {
                    Text("¿Confirmas tu pedido por ${formatoNumero.format(carroTotal + 2500)}?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Será entregado en: $direccionUsuario",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        enProceso = true
                        viewModel.crearOrden(
                            nombreUsuario = nombreUsuario,
                            telefonoUsuario = telefonoUsuario,
                            emailUsuario = emailUsuario,
                            direccionUsuario = direccionUsuario,
                            instruccionEspecial = instruccionEspecial
                        )
                        mostrarConfirmacion = false
                        enProceso = false
                        onOrderPlaced()
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizar Pedido") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Información del cliente
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Información de Contacto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Campo Nombre
                    OutlinedTextField(
                        value = nombreUsuario,
                        onValueChange = {
                            nombreUsuario = it
                            nomusuError = null
                        },
                        label = { Text("Nombre completo *") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        isError = nomusuError != null,
                        supportingText = nomusuError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo Email
                    OutlinedTextField(
                        value = emailUsuario,
                        onValueChange = {
                            emailUsuario = it
                            emailError = null
                        },
                        label = { Text("Correo electrónico *") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        isError = emailError != null,
                        supportingText = emailError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo Teléfono
                    OutlinedTextField(
                        value = telefonoUsuario,
                        onValueChange = {
                            telefonoUsuario = it
                            telefonoError = null
                        },
                        label = { Text("Teléfono *") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null)
                        },
                        isError = telefonoError != null,
                        supportingText = telefonoError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo Dirección
                    OutlinedTextField(
                        value = direccionUsuario,
                        onValueChange = {
                            direccionUsuario = it
                            direccionError = null
                        },
                        label = { Text("Dirección de entrega *") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        },
                        isError = direccionError != null,
                        supportingText = direccionError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        minLines = 2,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Instrucciones especiales
                    OutlinedTextField(
                        value = instruccionEspecial,
                        onValueChange = { instruccionEspecial = it },
                        label = { Text("Instrucciones especiales (opcional)") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        placeholder = { Text("Ej: Sin cebolla, timbre roto, etc.") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Resumen del pedido
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Resumen del Pedido",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal:", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            formatoNumero.format(carroTotal),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery:", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            formatoNumero.format(2500),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            formatoNumero.format(carroTotal + 2500),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de finalizar pedido
            Button(
                onClick = {
                    if (formularioValidacion()) {
                        mostrarConfirmacion = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                enabled = !enProceso
            ) {
                if (enProceso) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Realizar Pedido",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}