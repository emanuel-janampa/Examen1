package pe.upeu.biblioandes.presentation.prestamos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.FiltroPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.usecase.DevolverPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.FiltrarPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.presentation.components.EstadoPantalla

class PrestamosViewModel(
    private val obtenerPrestamos: ObtenerPrestamosUseCase,
    private val filtrarPrestamos: FiltrarPrestamosUseCase,
    private val devolverPrestamo: DevolverPrestamoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamosUiState())
    val uiState: StateFlow<PrestamosUiState> = _uiState.asStateFlow()

    private var todos: List<Prestamo> = emptyList()
    private var jobCarga: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        jobCarga?.cancel()
        jobCarga = viewModelScope.launch {
            _uiState.update { it.copy(estado = EstadoPantalla.Cargando, mensajeError = "") }
            obtenerPrestamos()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            estado = EstadoPantalla.Error,
                            mensajeError = error.message ?: "No se pudieron cargar los préstamos."
                        )
                    }
                }
                .collect { lista ->
                    todos = lista
                    publicar()
                }
        }
    }

    fun seleccionarFiltro(filtro: FiltroPrestamo) {
        _uiState.update { it.copy(filtro = filtro) }
        publicar()
    }

    /** Regulariza un préstamo pendiente. La lista se actualiza sola (el repositorio es reactivo). */
    fun devolver(prestamoId: Int) {
        viewModelScope.launch {
            devolverPrestamo(prestamoId)
        }
    }

    private fun publicar() {
        _uiState.update { actual ->
            val visibles = filtrarPrestamos(todos, actual.filtro)
            actual.copy(
                prestamos = visibles,
                estado = if (visibles.isEmpty()) EstadoPantalla.Vacio else EstadoPantalla.Contenido
            )
        }
    }
}
