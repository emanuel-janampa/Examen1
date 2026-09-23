package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

class ObtenerEstudianteUseCase(private val repository: BibliotecaRepository) {
    operator fun invoke(): Flow<Estudiante> = repository.getEstudiante()
}
