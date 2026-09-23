package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

class ObtenerLibroUseCase(private val repository: BibliotecaRepository) {
    operator fun invoke(id: Int): Flow<Libro?> =
        repository.getLibros().map { libros -> libros.firstOrNull { it.id == id } }
}
