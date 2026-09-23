package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.presentation.catalogo.CatalogoScreen
import pe.upeu.biblioandes.presentation.components.Iconos
import pe.upeu.biblioandes.presentation.detalle.DetalleLibroScreen
import pe.upeu.biblioandes.presentation.inicio.InicioScreen
import pe.upeu.biblioandes.presentation.perfil.PerfilScreen
import pe.upeu.biblioandes.presentation.prestamos.PrestamosScreen
import pe.upeu.biblioandes.presentation.theme.BiblioAndesTheme

/**
 * Raíz de la aplicación: tema + Scaffold con barra inferior + NavHost.
 * El tema se aplica AQUÍ, en la raíz, para que el cambio claro/oscuro llegue a todas las pantallas.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val sistemaOscuro = isSystemInDarkTheme()
    // Estado del tema elevado a la raíz; rememberSaveable lo conserva al rotar la pantalla.
    var esModoOscuro by rememberSaveable { mutableStateOf(sistemaOscuro) }

    BiblioAndesTheme(darkTheme = esModoOscuro) {
        val entradaActual by navController.currentBackStackEntryAsState()
        val rutaActual = entradaActual?.destination?.route

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (DestinosBarraInferior.any { it.ruta == rutaActual }) {
                    BarraInferior(
                        rutaActual = rutaActual,
                        onDestino = { navController.irATab(it.ruta) }
                    )
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Destino.Inicio.ruta,
                modifier = Modifier.padding(padding).consumeWindowInsets(padding)
            ) {
                composable(Destino.Inicio.ruta) {
                    InicioScreen(
                        viewModel = koinViewModel(),
                        onIrACatalogo = { navController.irATab(Destino.Catalogo.ruta) },
                        onIrAPrestamos = { navController.irATab(Destino.Prestamos.ruta) },
                        onIrAPerfil = { navController.navigate(Destino.Perfil.ruta) }
                    )
                }
                composable(Destino.Catalogo.ruta) {
                    CatalogoScreen(
                        viewModel = koinViewModel(),
                        onLibroClick = { id ->
                            navController.navigate(Destino.DetalleLibro.crearRuta(id))
                        }
                    )
                }
                composable(Destino.Prestamos.ruta) {
                    PrestamosScreen(viewModel = koinViewModel())
                }
                composable(Destino.Perfil.ruta) {
                    PerfilScreen(
                        viewModel = koinViewModel(),
                        esModoOscuro = esModoOscuro,
                        onCambiarTema = { esModoOscuro = it },
                        onVolver = { navController.popBackStack() }
                    )
                }
                composable(Destino.DetalleLibro.ruta) { entrada ->
                    val libroId = entrada.arguments
                        ?.read { getString(ARG_LIBRO_ID) }
                        ?.toIntOrNull()
                        ?: -1
                    DetalleLibroScreen(
                        libroId = libroId,
                        viewModel = koinViewModel(),
                        onVolver = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

private class ItemBarra(val destino: Destino, val icono: ImageVector)

@Composable
private fun BarraInferior(rutaActual: String?, onDestino: (Destino) -> Unit) {
    val items = listOf(
        ItemBarra(Destino.Inicio, Iconos.Inicio),
        ItemBarra(Destino.Catalogo, Iconos.Catalogo),
        ItemBarra(Destino.Prestamos, Iconos.Prestamos)
    )
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.destino.ruta,
                onClick = { onDestino(item.destino) },
                icon = { Icon(imageVector = item.icono, contentDescription = null) },
                label = { Text(item.destino.titulo) }
            )
        }
    }
}

/** Navegación entre pestañas: conserva el estado de cada una y evita apilar duplicados. */
private fun NavHostController.irATab(ruta: String) {
    navigate(ruta) {
        popUpTo(Destino.Inicio.ruta) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
