@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package pe.upeu.biblioandes.presentation.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.presentation.components.BarraSuperior
import pe.upeu.biblioandes.presentation.components.EstadoCargando
import pe.upeu.biblioandes.presentation.components.EstadoError
import pe.upeu.biblioandes.presentation.components.EstadoPantalla

@Composable
fun DetalleLibroScreen(
    libroId: Int,
    viewModel: DetalleLibroViewModel,
    onVolver: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(libroId) {
        viewModel.cargar(libroId)
    }

    LaunchedEffect(state.mensaje) {
        val mensaje = state.mensaje
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Detalle del libro", onVolver = onVolver) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state.estado) {
                EstadoPantalla.Cargando -> EstadoCargando()
                EstadoPantalla.Error -> EstadoError(state.mensajeError, onReintentar = viewModel::reintentar)
                else -> {
                    val libro = state.libro
                    if (libro != null) {
                        ContenidoLibro(
                            libro = libro,
                            motivoBloqueo = state.motivoBloqueo,
                            solicitando = state.solicitando,
                            onSolicitar = { mostrarDialogo = true }
                        )
                        if (mostrarDialogo) {
                            DialogoConfirmacion(
                                titulo = libro.titulo,
                                onConfirmar = {
                                    mostrarDialogo = false
                                    viewModel.solicitar()
                                },
                                onCancelar = { mostrarDialogo = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContenidoLibro(
    libro: Libro,
    motivoBloqueo: String?,
    solicitando: Boolean,
    onSolicitar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = libro.titulo, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = libro.autor,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider()
            FilaDato("Año", libro.anio.toString())
            FilaDato("Categoría", libro.categoria)
            FilaDato("Sede", libro.sede)
            FilaDato("Ejemplares disponibles", libro.ejemplaresDisponibles.toString())
        }

        if (motivoBloqueo != null) {
            Text(
                text = motivoBloqueo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = onSolicitar,
            enabled = motivoBloqueo == null && !solicitando,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            Text(if (solicitando) "Solicitando…" else "Solicitar préstamo")
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = valor, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun DialogoConfirmacion(
    titulo: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Confirmar solicitud") },
        text = { Text("¿Deseas solicitar el préstamo de \"$titulo\"? Tendrás 7 días para devolverlo.") },
        confirmButton = { Button(onClick = onConfirmar) { Text("Confirmar") } },
        dismissButton = { OutlinedButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
