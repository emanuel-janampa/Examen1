package pe.upeu.biblioandes.presentation.prestamos

import pe.upeu.biblioandes.domain.model.FiltroPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.presentation.components.EstadoPantalla

data class PrestamosUiState(
    val estado: EstadoPantalla = EstadoPantalla.Cargando,
    val prestamos: List<Prestamo> = emptyList(),
    val filtro: FiltroPrestamo = FiltroPrestamo.Todos,
    val mensajeError: String = ""
)
