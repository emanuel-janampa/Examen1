package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo

/** RF-01: el préstamo pendiente (no devuelto) cuya devolución vence primero. */
class ObtenerPrestamoProximoUseCase(private val obtenerPrestamos: ObtenerPrestamosUseCase) {
    operator fun invoke(): Flow<Prestamo?> =
        obtenerPrestamos().map { lista ->
            lista
                .filter { it.estado !is EstadoPrestamo.Devuelto }
                .minByOrNull { it.fechaLimite }
        }
}
