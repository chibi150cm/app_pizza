package com.example.pixzeleria.logic

interface DescuentoStrategy {
    fun calcularDescuento(monto: Double): Double
    fun obtenerNombre(): String
}

class SinDescuento : DescuentoStrategy {
    override fun calcularDescuento(monto: Double): Double = 0.0
    override fun obtenerNombre(): String = "Normal"
}

class DescuentoGamer : DescuentoStrategy {
    override fun calcularDescuento(monto: Double): Double {
        return monto * 0.15
    }
    override fun obtenerNombre(): String = "Gamer PRO (15% OFF)"
}