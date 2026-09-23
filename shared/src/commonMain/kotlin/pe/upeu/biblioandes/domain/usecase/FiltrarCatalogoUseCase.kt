package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.util.normalizado

/**
 * RF-02 + RF-05: filtra el catálogo por categoría y por texto (título o autor),
 * sin distinguir mayúsculas ni tildes. Función pura: no toca la interfaz.
 */
class FiltrarCatalogoUseCase {
    operator fun invoke(libros: List<Libro>, categoria: String?, consulta: String): List<Libro> {
        val texto = consulta.trim().normalizado()
        return libros.filter { libro ->
            val coincideCategoria = categoria == null || libro.categoria == categoria
            val coincideTexto = texto.isEmpty() ||
                libro.titulo.normalizado().contains(texto) ||
                libro.autor.normalizado().contains(texto)
            coincideCategoria && coincideTexto
        }
    }
}
