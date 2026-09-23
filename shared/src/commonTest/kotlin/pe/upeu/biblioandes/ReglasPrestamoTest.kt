package pe.upeu.biblioandes

import kotlinx.datetime.LocalDate
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.rules.MotivoRechazo
import pe.upeu.biblioandes.domain.rules.ReglasPrestamo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReglasPrestamoTest {

    private val hoy = LocalDate(2026, 9, 23)

    private fun libro(id: Int = 1, ejemplares: Int = 1) =
        Libro(id, "Libro $id", "Autor", 2020, "Redes", "Central", ejemplares)

    private fun prestamo(id: Int, limite: String, estado: EstadoPrestamo) =
        Prestamo(id, libro(id), "2026-09-01", limite, estado)

    private fun activo(id: Int) = prestamo(id, "2026-09-30", EstadoPrestamo.Activo(7))

    @Test
    fun rn03_fecha_limite_es_siete_dias_despues() {
        assertEquals(LocalDate(2026, 9, 30), ReglasPrestamo.calcularFechaLimite(hoy))
    }

    @Test
    fun rn03_fecha_pasada_se_muestra_como_vencido() {
        val p = prestamo(1, "2026-09-20", EstadoPrestamo.Activo(0))
        assertEquals(EstadoPrestamo.Vencido(3), ReglasPrestamo.calcularEstado(p, hoy))
    }

    @Test
    fun rn03_fecha_futura_es_activo_con_dias_restantes() {
        val p = prestamo(1, "2026-09-28", EstadoPrestamo.Activo(0))
        assertEquals(EstadoPrestamo.Activo(5), ReglasPrestamo.calcularEstado(p, hoy))
    }

    @Test
    fun rn03_vence_hoy_todavia_es_activo() {
        val p = prestamo(1, "2026-09-23", EstadoPrestamo.Activo(0))
        assertEquals(EstadoPrestamo.Activo(0), ReglasPrestamo.calcularEstado(p, hoy))
    }

    @Test
    fun rn03_devuelto_no_cambia() {
        val devuelto = EstadoPrestamo.Devuelto("2026-08-11")
        val p = prestamo(1, "2026-08-12", devuelto)
        assertEquals(devuelto, ReglasPrestamo.calcularEstado(p, hoy))
    }

    @Test
    fun rn02_sin_ejemplares_se_rechaza() {
        val motivo = ReglasPrestamo.validarSolicitud(libro(ejemplares = 0), emptyList())
        assertEquals(MotivoRechazo.SinEjemplares, motivo)
    }

    @Test
    fun rn01_con_tres_activos_se_rechaza() {
        val prestamos = listOf(activo(1), activo(2), activo(3))
        val motivo = ReglasPrestamo.validarSolicitud(libro(id = 9), prestamos)
        assertEquals(MotivoRechazo.LimiteDePrestamosActivos, motivo)
    }

    @Test
    fun rn01_con_dos_activos_procede() {
        val prestamos = listOf(activo(1), activo(2))
        assertNull(ReglasPrestamo.validarSolicitud(libro(id = 9), prestamos))
    }

    @Test
    fun rn04_con_un_vencido_se_rechaza() {
        val vencido = prestamo(1, "2026-09-01", EstadoPrestamo.Vencido(22))
        val motivo = ReglasPrestamo.validarSolicitud(libro(id = 9), listOf(vencido))
        assertEquals(MotivoRechazo.PrestamoVencido, motivo)
    }

    @Test
    fun sin_restricciones_la_solicitud_procede() {
        assertNull(ReglasPrestamo.validarSolicitud(libro(), listOf(activo(1))))
    }
}
