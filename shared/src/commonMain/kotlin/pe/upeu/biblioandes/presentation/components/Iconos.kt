package pe.upeu.biblioandes.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Iconos propios (Material, 24dp) dibujados como ImageVector.
 * Evita depender de material-icons-extended, cuya última versión publicada para
 * Compose Multiplatform es la 1.7.3 (la 1.11.0 NO existe en Maven).
 */
object Iconos {
    val Inicio: ImageVector by lazy {
        icono("Inicio", "M10,20v-6h4v6h5v-8h3L12,3 2,12h3v8z")
    }
    val Catalogo: ImageVector by lazy {
        icono(
            "Catalogo",
            "M18,2H6c-1.1,0 -2,0.9 -2,2v16c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V4c0,-1.1 -0.9,-2 -2,-2zM6,4h5v8l-2.5,-1.5L6,12V4z"
        )
    }
    val Prestamos: ImageVector by lazy {
        icono(
            "Prestamos",
            "M3,13h2v-2H3v2zM3,17h2v-2H3v2zM3,9h2V7H3v2zM7,13h14v-2H7v2zM7,17h14v-2H7v2zM7,7v2h14V7H7z"
        )
    }
    val Perfil: ImageVector by lazy {
        icono(
            "Perfil",
            "M12,12c2.21,0 4,-1.79 4,-4s-1.79,-4 -4,-4 -4,1.79 -4,4 1.79,4 4,4zM12,14c-2.67,0 -8,1.34 -8,4v2h16v-2c0,-2.66 -5.33,-4 -8,-4z"
        )
    }
    val Buscar: ImageVector by lazy {
        icono(
            "Buscar",
            "M15.5,14h-0.79l-0.28,-0.27C15.41,12.59 16,11.11 16,9.5 16,5.91 13.09,3 9.5,3S3,5.91 3,9.5 5.91,16 9.5,16c1.61,0 3.09,-0.59 4.23,-1.57l0.27,0.28v0.79l5,4.99L20.49,19l-4.99,-5zM9.5,14C7.01,14 5,11.99 5,9.5S7.01,5 9.5,5 14,7.01 14,9.5 11.99,14 9.5,14z"
        )
    }
    val Atras: ImageVector by lazy {
        icono("Atras", "M20,11H7.83l5.59,-5.59L12,4l-8,8 8,8 1.41,-1.41L7.83,13H20v-2z")
    }
}

private fun icono(nombre: String, trazo: String): ImageVector =
    ImageVector.Builder(
        name = nombre,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).addPath(
        pathData = addPathNodes(trazo),
        fill = SolidColor(Color.Black)
    ).build()
