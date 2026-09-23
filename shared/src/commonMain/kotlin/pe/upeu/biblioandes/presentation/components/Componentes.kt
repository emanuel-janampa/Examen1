package pe.upeu.biblioandes.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

// ---------------------------------------------------------------------------
// Estados de pantalla (carga / vacío / error)
// ---------------------------------------------------------------------------

@Composable
fun EstadoCargando(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EstadoVacio(mensaje: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EstadoError(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onReintentar) { Text("Reintentar") }
        }
    }
}

// ---------------------------------------------------------------------------
// Barra superior con botón de retorno
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String, onVolver: () -> Unit) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            IconButton(onClick = onVolver) {
                Icon(imageVector = Iconos.Atras, contentDescription = "Volver")
            }
        }
    )
}

// ---------------------------------------------------------------------------
// Fila de filtros reutilizable (categorías del catálogo, estados de préstamos)
// No sabe qué filtra: recibe las opciones, la seleccionada y cómo mostrarlas.
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilaFiltros(
    opciones: List<T>,
    seleccionada: T,
    etiqueta: (T) -> String,
    onSeleccionar: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { opcion ->
            FilterChip(
                selected = opcion == seleccionada,
                onClick = { onSeleccionar(opcion) },
                label = { Text(etiqueta(opcion)) }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Tarjetas de dominio
// ---------------------------------------------------------------------------

/** Tarjeta de un libro del catálogo. Solo recibe el modelo y un callback de clic. */
@Composable
fun LibroCard(libro: Libro, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = libro.titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                text = libro.autor,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = libro.categoria,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (libro.ejemplaresDisponibles > 0) {
                        "Disponibles: ${libro.ejemplaresDisponibles}"
                    } else {
                        "Sin ejemplares"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (libro.ejemplaresDisponibles > 0) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}

/** Tarjeta de un préstamo. Si está pendiente muestra el botón "Devolver" (regularizar). */
@Composable
fun PrestamoCard(prestamo: Prestamo, onDevolver: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = prestamo.libro.titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Prestado: ${prestamo.fechaPrestamo}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Fecha límite: ${prestamo.fechaLimite}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EstadoPrestamoTexto(estado = prestamo.estado, modifier = Modifier.weight(1f))
                if (prestamo.estado !is EstadoPrestamo.Devuelto) {
                    TextButton(onClick = onDevolver) { Text("Devolver") }
                }
            }
        }
    }
}

/** Texto coloreado según el estado del préstamo (Activo / Devuelto / Vencido). */
@Composable
fun EstadoPrestamoTexto(estado: EstadoPrestamo, modifier: Modifier = Modifier) {
    val (texto, color) = when (estado) {
        is EstadoPrestamo.Activo ->
            textoActivo(estado.diasRestantes) to MaterialTheme.colorScheme.primary
        is EstadoPrestamo.Devuelto ->
            "Devuelto el ${estado.fechaDevolucion}" to MaterialTheme.colorScheme.secondary
        is EstadoPrestamo.Vencido ->
            "Vencido · ${estado.diasDeAtraso} ${plural(estado.diasDeAtraso)} de atraso" to
                MaterialTheme.colorScheme.error
    }
    Text(
        text = texto,
        color = color,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
    )
}

private fun textoActivo(diasRestantes: Int): String =
    if (diasRestantes == 0) "Activo · vence hoy"
    else if (diasRestantes == 1) "Activo · 1 día restante"
    else "Activo · $diasRestantes días restantes"

private fun plural(dias: Int): String = if (dias == 1) "día" else "días"
