package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

class ObtenerCategoriasUseCase(private val repository: BibliotecaRepository) {
    operator fun invoke(): Flow<List<String>> = repository.getCategorias()
}
