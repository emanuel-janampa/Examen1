package pe.upeu.biblioandes.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import pe.upeu.biblioandes.data.local.DatosSimulados
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.rules.ReglasPrestamo

/**
 * Implementación SIMULADA (en memoria) de [BibliotecaRepository].
 *
 * - Simula la latencia de red con delay(800 ms) usando corrutinas (no bloquea el hilo principal).
 * - [simularErrorCatalogo]: bandera para activar el estado de error del catálogo (sección 3.2).
 *   Para probarlo, cámbiala a true (aquí o en AppModule) y vuelve a ejecutar.
 * - No valida reglas de negocio: eso vive en domain/rules.
 */
class BibliotecaRepositoryFake(
    var simularErrorCatalogo: Boolean = false
) : BibliotecaRepository {

    private val libros = MutableStateFlow(DatosSimulados.libros)
    private val prestamos = MutableStateFlow(DatosSimulados.prestamos)

    override fun getEstudiante(): Flow<Estudiante> = flow {
        delay(RETARDO_CARGA_MS)
        emit(DatosSimulados.estudiante)
    }

    override fun getCategorias(): Flow<List<String>> = flow {
        delay(RETARDO_CARGA_MS)
        emit(DatosSimulados.categorias)
    }

    override fun getLibros(): Flow<List<Libro>> = flow {
        delay(RETARDO_CARGA_MS)
        if (simularErrorCatalogo) {
            throw IllegalStateException("No se pudo cargar el catálogo. Inténtalo de nuevo.")
        }
        emitAll(libros)
    }

    override fun getPrestamos(): Flow<List<Prestamo>> = flow {
        delay(RETARDO_CARGA_MS)
        emitAll(prestamos)
    }

    override suspend fun obtenerLibro(id: Int): Libro? =
        libros.value.firstOrNull { it.id == id }

    override suspend fun obtenerPrestamosActuales(): List<Prestamo> = prestamos.value

    override suspend fun registrarPrestamo(
        libroId: Int,
        fechaPrestamo: String,
        fechaLimite: String
    ): Prestamo {
        delay(RETARDO_ESCRITURA_MS)

        val libro = libros.value.first { it.id == libroId }
        val libroActualizado = libro.copy(ejemplaresDisponibles = libro.ejemplaresDisponibles - 1)
        libros.update { lista ->
            lista.map { if (it.id == libroId) libroActualizado else it }
        }

        val nuevo = Prestamo(
            id = (prestamos.value.maxOfOrNull { it.id } ?: 0) + 1,
            libro = libroActualizado,
            fechaPrestamo = fechaPrestamo,
            fechaLimite = fechaLimite,
            estado = EstadoPrestamo.Activo(ReglasPrestamo.DIAS_PRESTAMO)
        )
        prestamos.update { lista -> lista + nuevo }
        return nuevo
    }

    override suspend fun registrarDevolucion(prestamoId: Int, fechaDevolucion: String) {
        delay(RETARDO_ESCRITURA_MS)

        val prestamo = prestamos.value.firstOrNull { it.id == prestamoId } ?: return
        prestamos.update { lista ->
            lista.map {
                if (it.id == prestamoId) it.copy(estado = EstadoPrestamo.Devuelto(fechaDevolucion)) else it
            }
        }
        libros.update { lista ->
            lista.map {
                if (it.id == prestamo.libro.id) it.copy(ejemplaresDisponibles = it.ejemplaresDisponibles + 1) else it
            }
        }
    }

    private companion object {
        const val RETARDO_CARGA_MS = 800L
        const val RETARDO_ESCRITURA_MS = 500L
    }
}
