package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.util.Reloj

/** Permite "regularizar" un préstamo (RN-04): registra su devolución con la fecha de hoy. */
class DevolverPrestamoUseCase(
    private val repository: BibliotecaRepository,
    private val reloj: Reloj
) {
    suspend operator fun invoke(prestamoId: Int): Result<Unit> {
        val prestamo = repository.obtenerPrestamosActuales().firstOrNull { it.id == prestamoId }
            ?: return Result.failure(IllegalArgumentException("Préstamo no encontrado."))

        if (prestamo.estado is EstadoPrestamo.Devuelto) {
            return Result.failure(IllegalStateException("El préstamo ya fue devuelto."))
        }

        repository.registrarDevolucion(prestamoId, reloj.hoy().toString())
        return Result.success(Unit)
    }
}
