package pe.upeu.biblioandes.presentation.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.upeu.biblioandes.presentation.components.EstadoCargando
import pe.upeu.biblioandes.presentation.components.EstadoError
import pe.upeu.biblioandes.presentation.components.EstadoPantalla
import pe.upeu.biblioandes.presentation.components.EstadoVacio
import pe.upeu.biblioandes.presentation.components.FilaFiltros
import pe.upeu.biblioandes.presentation.components.Iconos
import pe.upeu.biblioandes.presentation.components.LibroCard

@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel,
    onLibroClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CatalogoContenido(
        state = state,
        onCategoria = viewModel::seleccionarCategoria,
        onBuscar = viewModel::buscar,
        onReintentar = viewModel::cargar,
        onLibroClick = onLibroClick
    )
}

/** Versión sin ViewModel (state hoisting): solo dibuja el estado que recibe. */
@Composable
fun CatalogoContenido(
    state: CatalogoUiState,
    onCategoria: (String?) -> Unit,
    onBuscar: (String) -> Unit,
    onReintentar: () -> Unit,
    onLibroClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(
            text = "Catálogo",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        when (state.estado) {
            EstadoPantalla.Cargando -> EstadoCargando()
            EstadoPantalla.Error -> EstadoError(state.mensajeError, onReintentar)
            EstadoPantalla.Contenido, EstadoPantalla.Vacio -> {
                // RF-05: búsqueda por título o autor (RF-02: filtro por categoría con chips)
                OutlinedTextField(
                    value = state.consulta,
                    onValueChange = onBuscar,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar por título o autor") },
                    leadingIcon = { Icon(Iconos.Buscar, contentDescription = null) },
                    singleLine = true
                )

                FilaFiltros(
                    opciones = listOf<String?>(null) + state.categorias,
                    seleccionada = state.categoriaSeleccionada,
                    etiqueta = { it ?: "Todas" },
                    onSeleccionar = onCategoria,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (state.estado == EstadoPantalla.Vacio) {
                    EstadoVacio("No se encontraron libros con esos criterios.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(state.libros, key = { it.id }) { libro ->
                            LibroCard(libro = libro, onClick = { onLibroClick(libro.id) })
                        }
                    }
                }
            }
        }
    }
}
