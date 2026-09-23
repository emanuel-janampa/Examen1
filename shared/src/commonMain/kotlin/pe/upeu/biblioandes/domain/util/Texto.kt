package pe.upeu.biblioandes.domain.util

/** Minúsculas y sin tildes, para comparar textos sin distinguir mayúsculas ni acentos (RF-05). */
fun String.normalizado(): String {
    val resultado = StringBuilder(length)
    for (c in lowercase()) {
        resultado.append(
            when (c) {
                'á', 'à', 'ä', 'â' -> 'a'
                'é', 'è', 'ë', 'ê' -> 'e'
                'í', 'ì', 'ï', 'î' -> 'i'
                'ó', 'ò', 'ö', 'ô' -> 'o'
                'ú', 'ù', 'ü', 'û' -> 'u'
                'ñ' -> 'n'
                else -> c
            }
        )
    }
    return resultado.toString()
}
