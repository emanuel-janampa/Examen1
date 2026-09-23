package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

class ObtenerCatalogoUseCase(private val repository: BibliotecaRepository) {
    operator fun invoke(): Flow<List<Libro>> = repository.getLibros()
}
