package com.example.pixzeleria.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pixzeleria.data.model.Pedido
import com.example.pixzeleria.data.model.pedidoStatus
import com.example.pixzeleria.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocineroScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val pedidosList by viewModel.pedidosCocina.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comandas de Cocina") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a la App"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (pedidosList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay comandas pendientes~")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pedidosList) { pedido ->
                    CocineroOrderCard(
                        pedido = pedido,
                        onCambiarEstado = { nuevoEstado ->
                            viewModel.cambiarEstadoPedido(pedido.id, nuevoEstado)
                        },
                        onEliminarPedido = {
                            viewModel.cancelarPedido(pedido.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CocineroOrderCard(
    pedido: Pedido,
    onCambiarEstado: (pedidoStatus) -> Unit,
    onEliminarPedido: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar Comanda?") },
            text = { Text("¿Estás seguro que deseas eliminar el pedido #${pedido.id.take(4)}?") },
            confirmButton = {
                Button(
                    onClick = {
                        onEliminarPedido()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Orden #${pedido.id.take(6)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    // Chip de estado
                    Surface(
                        color = when(pedido.estado) {
                            pedidoStatus.PENDIENTE -> Color(0xFFFFC107)
                            pedidoStatus.PREPARANDO -> Color(0xFFFFA000)
                            pedidoStatus.COMPLETO -> Color(0xFF4CAF50)
                            else -> Color.Gray
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = pedido.estado.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón eliminar
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Nombre Cliente
            Text("👤 ${pedido.nombreCliente}", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            Text("A PREPARAR:", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)

            pedido.items.forEach { item ->
                Text("• ${item.cantidad}x ${item.pizza.nombrePizza}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botonera de estados
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                when (pedido.estado) {
                    pedidoStatus.PENDIENTE -> {
                        Button(
                            onClick = { onCambiarEstado(pedidoStatus.PREPARANDO) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                        ) { Text("A COCINAR") }
                    }
                    pedidoStatus.PREPARANDO -> {
                        Button(
                            onClick = { onCambiarEstado(pedidoStatus.ENVIADO) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) { Text("TERMINAR Y DESPACHAR") }
                    }
                    pedidoStatus.ENVIADO -> {
                        Text("En camino...", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                    }
                    pedidoStatus.COMPLETO -> {
                        Text("¡Entregado!", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                    }

                    pedidoStatus.CONFIRMADO -> TODO()
                    pedidoStatus.TERMINADO -> TODO()
                    pedidoStatus.CANCELADO -> TODO()
                }
            }
        }
    }
}