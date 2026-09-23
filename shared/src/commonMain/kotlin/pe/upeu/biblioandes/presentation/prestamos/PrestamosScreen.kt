package pe.upeu.biblioandes.presentation.prestamos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.upeu.biblioandes.domain.model.FiltroPrestamo
import pe.upeu.biblioandes.presentation.components.EstadoCargando
import pe.upeu.biblioandes.presentation.components.EstadoError
import pe.upeu.biblioandes.presentation.components.EstadoPantalla
import pe.upeu.biblioandes.presentation.components.EstadoVacio
import pe.upeu.biblioandes.presentation.components.FilaFiltros
import pe.upeu.biblioandes.presentation.components.PrestamoCard

@Composable
fun PrestamosScreen(viewModel: PrestamosViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PrestamosContenido(
        state = state,
        onFiltro = viewModel::seleccionarFiltro,
        onDevolver = viewModel::devolver,
        onReintentar = viewModel::cargar
    )
}

@Composable
fun PrestamosContenido(
    state: PrestamosUiState,
    onFiltro: (FiltroPrestamo) -> Unit,
    onDevolver: (Int) -> Unit,
    onReintentar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(
            text = "Mis préstamos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        when (state.estado) {
            EstadoPantalla.Cargando -> EstadoCargando()
            EstadoPantalla.Error -> EstadoError(state.mensajeError, onReintentar)
            EstadoPantalla.Contenido, EstadoPantalla.Vacio -> {
                FilaFiltros(
                    opciones = FiltroPrestamo.entries.toList(),
                    seleccionada = state.filtro,
                    etiqueta = { it.name },
                    onSeleccionar = onFiltro,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (state.estado == EstadoPantalla.Vacio) {
                    EstadoVacio("No hay préstamos para mostrar.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(state.prestamos, key = { it.id }) { prestamo ->
                            PrestamoCard(
                                prestamo = prestamo,
                                onDevolver = { onDevolver(prestamo.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
