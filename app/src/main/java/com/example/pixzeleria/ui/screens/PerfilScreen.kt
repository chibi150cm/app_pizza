package com.example.pixzeleria.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pixzeleria.data.model.User
import com.example.pixzeleria.ui.viewmodel.MainViewModel
import com.example.pixzeleria.utils.formularioValidacion
import com.example.pixzeleria.utils.toErrorMessage
import kotlinx.coroutines.delay
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import android.net.Uri
import androidx.compose.ui.graphics.Color

@Composable
fun PerfilScreen(
    viewModel: MainViewModel,
    onNavigateToOrders: () -> Unit,
    onNavigateToCocina: () -> Unit,
    onNavigateToReparto: () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        PerfilLogueadoView(viewModel, onNavigateToOrders, onNavigateToCocina, onNavigateToReparto)
    } else {
        PerfilLoginView(viewModel)
    }
}

// Formulario lgoin/regsitro
@Composable
fun PerfilLoginView(viewModel: MainViewModel) {
    val error by viewModel.loginError.collectAsState()
    var isRegistering by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Variables para recuperación
    var showRecoverDialog by remember { mutableStateOf(false) }

    if (showRecoverDialog) {
        AlertDialog(
            onDismissRequest = { showRecoverDialog = false },
            title = { Text("Recuperar Contraseña") },
            text = { Text("Se enviará un correo a $email con las instrucciones.") },
            confirmButton = { Button(onClick = { showRecoverDialog = false }) { Text("Enviar") } }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isRegistering) "Crear Cuenta" else "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isRegistering) {
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isRegistering) {
                    val nuevoUsuario = User(nombre = nombre, email = email, password = password, rol = "USER")
                    viewModel.registrarse(nuevoUsuario)
                } else {
                    viewModel.login(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (isRegistering) "Registrarme" else "Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { isRegistering = !isRegistering }) {
            Text(if (isRegistering) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate")
        }

        if (!isRegistering) {
            TextButton(onClick = { showRecoverDialog = true }) {
                Text("Olvidé mi contraseña", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// Vista usuario logueado
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilLogueadoView(
    viewModel: MainViewModel,
    onNavigateToOrders: () -> Unit,
    onNavigateToCocina: () -> Unit,
    onNavigateToReparto: () -> Unit
) {
    val usuario by viewModel.usuario.collectAsState()
    val pedidos by viewModel.pedidos.collectAsState()
    val favoritos by viewModel.favoritas.collectAsState()
    val esAdmin by viewModel.esAdmin.collectAsState()
    val tienePermisoCocina by viewModel.tienePermisoCocina.collectAsState()
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

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

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imagenUri = uri
        }
    }

    fun validaryGuardar(): Boolean {
        val validations = formularioValidacion.validarCheckout(nombreU, emailU, telefonoU, direccionU)
        nombreError = validations["name"]?.toErrorMessage()
        emailError = validations["email"]?.toErrorMessage()
        telefonoError = validations["phone"]?.toErrorMessage()
        direccionError = validations["address"]?.toErrorMessage()

        if (validations.values.all { it.isValid }) {
            val usuarioActualizado = User(
                id = usuario.id,
                nombre = nombreU,
                email = emailU,
                password = usuario.password,
                telefono = telefonoU,
                direccion = direccionU,
                rol = usuario.rol
            )

            viewModel.actualizarPerfil(usuarioActualizado)

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
                    // Botón de logout
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        snackbarHost = {
            if (mostrarGuardado) {
                Snackbar(modifier = Modifier.padding(16.dp)) { Text("Perfil guardado con éxito") }
                LaunchedEffect(Unit) { delay(2000); mostrarGuardado = false }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Aquí está la cosa para ponerle una foto de perfil
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
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imagenUri != null) {
                        AsyncImage(
                            model = imagenUri,
                            contentDescription = "Foto Perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Toca para cambiar foto", style = MaterialTheme.typography.labelSmall)

                Spacer(modifier = Modifier.height(8.dp))

                // Botón editar
                Spacer(modifier = Modifier.height(8.dp))
                if (!editando) {
                    OutlinedButton(onClick = { editando = true }) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar Datos")
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { validaryGuardar() }) { Text("Guardar") }
                        OutlinedButton(onClick = { editando = false }) { Text("Cancelar") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estadísticas
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Modifier.weight(1f), Icons.Default.ShoppingBag, pedidos.size.toString(), "Pedidos")
                StatCard(Modifier.weight(1f), Icons.Default.Favorite, favoritos.size.toString(), "Favoritos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formulario con info
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Información Personal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = nombreU, onValueChange = { nombreU = it; nombreError = null },
                        label = { Text("Nombre completo") }, leadingIcon = { Icon(Icons.Default.Person, null) },
                        enabled = editando, isError = nombreError != null,
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = emailU, onValueChange = { emailU = it; emailError = null },
                        label = { Text("Email") }, leadingIcon = { Icon(Icons.Default.Email, null) },
                        enabled = editando, isError = emailError != null,
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = telefonoU, onValueChange = { telefonoU = it; telefonoError = null },
                        label = { Text("Teléfono") }, leadingIcon = { Icon(Icons.Default.Phone, null) },
                        enabled = editando, isError = telefonoError != null,
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = direccionU, onValueChange = { direccionU = it; direccionError = null },
                        label = { Text("Dirección") }, leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        enabled = editando, isError = direccionError != null,
                        modifier = Modifier.fillMaxWidth(), minLines = 2
                    )
                }
            }

            // Cocina
            if (tienePermisoCocina) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if(esAdmin) MaterialTheme.colorScheme.tertiaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (esAdmin) "Modo: GERENCIA (ZEFF)" else "Modo: COCINA (SANJI)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (esAdmin) "Administración total y acceso a cocina"
                                    else "Panel de comandas habilitado",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Icon(
                                imageVector = if(esAdmin) Icons.Default.VerifiedUser else Icons.Default.Restaurant,
                                contentDescription = null
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onNavigateToCocina,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.RestaurantMenu, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ENTRAR A COCINA")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val esRepartidor by viewModel.esRepartidor.collectAsState()

            if (esRepartidor) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if(esAdmin) MaterialTheme.colorScheme.tertiaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (esAdmin) "Modo: GERENCIA (ZEFF)" else "Modo: REPARTIDOR (GOD USOPP)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (esAdmin) "Administración total y acceso a repartidor"
                                    else "Panel de entregas habilitado",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Icon(
                                imageVector = if(esAdmin) Icons.Default.VerifiedUser else Icons.Default.DeliveryDining,
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToReparto,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ENTRAR A REPARTIDOR")
                        }
                    }
                }
            }

            // Opciones de menú
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column {
                    PerfilMenuItem(Icons.Default.History, "Historial de Pedidos", "${pedidos.size} pedidos realizados", onNavigateToOrders)
                    Divider()
                    PerfilMenuItem(Icons.Default.Info, "Acerca de", "Versión 1.0.0", {})
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val esPersonalStaff = (usuario.rol == "COCINERO" || usuario.rol == "REPARTIDOR")

            if (!esPersonalStaff || esAdmin) {

                var showDeleteDialog by remember { mutableStateOf(false) }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("¿Eliminar Cuenta?") },
                        text = { Text("Esta acción es permanente.") },
                        confirmButton = {
                            Button(
                                onClick = { viewModel.eliminarCuenta(); showDeleteDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) { Text("Sí, Eliminar") }
                        },
                        dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteForever, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar mi Cuenta")
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Cuenta de Staff protegida por la Gerencia.",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
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