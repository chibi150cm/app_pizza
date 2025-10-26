package com.example.pixzeleria.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import com.example.pixzeleria.data.model.Pizza
import com.example.pixzeleria.ui.viewmodel.MainViewModel
import com.example.pixzeleria.utils.ImageUtils
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToMenu: () -> Unit,
    onNavigateToPizza: (String) -> Unit
) {
    val pizzas by viewModel.pizzas.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()

    // Animación de entrada re piola
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "La Pixzeleria",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Normal
                    )
                },
                actions = {
                    if (cartItemCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(cartItemCount.toString())
                                }
                            }
                        ) {
                            IconButton(onClick = { /* Navegar al carrito */ }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                            }
                        }
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
            // Hero Banner con animación piola
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically() + fadeIn()
            ) {
                HeroBanner(onNavigateToMenu)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Categorías
            Categorias()

            Spacer(modifier = Modifier.height(24.dp))

            // Pizzas destacadas
            Text(
                "Pizzas Destacadas",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(pizzas.take(5)) { pizza ->
                    PizzaDestacadaCard(
                        pizza = pizza,
                        onClick = { onNavigateToPizza(pizza.id) },
                        onAddToCart = { viewModel.agregarCarro(pizza) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sale su promo
            PromoSeccion()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HeroBanner(onNavigateToMenu: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "¡Bienvenido!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Las mejores pixzas artesanales!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNavigateToMenu,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Ver Menú")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun Categorias() {
    val categories = listOf(
        "Clásicas" to Icons.Default.LocalPizza,
        "Gourmet" to Icons.Default.Star,
        "Veggies" to Icons.Default.Eco,
        "Especiales" to Icons.Default.Whatshot
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { (name, icon) ->
            FilterChip(
                selected = false,
                onClick = { /* Filtrar por categoría */ },
                label = { Text(name) },
                leadingIcon = {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun PizzaDestacadaCard(
    pizza: Pizza,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Placeholder para imagen
            Image(
                painter = painterResource(id = ImageUtils.getPizzaImageResource(pizza.imagenUrl)),
                contentDescription = pizza.nombrePizza,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    pizza.nombrePizza,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    pizza.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        numberFormat.format(pizza.precio),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                    }
                }
            }
        }
    }
}

@Composable
fun PromoSeccion() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalOffer,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "¡Oferta Especial!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "2x1 en pizzas familiares todos los viernes",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}