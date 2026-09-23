# BiblioAndes — Examen Parcial U1 (Versión B)

Aplicación **Kotlin Multiplatform + Compose Multiplatform** (Android e iOS) para consultar el catálogo de una
biblioteca, solicitar préstamos y controlar fechas de devolución. Arquitectura **Clean + MVVM** con datos
**simulados en memoria** (sin Ktor, Room, SQLDelight ni Retrofit).

## Estructura de paquetes (`shared/src/commonMain/kotlin/pe/upeu/biblioandes`)

```
domain/
  model/        Libro, Prestamo, EstadoPrestamo (sealed class), Estudiante, FiltroPrestamo
  rules/        ReglasPrestamo (RN-01..RN-04), MotivoRechazo
  repository/   BibliotecaRepository (solo la interfaz)
  usecase/      ObtenerCatalogo, ObtenerCategorias, ObtenerLibro, FiltrarCatalogo, ObtenerPrestamos,
                FiltrarPrestamos, ObtenerPrestamoProximo, ObtenerEstudiante, SolicitarPrestamo, DevolverPrestamo
  util/         Reloj (fecha de hoy), normalizado() (búsqueda sin tildes)
data/
  local/        DatosSimulados (12 libros, 5 categorías, 5 préstamos; fechas relativas a hoy)
  repository/   BibliotecaRepositoryFake (StateFlow en memoria + delay de 800 ms)
  util/         RelojSistema
presentation/
  inicio/ catalogo/ detalle/ prestamos/ perfil/   (ViewModel + UiState + Screen por pantalla)
  components/   Composables reutilizables (LibroCard, PrestamoCard, FilaFiltros, estados de carga/vacío/error…)
  navigation/   AppNavHost, Destinos
  theme/        Color, Type, BiblioAndesTheme
di/             AppModule, InitKoin
```

## Decisiones de arquitectura

* **Reglas de negocio en el dominio** (`domain/rules/ReglasPrestamo.kt`), nunca en un composable:
  * RN-01 máx. 3 préstamos activos → `alcanzoLimiteDeActivos` / `validarSolicitud`
  * RN-02 sin ejemplares → `validarSolicitud`
  * RN-03 7 días y estado Vencido → `calcularFechaLimite` / `calcularEstado` (se recalcula con la fecha de hoy en `ObtenerPrestamosUseCase`)
  * RN-04 con préstamo vencido no se solicita → `validarSolicitud`
  * `SolicitarPrestamoUseCase` valida y solo después pide al repositorio que persista.
* **Estado del préstamo como `sealed class`**: cada estado lleva un dato distinto (`diasRestantes`, `fechaDevolucion`, `diasDeAtraso`), sin propiedades vacías.
* **ViewModels** exponen `StateFlow` de solo lectura (`asStateFlow()` / `stateIn`); el `MutableStateFlow` es privado. Se obtienen con `koinViewModel()`, por lo que sobreviven a la rotación y se cancelan al destruirse la pantalla.
* **Repositorio reactivo**: al solicitar o devolver un préstamo, catálogo, detalle, inicio y préstamos se actualizan solos.
* **Tema** aplicado en la raíz (`AppNavHost`) con el estado elevado (`rememberSaveable`), así el cambio claro/oscuro llega de inmediato a toda la app.
* **Iconos propios** (`components/Iconos.kt`): `material-icons-extended` no tiene versión 1.11.0 (se congeló en 1.7.3), por eso no se usa.

## Cambiar de datos simulados a la API real

1. Crear `data/repository/BibliotecaRepositoryApi.kt` que implemente `BibliotecaRepository`.
2. Cambiar UNA línea en `di/AppModule.kt` (`single<BibliotecaRepository> { ... }`).

La interfaz de usuario y los casos de uso no se tocan.

## Estado de error del catálogo

`BibliotecaRepositoryFake(simularErrorCatalogo = true)` (en `di/AppModule.kt`) hace fallar la carga del catálogo y
muestra el estado de error con el botón «Reintentar».

## Datos de prueba y regla RN-04

El estudiante semilla tiene **un préstamo vencido**, por lo que (RN-04) no puede solicitar libros hasta regularizarlo.
En **Mis préstamos** use «Devolver» sobre el préstamo vencido y luego solicite un libro desde el detalle.

## Ejecución

* Android: `./gradlew :androidApp:assembleDebug` o ejecutar la configuración `androidApp` en Android Studio.
* iOS: abrir `iosApp/iosApp.xcodeproj` en Xcode y ejecutar (el script de build llama a `:shared:embedAndSignAppleFrameworkForXcode`).
* Pruebas: `./gradlew :shared:testAndroidHostTest` y `./gradlew :shared:iosSimulatorArm64Test`.

## Flujo de trabajo con Git

`main` ← `develop` ← `feature/<funcionalidad>-<apellido>`; solicitudes de cambio en `sc-<letra>-<apellido>`.
Mensajes con prefijos `feat`, `fix`, `refactor`, `style`, `docs`. Etiqueta final: `v1.0-unidad1`.
