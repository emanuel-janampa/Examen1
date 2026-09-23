package pe.upeu.biblioandes.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.usecase.ObtenerEstudianteUseCase

sealed interface PerfilUiState {
    data object Cargando : PerfilUiState
    data class Contenido(val estudiante: Estudiante) : PerfilUiState
    data class Error(val mensaje: String) : PerfilUiState
}

class PerfilViewModel(obtenerEstudiante: ObtenerEstudianteUseCase) : ViewModel() {

    val uiState: StateFlow<PerfilUiState> =
        obtenerEstudiante()
            .map { estudiante ->
                val estado: PerfilUiState = PerfilUiState.Contenido(estudiante)
                estado
            }
            .catch { error ->
                emit(PerfilUiState.Error(error.message ?: "No se pudo cargar el perfil."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PerfilUiState.Cargando
            )
}
