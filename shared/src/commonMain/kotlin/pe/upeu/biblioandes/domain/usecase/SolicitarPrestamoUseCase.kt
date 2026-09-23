package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.rules.ReglasPrestamo
import pe.upeu.biblioandes.domain.rules.SolicitudRechazadaException
import pe.upeu.biblioandes.domain.util.Reloj

/**
 * Orquesta la solicitud de un préstamo: primero valida las reglas de negocio
 * (RN-01, RN-02, RN-03, RN-04 en ReglasPrestamo) y solo si proceden le pide al
 * repositorio que persista.
 */
class SolicitarPrestamoUseCase(
    private val repository: BibliotecaRepository,
    private val reloj: Reloj
) {
    suspend operator fun invoke(libroId: Int): Result<Prestamo> {
        val libro = repository.obtenerLibro(libroId)
            ?: return Result.failure(IllegalArgumentException("Libro no encontrado."))

        val hoy = reloj.hoy()
        val prestamos = repository.obtenerPrestamosActuales()
            .map { ReglasPrestamo.actualizarEstado(it, hoy) }

        val motivo = ReglasPrestamo.validarSolicitud(libro, prestamos)
        if (motivo != null) {
            return Result.failure(SolicitudRechazadaException(motivo))
        }

        val prestamo = repository.registrarPrestamo(
            libroId = libro.id,
            fechaPrestamo = hoy.toString(),
            fechaLimite = ReglasPrestamo.calcularFechaLimite(hoy).toString()
        )
        return Result.success(prestamo)
    }
}
