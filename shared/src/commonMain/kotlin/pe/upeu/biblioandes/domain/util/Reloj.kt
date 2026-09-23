package pe.upeu.biblioandes.domain.util

import kotlinx.datetime.LocalDate

/**
 * Abstracción de "la fecha de hoy". Permite que las reglas de negocio (RN-03)
 * sean deterministas y fáciles de probar. La implementación real vive en data.
 */
fun interface Reloj {
    fun hoy(): LocalDate
}
