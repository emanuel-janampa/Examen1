package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.FiltroPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo

class FiltrarPrestamosUseCase {
    operator fun invoke(prestamos: List<Prestamo>, filtro: FiltroPrestamo): List<Prestamo> =
        when (filtro) {
            FiltroPrestamo.Todos -> prestamos
            FiltroPrestamo.Activo -> prestamos.filter { it.estado is EstadoPrestamo.Activo }
            FiltroPrestamo.Devuelto -> prestamos.filter { it.estado is EstadoPrestamo.Devuelto }
            FiltroPrestamo.Vencido -> prestamos.filter { it.estado is EstadoPrestamo.Vencido }
        }
}
