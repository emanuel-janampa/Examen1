package pe.upeu.biblioandes.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import pe.upeu.biblioandes.data.repository.BibliotecaRepositoryFake
import pe.upeu.biblioandes.data.util.RelojSistema
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.usecase.DevolverPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.FiltrarCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.FiltrarPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerCategoriasUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerEstudianteUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerLibroUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamoProximoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase
import pe.upeu.biblioandes.domain.util.Reloj
import pe.upeu.biblioandes.presentation.catalogo.CatalogoViewModel
import pe.upeu.biblioandes.presentation.detalle.DetalleLibroViewModel
import pe.upeu.biblioandes.presentation.inicio.InicioViewModel
import pe.upeu.biblioandes.presentation.perfil.PerfilViewModel
import pe.upeu.biblioandes.presentation.prestamos.PrestamosViewModel

val appModule = module {
    // Infraestructura
    single<Reloj> { RelojSistema() }

    // Repositorio: cuando exista la API, SOLO se cambia esta línea por la implementación real.
    single<BibliotecaRepository> { BibliotecaRepositoryFake(simularErrorCatalogo = false) }

    // Casos de uso
    factoryOf(::ObtenerCatalogoUseCase)
    factoryOf(::ObtenerCategoriasUseCase)
    factoryOf(::ObtenerLibroUseCase)
    factoryOf(::ObtenerEstudianteUseCase)
    factoryOf(::FiltrarCatalogoUseCase)
    factoryOf(::ObtenerPrestamosUseCase)
    factoryOf(::FiltrarPrestamosUseCase)
    factoryOf(::ObtenerPrestamoProximoUseCase)
    factoryOf(::SolicitarPrestamoUseCase)
    factoryOf(::DevolverPrestamoUseCase)

    // ViewModels
    viewModelOf(::InicioViewModel)
    viewModelOf(::CatalogoViewModel)
    viewModelOf(::DetalleLibroViewModel)
    viewModelOf(::PrestamosViewModel)
    viewModelOf(::PerfilViewModel)
}
