package pe.upeu.biblioandes.domain.rules

/** Motivo por el cual una solicitud de préstamo no procede. Cada uno corresponde a una regla de negocio. */
sealed class MotivoRechazo(val regla: String, val mensaje: String) {
    data object SinEjemplares : MotivoRechazo(
        regla = "RN-02",
        mensaje = "No hay ejemplares disponibles de este libro."
    )

    data object LimiteDePrestamosActivos : MotivoRechazo(
        regla = "RN-01",
        mensaje = "Ya tienes ${ReglasPrestamo.MAX_PRESTAMOS_ACTIVOS} préstamos activos, el máximo permitido."
    )

    data object PrestamoVencido : MotivoRechazo(
        regla = "RN-04",
        mensaje = "Tienes un préstamo vencido. Devuélvelo para poder solicitar otro libro."
    )
}

class SolicitudRechazadaException(val motivo: MotivoRechazo) : Exception(motivo.mensaje)
