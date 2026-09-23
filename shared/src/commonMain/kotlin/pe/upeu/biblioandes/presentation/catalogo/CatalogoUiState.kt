package pe.upeu.biblioandes.presentation.catalogo

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.presentation.components.EstadoPantalla

/**
 * Estado de la pantalla del catálogo (lo que la UI necesita para dibujarse).
 * Distinto del modelo de dominio [Libro]: aquí también viven la categoría elegida,
 * el texto de búsqueda y el estado de carga/vacío/error.
 */
data class CatalogoUiState(
    val estado: EstadoPantalla = EstadoPantalla.Cargando,
    val libros: List<Libro> = emptyList(),
    val categorias: List<String> = emptyList(),
    val categoriaSeleccionada: String? = null,   // null = "Todas"
    val consulta: String = "",
    val mensajeError: String = ""
)
