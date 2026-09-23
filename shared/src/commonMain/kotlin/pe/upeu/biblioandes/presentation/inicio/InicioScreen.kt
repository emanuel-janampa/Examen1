package pe.upeu.biblioandes.presentation.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.presentation.components.EstadoCargando
import pe.upeu.biblioandes.presentation.components.EstadoPrestamoTexto
import pe.upeu.biblioandes.presentation.components.EstadoVacio
import pe.upeu.biblioandes.presentation.components.Iconos

@Composable
fun InicioScreen(
    viewModel: InicioViewModel,
    onIrACatalogo: () -> Unit,
    onIrAPrestamos: () -> Unit,
    onIrAPerfil: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    InicioContenido(state, onIrACatalogo, onIrAPrestamos, onIrAPerfil)
}

@Composable
fun InicioContenido(
    state: InicioUiState,
    onIrACatalogo: () -> Unit,
    onIrAPrestamos: () -> Unit,
    onIrAPerfil: () -> Unit
) {
    when (state) {
        is InicioUiState.Cargando -> EstadoCargando()
        is InicioUiState.Error -> EstadoVacio(state.mensaje)
        is InicioUiState.Contenido -> Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¡Hola, ${state.estudiante.nombre}!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${state.estudiante.carrera} · ${state.estudiante.codigo}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onIrAPerfil) {
                    Icon(imageVector = Iconos.Perfil, contentDescription = "Perfil y ajustes")
                }
            }

            TarjetaProximoPrestamo(prestamo = state.prestamoProximo)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onIrACatalogo, modifier = Modifier.weight(1f)) {
                    Text("Ver catálogo")
                }
                OutlinedButton(onClick = onIrAPrestamos, modifier = Modifier.weight(1f)) {
                    Text("Mis préstamos")
                }
            }
        }
    }
}

/** RF-01: tarjeta destacada con el préstamo cuya devolución vence primero. */
@Composable
private fun TarjetaProximoPrestamo(prestamo: Prestamo?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Próxima devolución", style = MaterialTheme.typography.titleMedium)
            if (prestamo == null) {
                Text(
                    text = "No tienes préstamos pendientes. ¡Explora el catálogo!",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(text = prestamo.libro.titulo, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Fecha límite: ${prestamo.fechaLimite}",
                    style = MaterialTheme.typography.bodyMedium
                )
                EstadoPrestamoTexto(estado = prestamo.estado)
            }
        }
    }
}
