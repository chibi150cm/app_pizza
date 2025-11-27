package com.example.pixzeleria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke // Importante para el borde rojo
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pixzeleria.data.model.User
import com.example.pixzeleria.ui.viewmodel.MainViewModel
import com.example.pixzeleria.utils.formularioValidacion
import com.example.pixzeleria.utils.toErrorMessage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: MainViewModel, // <- Aquí recibes el viewModel
    onNavigateToOrders: () -> Unit
) {
    val usuario by viewModel.usuario.collectAsState()
    val pedidos by viewModel.pedidos.collectAsState()
    val favoritos by viewModel.favoritas.collectAsState()
    val focusManager = LocalFocusManager.current

    var editando by remember { mutableStateOf(false) }
    var nombreU by remember { mutableStateOf(usuario.nombre) }
    var emailU by remember { mutableStateOf(usuario.email) }
    var telefonoU by remember { mutableStateOf(usuario.telefono) }
    var direccionU by remember { mutableStateOf(usuario.direccion) }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    var direccionError by remember { mutableStateOf<String?>(null) }

    var mostrarGuardado by remember { mutableStateOf(false) }

    LaunchedEffect(usuario) {
        nombreU = usuario.nombre
        emailU = usuario.email
        telefonoU = usuario.telefono
        direccionU = usuario.direccion
    }

    fun validaryGuardar(): Boolean {
        val validations = formularioValidacion.validarCheckout(nombreU, emailU, telefonoU, direccionU)

        nombreError = validations["name"]?.toErrorMessage()
        emailError = validations["email"]?.toErrorMessage()
        telefonoError = validations["phone"]?.toErrorMessage()
        direccionError = validations["address"]?.toErrorMessage()

        if (validations.values.all { it.isValid }) {
            viewModel.actualizarPerfil(User(nombreU, emailU, telefonoU, direccionU))
            editando = false
            mostrarGuardado = true
            return true
        }
        return false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Pixi-Perfil") },
                actions = {
                    if (editando) {
                        TextButton(onClick = {
                            editando = false
                            nombreU = usuario.nombre
                            emailU = usuario.email
                            telefonoU = usuario.telefono
                            direccionU = usuario.direccion
                            nombreError = null
                            emailError = null
                            telefonoError = null
                            direccionError = null
                        }) {
                            Text("Cancelar")
                        }
                        TextButton(onClick = { validaryGuardar() }) {
                            Text("Guardar")
                        }
                    } else {
                        IconButton(onClick = { editando = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = {
            if (mostrarGuardado) {
                Snackbar(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Perfil guardado con éxito")
                }
                LaunchedEffect(Unit) {
                    delay(2000)
                    mostrarGuardado = false
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con avatar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    if (usuario.nombre.isNotEmpty()) usuario.nombre else "Usuario",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                if (usuario.email.isNotEmpty()) {
                    Text(
                        usuario.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estadísticas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.ShoppingBag,
                    value = pedidos.size.toString(),
                    label = "Pedidos"
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Favorite,
                    value = favoritos.size.toString(),
                    label = "Favoritos"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formulario de información
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
                        "Información Personal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = nombreU,
                        onValueChange = {
                            nombreU = it
                            nombreError = null
                        },
                        label = { Text("Nombre completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        enabled = editando,
                        isError = nombreError != null,
                        supportingText = nombreError?.let { { Text(it) } },
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

                    OutlinedTextField(value = emailU, onValueChange = { emailU = it; emailError = null }, label = { Text("Email") }, leadingIcon = { Icon(Icons.Default.Email, null) }, enabled = editando, isError = emailError != null, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = telefonoU, onValueChange = { telefonoU = it; telefonoError = null }, label = { Text("Teléfono") }, leadingIcon = { Icon(Icons.Default.Phone, null) }, enabled = editando, isError = telefonoError != null, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = direccionU, onValueChange = { direccionU = it; direccionError = null }, label = { Text("Dirección") }, leadingIcon = { Icon(Icons.Default.LocationOn, null) }, enabled = editando, isError = direccionError != null, modifier = Modifier.fillMaxWidth())
                }
            }

            // Opciones de menú
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column {
                    PerfilMenuItem(
                        icon = Icons.Default.History,
                        title = "Historial de Pedidos",
                        subtitle = "${pedidos.size} pedidos realizados",
                        onClick = onNavigateToOrders
                    )
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    PerfilMenuItem(
                        icon = Icons.Default.Info,
                        title = "Acerca de",
                        subtitle = "Versión 1.0.0",
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            var showDeleteDialog by remember { mutableStateOf(false) }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("¿Eliminar Cuenta?") },
                    text = { Text("Se borrarán tus datos personales de la base de datos. Esta acción es permanente.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.eliminarCuenta()
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Sí, Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar mi Cuenta")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, icon: ImageVector, value: String, label: String) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.secondary)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PerfilMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}