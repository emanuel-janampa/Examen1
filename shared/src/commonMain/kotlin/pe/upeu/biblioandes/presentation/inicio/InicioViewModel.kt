package pe.upeu.biblioandes.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.usecase.ObtenerEstudianteUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamoProximoUseCase

sealed interface InicioUiState {
    data object Cargando : InicioUiState
    data class Contenido(val estudiante: Estudiante, val prestamoProximo: Prestamo?) : InicioUiState
    data class Error(val mensaje: String) : InicioUiState
}

class InicioViewModel(
    obtenerEstudiante: ObtenerEstudianteUseCase,
    obtenerPrestamoProximo: ObtenerPrestamoProximoUseCase
) : ViewModel() {

    val uiState: StateFlow<InicioUiState> =
        combine(obtenerEstudiante(), obtenerPrestamoProximo()) { estudiante, proximo ->
            val estado: InicioUiState = InicioUiState.Contenido(estudiante, proximo)
            estado
        }
            .catch { error ->
                emit(InicioUiState.Error(error.message ?: "No se pudo cargar la información."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = InicioUiState.Cargando
            )
}
