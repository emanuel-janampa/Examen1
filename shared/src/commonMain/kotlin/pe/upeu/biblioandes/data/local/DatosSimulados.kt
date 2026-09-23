package pe.upeu.biblioandes.data.local

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import pe.upeu.biblioandes.data.util.RelojSistema
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * Datos semilla en memoria (anexo del examen).
 * Las fechas se calculan respecto a HOY para que los préstamos Activos
 * siempre queden en el futuro el día de la evaluación (RN-03).
 */
object DatosSimulados {

    private val hoy: LocalDate = RelojSistema().hoy()

    private fun fecha(diasDesdeHoy: Int): String =
        hoy.plus(diasDesdeHoy, DateTimeUnit.DAY).toString()

    val estudiante = Estudiante(
        codigo = "E-2291",
        nombre = "Emanuel Janampa",
        carrera = "Ingeniería de Sistemas",
        correo = "emanuel.janampa@correo.pe"
    )

    val categorias = listOf("Programación", "Matemática", "Redes", "Gestión", "Literatura")

    val libros = listOf(
        Libro(1, "Kotlin en profundidad", "M. Salazar", 2023, "Programación", "Central", 3),
        Libro(2, "Estructuras de datos", "R. Peña", 2021, "Programación", "Central", 0),
        Libro(3, "Cálculo aplicado", "L. Ortega", 2019, "Matemática", "Sede Norte", 2),
        Libro(4, "Redes de computadoras", "A. Medina", 2022, "Redes", "Sede Sur", 4),
        Libro(5, "Seguridad en redes", "P. Ríos", 2024, "Redes", "Central", 0),
        Libro(6, "Gestión de proyectos", "S. Delgado", 2021, "Gestión", "Sede Norte", 2),
        Libro(7, "Sistemas operativos", "A. Tanenbaum", 2020, "Programación", "Sede Sur", 5),
        Libro(8, "Álgebra lineal", "G. Strang", 2018, "Matemática", "Central", 1),
        Libro(9, "Enrutamiento Cisco CCNA", "C. Gómez", 2023, "Redes", "Sede Norte", 3),
        Libro(10, "Liderazgo estratégico", "M. Porter", 2022, "Gestión", "Central", 2),
        Libro(11, "Tradiciones peruanas", "R. Palma", 2015, "Literatura", "Sede Sur", 4),
        Libro(12, "Don Quijote de la Mancha", "M. Cervantes", 2016, "Literatura", "Central", 2)
    )

    // Dos Activos, dos Devueltos y uno Vencido (sección 3.3).
    val prestamos = listOf(
        Prestamo(1, libros[0], fecha(-2), fecha(5), EstadoPrestamo.Activo(5)),
        Prestamo(2, libros[3], fecha(-1), fecha(6), EstadoPrestamo.Activo(6)),
        Prestamo(3, libros[2], fecha(-34), fecha(-27), EstadoPrestamo.Devuelto(fecha(-28))),
        Prestamo(4, libros[1], fecha(-49), fecha(-42), EstadoPrestamo.Devuelto(fecha(-43))),
        Prestamo(5, libros[5], fecha(-25), fecha(-18), EstadoPrestamo.Vencido(18))
    )
}
