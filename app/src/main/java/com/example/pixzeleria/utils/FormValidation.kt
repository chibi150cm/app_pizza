package com.example.pixzeleria.utils

import android.util.Patterns

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

object formularioValidacion {

    fun validarNombre(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "El nombre es requerido")
            name.length < 2 -> ValidationResult(false, "El nombre debe tener al menos 2 caracteres")
            name.length > 50 -> ValidationResult(false, "El nombre no puede exceder 50 caracteres")
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) ->
                ValidationResult(false, "El nombre solo puede contener letras")
            else -> ValidationResult(true)
        }
    }

    fun validarEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "El email es requerido")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult(false, "Email inválido")
            else -> ValidationResult(true)
        }
    }

    fun validarTelefono(phone: String): ValidationResult {
        val cleanPhone = phone.replace(Regex("[\\s-()]"), "")
        return when {
            phone.isBlank() -> ValidationResult(false, "El teléfono es requerido")
            cleanPhone.length < 8 -> ValidationResult(false, "El teléfono debe tener al menos 8 dígitos")
            cleanPhone.length > 15 -> ValidationResult(false, "El teléfono no puede exceder 15 dígitos")
            !cleanPhone.matches(Regex("^[+]?[0-9]+$")) ->
                ValidationResult(false, "El teléfono solo puede contener números")
            else -> ValidationResult(true)
        }
    }

    fun validarDireccion(address: String): ValidationResult {
        return when {
            address.isBlank() -> ValidationResult(false, "La dirección es requerida")
            address.length < 10 -> ValidationResult(false, "La dirección debe tener al menos 10 caracteres")
            address.length > 200 -> ValidationResult(false, "La dirección no puede exceder 200 caracteres")
            else -> ValidationResult(true)
        }
    }

    fun validarCheckout(
        name: String,
        email: String,
        phone: String,
        address: String
    ): Map<String, ValidationResult> {
        return mapOf(
            "name" to validarNombre(name),
            "email" to validarEmail(email),
            "phone" to validarTelefono(phone),
            "address" to validarDireccion(address)
        )
    }

}

// Devuelve el mensaje de error si la validación falló
fun ValidationResult.toErrorMessage(): String? {
    return if (!isValid) errorMessage else null
}