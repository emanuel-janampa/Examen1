package pe.upeu.biblioandes.presentation.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.presentation.components.BarraSuperior
import pe.upeu.biblioandes.presentation.components.EstadoCargando
import pe.upeu.biblioandes.presentation.components.EstadoVacio

/**
 * RF-06: datos del estudiante y conmutador de tema.
 * El estado del tema NO vive aquí: se recibe (esModoOscuro) y se notifica (onCambiarTema);
 * lo posee AppNavHost, que está por encima del tema en el árbol de composición.
 */
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    esModoOscuro: Boolean,
    onCambiarTema: (Boolean) -> Unit,
    onVolver: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { BarraSuperior(titulo = "Perfil y ajustes", onVolver = onVolver) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val estado = state) {
                is PerfilUiState.Cargando -> EstadoCargando()
                is PerfilUiState.Error -> EstadoVacio(estado.mensaje)
                is PerfilUiState.Contenido -> PerfilContenido(
                    estudiante = estado.estudiante,
                    esModoOscuro = esModoOscuro,
                    onCambiarTema = onCambiarTema
                )
            }
        }
    }
}

@Composable
private fun PerfilContenido(
    estudiante: Estudiante,
    esModoOscuro: Boolean,
    onCambiarTema: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = estudiante.nombre, style = MaterialTheme.typography.titleLarge)
                DatoPerfil("Código", estudiante.codigo)
                DatoPerfil("Carrera", estudiante.carrera)
                DatoPerfil("Correo", estudiante.correo)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Modo oscuro", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = esModoOscuro, onCheckedChange = onCambiarTema)
            }
        }
    }
}

@Composable
private fun DatoPerfil(etiqueta: String, valor: String) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = valor, style = MaterialTheme.typography.bodyLarge)
    }
}
