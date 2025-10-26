package com.example.pixzeleria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pixzeleria.ui.navigation.BottomNavItem
import com.example.pixzeleria.ui.navigation.Screen
import com.example.pixzeleria.ui.navigation.bottomNavItems
import com.example.pixzeleria.ui.screens.CarritoScreen
import com.example.pixzeleria.ui.screens.CheckoutScreen
import com.example.pixzeleria.ui.screens.HomeScreen
import com.example.pixzeleria.ui.screens.MenuScreen
import com.example.pixzeleria.ui.screens.PedidosScreen
import com.example.pixzeleria.ui.screens.DetallesPizzaScreen
import com.example.pixzeleria.ui.screens.PerfilScreen
import com.example.pixzeleria.ui.theme.PixzeleriaTheme
import com.example.pixzeleria.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixzeleriaTheme {
                val viewModel: MainViewModel = viewModel()
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val cartItemCount by viewModel.cartItemCount.collectAsState()

    Scaffold(
        bottomBar = {
            // Mostrar barra solo en pantallas principales
            if (mostrarBottomBar(currentRoute)) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.screen.route

                        NavigationBarItem(
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item is BottomNavItem.Cart && cartItemCount > 0) {
                                            Badge {
                                                Text(cartItemCount.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                }
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.screen.route) {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToMenu = { navController.navigate(Screen.Menu.route) },
                    onNavigateToPizza = { pizzaId ->
                        navController.navigate(Screen.PizzaDetail.createRoute(pizzaId))
                    }
                )
            }

            composable(Screen.Menu.route) {
                MenuScreen(
                    viewModel = viewModel,
                    onPizzaClick = { pizzaId ->
                        navController.navigate(Screen.PizzaDetail.createRoute(pizzaId))
                    }
                )
            }

            composable(Screen.Cart.route) {
                CarritoScreen(
                    viewModel = viewModel,
                    onCheckout = { navController.navigate(Screen.Checkout.route) },
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.Profile.route) {
                PerfilScreen(
                    viewModel = viewModel,
                    onNavigateToOrders = { navController.navigate(Screen.Orders.route) }
                )
            }

            composable(Screen.Orders.route) {
                PedidosScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(
                route = Screen.PizzaDetail.route,
                arguments = listOf(navArgument("pizzaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val pizzaId = backStackEntry.arguments?.getString("pizzaId")
                DetallesPizzaScreen(
                    viewModel = viewModel,
                    pizzaId = pizzaId ?: "",
                    onNavigateBack = { navController.navigateUp() },
                    onAddedToCart = { navController.navigate(Screen.Cart.route) }
                )
            }

            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    viewModel = viewModel,
                    onOrderPlaced = {
                        navController.navigate(Screen.Orders.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}

private fun mostrarBottomBar(route: String?): Boolean {
    return route in listOf(
        Screen.Home.route,
        Screen.Menu.route,
        Screen.Cart.route,
        Screen.Profile.route
    )
}