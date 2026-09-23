package pe.upeu.biblioandes.domain.rules

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * Las cuatro reglas de negocio del caso BiblioAndes (sección 3.1 del examen).
 * Es la ÚNICA fuente de verdad: ni la interfaz ni el repositorio las duplican.
 *
 *  - RN-01: máximo 3 préstamos Activos simultáneos.
 *  - RN-02: no se solicita un libro con 0 ejemplares disponibles.
 *  - RN-03: todo préstamo dura 7 días; si la fecha límite pasó, es Vencido.
 *  - RN-04: con al menos un préstamo Vencido no se puede solicitar otro libro.
 */
object ReglasPrestamo {
    const val MAX_PRESTAMOS_ACTIVOS = 3
    const val DIAS_PRESTAMO = 7

    /** RN-03: fecha límite = fecha del préstamo + 7 días. */
    fun calcularFechaLimite(fechaPrestamo: LocalDate): LocalDate =
        fechaPrestamo.plus(DIAS_PRESTAMO, DateTimeUnit.DAY)

    /** RN-03: deriva el estado real del préstamo a partir de su fecha límite y de la fecha de hoy. */
    fun calcularEstado(prestamo: Prestamo, hoy: LocalDate): EstadoPrestamo {
        val estado = prestamo.estado
        if (estado is EstadoPrestamo.Devuelto) return estado

        val diasRestantes = hoy.daysUntil(LocalDate.parse(prestamo.fechaLimite))
        return if (diasRestantes >= 0) {
            EstadoPrestamo.Activo(diasRestantes)
        } else {
            EstadoPrestamo.Vencido(-diasRestantes)
        }
    }

    fun actualizarEstado(prestamo: Prestamo, hoy: LocalDate): Prestamo =
        prestamo.copy(estado = calcularEstado(prestamo, hoy))

    fun contarActivos(prestamos: List<Prestamo>): Int =
        prestamos.count { it.estado is EstadoPrestamo.Activo }

    fun tieneVencidos(prestamos: List<Prestamo>): Boolean =
        prestamos.any { it.estado is EstadoPrestamo.Vencido }

    /** RN-01 expresada como consulta reutilizable (por ejemplo, para deshabilitar botones). */
    fun alcanzoLimiteDeActivos(prestamos: List<Prestamo>): Boolean =
        contarActivos(prestamos) >= MAX_PRESTAMOS_ACTIVOS

    /**
     * Aplica RN-02, RN-04 y RN-01 (en ese orden).
     * Recibe los préstamos con el estado YA calculado (RN-03).
     * Devuelve null si la solicitud procede.
     */
    fun validarSolicitud(libro: Libro, prestamos: List<Prestamo>): MotivoRechazo? = when {
        libro.ejemplaresDisponibles <= 0 -> MotivoRechazo.SinEjemplares
        tieneVencidos(prestamos) -> MotivoRechazo.PrestamoVencido
        alcanzoLimiteDeActivos(prestamos) -> MotivoRechazo.LimiteDePrestamosActivos
        else -> null
    }
}
