package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.rules.ReglasPrestamo
import pe.upeu.biblioandes.domain.util.Reloj

/**
 * Devuelve los préstamos con su estado ya calculado según RN-03
 * (Activo / Vencido según la fecha de hoy), ordenados por fecha de devolución más próxima (RF-04).
 */
class ObtenerPrestamosUseCase(
    private val repository: BibliotecaRepository,
    private val reloj: Reloj
) {
    operator fun invoke(): Flow<List<Prestamo>> =
        repository.getPrestamos().map { lista ->
            val hoy = reloj.hoy()
            lista
                .map { ReglasPrestamo.actualizarEstado(it, hoy) }
                .sortedBy { it.fechaLimite }
        }
}
