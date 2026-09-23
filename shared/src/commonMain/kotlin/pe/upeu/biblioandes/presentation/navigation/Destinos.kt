package pe.upeu.biblioandes.presentation.navigation

/** Argumento de la ruta de detalle. */
const val ARG_LIBRO_ID = "libroId"

/** Rutas de la aplicación (constantes tipadas). */
sealed class Destino(val ruta: String, val titulo: String) {
    data object Inicio : Destino("inicio", "Inicio")
    data object Catalogo : Destino("catalogo", "Catálogo")
    data object Prestamos : Destino("prestamos", "Préstamos")
    data object Perfil : Destino("perfil", "Perfil")
    data object DetalleLibro : Destino("detalle/{$ARG_LIBRO_ID}", "Detalle") {
        fun crearRuta(libroId: Int): String = "detalle/$libroId"
    }
}

/** RF-07: los tres destinos de la barra de navegación inferior. */
val DestinosBarraInferior: List<Destino> = listOf(Destino.Inicio, Destino.Catalogo, Destino.Prestamos)
