package pe.upeu.biblioandes.presentation.catalogo

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
import pe.upeu.biblioandes.domain.usecase.FiltrarCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerCategoriasUseCase
import pe.upeu.biblioandes.presentation.components.EstadoPantalla

class CatalogoViewModel(
    private val obtenerCatalogo: ObtenerCatalogoUseCase,
    private val obtenerCategorias: ObtenerCategoriasUseCase,
    private val filtrarCatalogo: FiltrarCatalogoUseCase
) : ViewModel() {

    // Estado mutable PRIVADO; la UI solo ve el StateFlow de solo lectura.
    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    private var catalogoCompleto: List<Libro> = emptyList()
    private var jobCarga: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        jobCarga?.cancel()
        // viewModelScope se cancela solo si la pantalla se destruye definitivamente.
        jobCarga = viewModelScope.launch {
            _uiState.update { it.copy(estado = EstadoPantalla.Cargando, mensajeError = "") }

            combine(obtenerCatalogo(), obtenerCategorias()) { libros, categorias ->
                libros to categorias
            }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            estado = EstadoPantalla.Error,
                            mensajeError = error.message ?: "Error desconocido"
                        )
                    }
                }
                .collect { (libros, categorias) ->
                    catalogoCompleto = libros
                    _uiState.update { it.copy(categorias = categorias) }
                    publicar()
                }
        }
    }

    fun seleccionarCategoria(categoria: String?) {
        _uiState.update { it.copy(categoriaSeleccionada = categoria) }
        publicar()
    }

    fun buscar(consulta: String) {
        _uiState.update { it.copy(consulta = consulta) }
        publicar()
    }

    /** Aplica categoría + búsqueda (regla de filtrado en el caso de uso, no en el composable). */
    private fun publicar() {
        _uiState.update { actual ->
            val visibles = filtrarCatalogo(
                libros = catalogoCompleto,
                categoria = actual.categoriaSeleccionada,
                consulta = actual.consulta
            )
            actual.copy(
                libros = visibles,
                estado = if (visibles.isEmpty()) EstadoPantalla.Vacio else EstadoPantalla.Contenido
            )
        }
    }
}
