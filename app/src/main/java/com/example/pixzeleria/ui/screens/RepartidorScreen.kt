package com.example.pixzeleria.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pixzeleria.data.model.Pedido
import com.example.pixzeleria.data.model.pedidoStatus
import com.example.pixzeleria.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepartidorScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val pedidosReparto by viewModel.pedidosReparto.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repartos Activos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE0F7FA),
                    titleContentColor = Color(0xFF006064)
                )
            )
        }
    ) { paddingValues ->
        if (pedidosReparto.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No hay entregas pendientes", style = MaterialTheme.typography.titleMedium)
                    Text("¡Descansa, guerrero del mar!", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidosReparto) { pedido ->
                    RepartidorCard(
                        pedido = pedido,
                        onEntregar = {
                            viewModel.cambiarEstadoPedido(pedido.id, pedidoStatus.COMPLETO)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RepartidorCard(pedido: Pedido, onEntregar: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Pa que el repartidor vea el maps
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val direccion = pedido.direccionDeli
                        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(direccion)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")

                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            val webIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            try {
                                context.startActivity(webIntent)
                            } catch (e2: Exception) {
                            }
                        }
                    }
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Mapa",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("Dirección (Toca para Mapa)", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = pedido.direccionDeli.ifEmpty { "Sin dirección especificada" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Text("Cliente: ${pedido.nombreCliente}")
            Text("Pedido #${pedido.id.take(6)}")
            Text(
                "Total a cobrar: $${pedido.total.toInt()}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF388E3C),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onEntregar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)) // Verde éxito
            ) {
                Icon(Icons.Default.CheckCircle, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CONFIRMAR ENTREGA")
            }
        }
    }
}