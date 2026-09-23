package pe.upeu.biblioandes.di

import org.koin.core.context.startKoin

/**
 * Arranca Koin con los módulos de la app.
 * Se llama una vez desde cada plataforma:
 *  - Android: BiblioAndesApp.onCreate()
 *  - iOS: iOSApp.init() (a través de doInitKoin() en MainViewController.kt)
 */
fun initKoin() {
    startKoin {
        modules(appModule)
    }
}
