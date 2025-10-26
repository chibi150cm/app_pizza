package com.example.pixzeleria.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kotlin.text.Typography
import com.example.pixzeleria.ui.theme.Typography

// Colores de la marca Pixzeleria
private val PrimaryColor = Color(0xFF6891A8) // Rojo pizza
private val SecondaryColor = Color(0xFFB2DBE2) // Naranja cálido
private val TertiaryColor = Color(0xFF59D0E0) // Verde para éxito

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color(0xFFECFCFF),
    primaryContainer = Color(0xFF7CD3FA), // Color del banner de arriba
    onPrimaryContainer = Color(0xFF410002),

    secondary = SecondaryColor,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3F4FF), // El banner de abajito
    onSecondaryContainer = Color(0xFF2B1700),

    tertiary = TertiaryColor,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF00210B),

    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF201A19),

    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF201A19),
    surfaceVariant = Color(0xFFD3F4FF), // Fondo del contenedor de las pizzas
    onSurfaceVariant = Color(0xFF534341),

    outline = Color(0xFF857371),
    outlineVariant = Color(0xFFD8C2BF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFDE9B93),
    onPrimary = Color(0xFF690005),
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD6),

    secondary = Color(0xFFFFB951),
    onSecondary = Color(0xFF4A2800),
    secondaryContainer = Color(0xFF693C00),
    onSecondaryContainer = Color(0xFFFFDDB3),

    tertiary = Color(0xFF7DCBA0),
    onTertiary = Color(0xFF00391D),
    tertiaryContainer = Color(0xFF00522F),
    onTertiaryContainer = Color(0xFFC8E6C9),

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),

    background = Color(0xFF201A19),
    onBackground = Color(0xFFEDE0DE),

    surface = Color(0xFF201A19),
    onSurface = Color(0xFFEDE0DE),
    surfaceVariant = Color(0xFF534341),
    onSurfaceVariant = Color(0xFFD8C2BF),

    outline = Color(0xFFA08C89),
    outlineVariant = Color(0xFF534341)
)

@Composable
fun PixzeleriaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}