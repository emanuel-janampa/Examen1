package pe.upeu.biblioandes.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.rules.ReglasPrestamo
import pe.upeu.biblioandes.domain.usecase.ObtenerLibroUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase
import pe.upeu.biblioandes.presentation.components.EstadoPantalla

data class DetalleUiState(
    val estado: EstadoPantalla = EstadoPantalla.Cargando,
    val libro: Libro? = null,
    /** Si no es null, la solicitud no procede y este es el motivo (viene de las reglas del dominio). */
    val motivoBloqueo: String? = null,
    val solicitando: Boolean = false,
    /** Mensaje de un solo uso (resultado de la solicitud) que la pantalla muestra en un Snackbar. */
    val mensaje: String? = null,
    val mensajeError: String = ""
)

class DetalleLibroViewModel(
    private val obtenerLibro: ObtenerLibroUseCase,
    private val obtenerPrestamos: ObtenerPrestamosUseCase,
    private val solicitarPrestamo: SolicitarPrestamoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    private var libroIdActual: Int? = null
    private var jobCarga: Job? = null

    fun cargar(libroId: Int, forzar: Boolean = false) {
        if (!forzar && libroIdActual == libroId) return
        libroIdActual = libroId

        jobCarga?.cancel()
        jobCarga = viewModelScope.launch {
            _uiState.update { DetalleUiState() }

            combine(obtenerLibro(libroId), obtenerPrestamos()) { libro, prestamos ->
                libro to prestamos
            }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            estado = EstadoPantalla.Error,
                            mensajeError = error.message ?: "No se pudo cargar el libro."
                        )
                    }
                }
                .collect { (libro, prestamos) ->
                    if (libro == null) {
                        _uiState.update {
                            it.copy(
                                estado = EstadoPantalla.Error,
                                libro = null,
                                mensajeError = "Libro no encontrado."
                            )
                        }
                    } else {
                        // Las reglas RN-01, RN-02 y RN-04 se LEEN del dominio; no se duplican aquí.
                        val motivo = ReglasPrestamo.validarSolicitud(libro, prestamos)
                        _uiState.update {
                            it.copy(
                                estado = EstadoPantalla.Contenido,
                                libro = libro,
                                motivoBloqueo = motivo?.mensaje
                            )
                        }
                    }
                }
        }
    }

    fun reintentar() {
        libroIdActual?.let { cargar(it, forzar = true) }
    }

    fun solicitar() {
        val libro = _uiState.value.libro ?: return
        if (_uiState.value.solicitando) return

        viewModelScope.launch {
            _uiState.update { it.copy(solicitando = true) }
            val resultado = solicitarPrestamo(libro.id)
            _uiState.update {
                it.copy(
                    solicitando = false,
                    mensaje = resultado.fold(
                        onSuccess = { "¡Préstamo solicitado con éxito!" },
                        onFailure = { e -> e.message ?: "No se pudo procesar la solicitud." }
                    )
                )
            }
        }
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
