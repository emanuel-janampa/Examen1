package pe.upeu.biblioandes.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * Contrato de acceso a datos. Hoy lo implementa BibliotecaRepositoryFake (datos en memoria);
 * cuando exista la API basta con crear otra implementación y cambiar UNA línea en AppModule.
 *
 * El repositorio solo persiste y consulta: las reglas de negocio viven en domain/rules.
 */
interface BibliotecaRepository {
    fun getEstudiante(): Flow<Estudiante>
    fun getCategorias(): Flow<List<String>>
    fun getLibros(): Flow<List<Libro>>
    fun getPrestamos(): Flow<List<Prestamo>>

    suspend fun obtenerLibro(id: Int): Libro?
    suspend fun obtenerPrestamosActuales(): List<Prestamo>

    /** Persiste el préstamo y descuenta un ejemplar. No valida reglas. */
    suspend fun registrarPrestamo(libroId: Int, fechaPrestamo: String, fechaLimite: String): Prestamo

    /** Marca el préstamo como Devuelto y repone el ejemplar. No valida reglas. */
    suspend fun registrarDevolucion(prestamoId: Int, fechaDevolucion: String)
}
