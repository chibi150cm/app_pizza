# La Pixzeleria

Bienvenido seas! Esta es una aplicación móvil android, desarrollada en Kotlin y que permite a los usuarios comprar pizzas con una temática visual Retro/Pixel Art.
Este proyecto implementa una arquitectura de microservicios utilizando **Spring Boot** para el backend, y la interfaz en **Jetpack Compose** para el frontend.

## Integrantes
* **Mara Cantillana** - Desarrolladora única Full Stack con Tuti

---

## Funcionalidades principales
La aplicación cubre el ciclo completo de compra y gestión de usuario:

1.  **Catálogo en tiempo real:** Visualización de las pizzas disponibles desde MySQL.
2.  **Carrito de compras:** Lógica de negocio para agregar pizzas, calcular subtotales y totales.
3.  **Gestión de pedidos (CRUD):**
    * **Create:** Genera nuevos pedidos con validación de stock.
    * **Read:** Historial de pedidos anteriores con detalles.
    * **Delete:** Cancelación de pedidos (eliminación tanto lógica como física en BD).
4.  **Gestión de perfil (CRUD):**
    * **Update:** Edición de datos personales (dirección, teléfono).
    * **Delete:** Eliminación de cuenta de usuario.
5.  **Integración API Externa:** Consumo de **PokeAPI** para mostrar una "Mascota del día" aleatoria en el Home, más una API de clima en Santiago.
6.  **Persistencia Híbrida:** Uso de **DataStore** para caché local y **MySQL** para persistencia remota.
7.  **Recursos nativos:** Al confirmar un pedido, el celular emitirá una vibración, tal y como lo hacen los joystick en los videojuegos.

---

## Endpoints utilizados

La aplicación se comunica con los siguientes servicios:

### 1. Microservicios propios (Spring Boot)
Base URL: `http://10.0.2.2:8081/api/`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **GET** | `/pizzas` | Obtiene el menú completo de pizzas |
| **POST** | `/pedidos` | Crea una nueva orden de compra |
| **GET** | `/pedidos/cliente/{id}` | Obtiene el historial de pedidos del cliente |
| **DELETE** | `/pedidos/{id}` | Elimina/Cancela el pedido que se hizo |
| **POST** | `/clientes/guardar` | Crea o actualiza la información del perfil de usuario |
| **DELETE** | `/clientes/{id}` | Elimina la cuenta del usuario |

### 2. API Externa
| Servicio | Endpoint | Descripción |
| :--- | :--- | :--- |
| **PokeAPI** | `https://pokeapi.co/api/v2/pokemon/{id}` | Obtiene nombre e imagen de un Pokémon aleatorio (Gen 1) |
| **OpenMeteo** | `https://api.open-meteo.com/v1/` | Obtiene el clima a tiempo real |

---

## Pasos para ejecutar el proyecto

Para probar el entorno completo (Cliente-Servidor), sigue este orden:

### 1. Base de Datos
* Abrir **XAMPP**.
* Iniciar Tomcat.
* Iniciar Apache.
* Iniciar el servicio **MySQL** (Puerto 3306).
* Crear la base de datos llamada: `pixzeria_pizzas` (Esto es MUCHO MUY IMPORTANTE!)

### 2. Backend (Spring Boot)
* Abrir el proyecto del backend.
* Esperar a que Gradle sincronice las dependencias.
* Ejecutar la clase principal `PixzeleriaApplication.kt`.
* Verificar en consola que diga: `Tomcat started on port 8081`.

### 3. Frontend (Android)
* Abrir el proyecto de la app en **Android Studio**.
* Asegurarse de tener un **Emulador** configurado (API 26 o superior) con acceso a internet.
* Ejecutar la app.
* *Nota:* La app está configurada para apuntar a `10.0.2.2`, que es la dirección localhost del emulador.

---

## Evidencia de Entrega (APK)

En la carpeta Releases se encuentran los archivos generados:

* **APK Firmado:** `app-release.apk`
* **Llave de Firma:** `llave_definitiva.jks`

---
Desarrollado para la asignatura **DSY1105 - Desarrollo de Aplicaciones Móviles**, tiene errores, NO SE LO ROBE.
