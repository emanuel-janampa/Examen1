package pe.upeu.biblioandes

import androidx.compose.ui.window.ComposeUIViewController
import pe.upeu.biblioandes.di.initKoin
import pe.upeu.biblioandes.presentation.navigation.AppNavHost

fun MainViewController() = ComposeUIViewController {
    AppNavHost()
}

/**
 * Se llama una sola vez desde Swift (por ejemplo, en el AppDelegate o el @main App),
 * antes de mostrar cualquier pantalla.
 */
fun doInitKoin() {
    initKoin()
}
