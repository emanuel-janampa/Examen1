package pe.upeu.biblioandes.data.util

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import pe.upeu.biblioandes.domain.util.Reloj

/** Implementación real de [Reloj]: la fecha de hoy según el reloj del dispositivo. */
@OptIn(ExperimentalTime::class)
class RelojSistema : Reloj {
    override fun hoy(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
}
