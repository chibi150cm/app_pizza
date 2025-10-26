package com.example.pixzeleria.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.pixzeleria.R
import org.w3c.dom.Text

val PixelFont = FontFamily(
    Font(R.font.press_start_2p_regular, FontWeight.Normal)
)

val PixelFont2 = FontFamily(
    Font(R.font.vt323_regular, FontWeight.Normal)
)

val PixelFont3 = FontFamily(
    Font(R.font.pixelify, FontWeight.Thin)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PixelFont,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = PixelFont,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = PixelFont,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = PixelFont,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle( // Tamaño de "Da Pixzeleria" / Bienvenido /
        fontFamily = PixelFont3,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle( // Tamaño de "Pizzas destacadas"
        fontFamily = PixelFont3,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle( // El texto del head de manú / carrito / perfil
        fontFamily = PixelFont2,
        fontWeight = FontWeight.Thin,
        fontSize = 30.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle( // Tamaño del nombre de las pizzas
        fontFamily = PixelFont2,
        fontWeight = FontWeight.Thin,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PixelFont,
        fontWeight = FontWeight.Thin,
        fontSize = 8.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle( // Ingredientes, pero al pinchar la pizza en el menú
        fontFamily = PixelFont3,
        fontWeight = FontWeight.Normal,
        fontSize = 21.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PixelFont3,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle( // Descripción de las pizzas
        fontFamily = PixelFont2,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle( // Tamaño de los botones
        fontFamily = PixelFont2,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 10.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PixelFont3,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle( // Tamaño del circulito de notificación
        fontFamily = PixelFont3,
        fontWeight = FontWeight.Thin,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
)
