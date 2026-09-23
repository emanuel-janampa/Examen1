package pe.upeu.biblioandes

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.usecase.FiltrarCatalogoUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class FiltrarCatalogoUseCaseTest {

    private val filtrar = FiltrarCatalogoUseCase()

    private val libros = listOf(
        Libro(1, "Cálculo aplicado", "L. Ortega", 2019, "Matemática", "Sede Norte", 2),
        Libro(2, "Redes de computadoras", "A. Medina", 2022, "Redes", "Sede Sur", 4),
        Libro(3, "Estructuras de datos", "R. Peña", 2021, "Programación", "Central", 0)
    )

    @Test
    fun busqueda_ignora_tildes_y_mayusculas_en_el_titulo() {
        assertEquals(listOf(1), filtrar(libros, null, "CALCULO").map { it.id })
    }

    @Test
    fun busqueda_por_autor() {
        assertEquals(listOf(2), filtrar(libros, null, "medina").map { it.id })
    }

    @Test
    fun filtro_de_categoria_se_combina_con_la_busqueda() {
        assertEquals(emptyList(), filtrar(libros, "Redes", "calculo").map { it.id })
        assertEquals(listOf(2), filtrar(libros, "Redes", "redes").map { it.id })
    }

    @Test
    fun sin_criterios_devuelve_todo() {
        assertEquals(3, filtrar(libros, null, "").size)
    }
}
