package com.example.pixzeleria.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Inicio")
    object Menu : Screen("menu", "Menú")
    object Cart : Screen("cart", "Carrito")
    object Profile : Screen("profile", "Perfil")
    object Orders : Screen("orders", "Pedidos")
    object PizzaDetail : Screen("pizza/{pizzaId}", "Detalle") {
        fun createRoute(pizzaId: String) = "pizza/$pizzaId"
    }
    object Checkout : Screen("checkout", "Finalizar Pedido")
}

sealed class BottomNavItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(
        screen = Screen.Home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        label = "Inicio"
    )

    object Menu : BottomNavItem(
        screen = Screen.Menu,
        selectedIcon = Icons.Filled.Restaurant,
        unselectedIcon = Icons.Outlined.Restaurant,
        label = "Menú"
    )

    object Cart : BottomNavItem(
        screen = Screen.Cart,
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        label = "Carrito"
    )

    object Profile : BottomNavItem(
        screen = Screen.Profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        label = "Perfil"
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Menu,
    BottomNavItem.Cart,
    BottomNavItem.Profile
)