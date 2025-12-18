# La Pixzeleria

Bienvenido seas! Esta es una aplicación móvil android, desarrollada en Kotlin y que permite a los usuarios comprar pizzas con una temática visual Retro/Pixel Art.
Este proyecto implementa una arquitectura de microservicios utilizando **Spring Boot** para el backend, y la interfaz en **Jetpack Compose** para el frontend.

## Integrantes
* **Mara Cantillana** - Desarrolladora única Full Stack con Tuti

---

## Funcionalidades Principales
La aplicación cubre el ciclo completo de compra y gestión de personal, cumpliendo con los requerimientos del contexto real.

1.  **Gestión de 4 Roles Diferenciados:**
    * **Cliente:** Realiza pedidos y revisa su historial.
    * **Cocinero:** Gestión de comandas (Pendiente -> Preparando -> Enviado).
    * **Repartidor:** Entrega de pedidos (Enviado -> Completo) con soporte de mapas.
    * **Administrador:** Acceso total a todas las áreas de gestión.
2.  **Catálogo y Carrito:** Visualización dinámica desde base de datos y lógica de negocio para cálculos de totales.
3.  **Gestión de Pedidos (CRUD):** Operaciones de persistencia externa en tiempo real a través de microservicios.
4.  **Gestión de Perfil:** Registro, inicio de sesión, recuperación de contraseña y edición de datos con protección para staff.
5.  **Integración API Externa:** Consumo de **PokeAPI** (Mascota del día) y **OpenMeteo** (Clima) mediante Retrofit.
6.  **Recursos Nativos:**
    * **Galería (Photo Picker):** Selección de foto de perfil nativa.
    * **GPS (Intents):** Apertura de Google Maps para rutas de reparto de forma segura y funcional.
    * **Feedback Háptico:** Sistema de vibración al confirmar pedidos.
7.  **Pruebas Unitarias:** Cobertura de lógica de negocio en ViewModel validando estados y roles.

---

## Endpoints Utilizados

### 1. Microservicios Propios (Spring Boot)
Base URL: `https://apppizzabackend-production.up.railway.app/api/`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **GET** | `/pizzas` | Obtiene el menú completo de pizzas desde la BD remota |
| **POST** | `/pedidos` | Crea una nueva orden de compra |
| **GET** | `/pedidos/cliente/{id}` | Obtiene el historial específico de un cliente |
| **GET** | `/pedidos` | Historial global de comandas para Staff (Sanji/Usopp) |
| **PUT** | `/pedidos/{id}/estado` | Actualiza el flujo del pedido (PREPARANDO -> ENVIADO -> COMPLETO) |
| **DELETE** | `/pedidos/{id}` | Elimina o cancela un pedido del sistema |
| **POST** | `/clientes/login` | Autenticación de usuarios y staff |
| **POST** | `/clientes/registro` | Registro de nuevos clientes |
| **POST** | `/clientes/guardar` | Actualiza los datos del perfil (incluyendo la foto) |
| **DELETE** | `/clientes/{id}` | Elimina permanentemente la cuenta de un usuario |

### 2. API Externa
| Servicio | Endpoint | Descripción                        |
| :--- | :--- |:-----------------------------------|
| **PokeAPI** | `https://pokeapi.co/api/v2/pokemon/{id}` | Pokemon visual del día             |
| **OpenMeteo** | `https://api.open-meteo.com/v1/` | Clima en tiempo real para despacho |

---

## Instrucciones de Ejecución

### 1. Backend (Spring Boot)
* Abrir el proyecto en IntelliJ o Eclipse.
* Ejecutar la clase principal `PixzeleriaApplication.kt` y esperar confirmación en el puerto 8081.
* Como el backend está en Railway, de momento se puede saltar al paso del Frontend.

### 2. Frontend (Android)
* Abrir el proyecto en **Android Studio**.
* Ejecutar en emulador (API 26 o superior).
* **Pruebas:** Para validar la lógica, ejecutar el archivo `MainViewModelTest`.

---

## Evidencia de Entrega (APK Firmado)

El proyecto incluye la configuración técnica necesaria para su distribución:
* **APK Firmado:** Ubicado en la carpeta `app/release/app-release.apk`.
* **Llave de Firma:** Archivo `llave_definitiva.jks` ubicado en la configuración técnica del repositorio.

---
Desarrollado para la asignatura **DSY1105 - Desarrollo de Aplicaciones Móviles**.
*Evidencia de trabajo propio, no se lo robe, no sea malito.*.
