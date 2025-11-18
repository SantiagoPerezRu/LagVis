object NewsCategoryTranslations {
    val EN_TO_ES: Map<String, String> = mapOf(
        "business"      to "Economía y negocios",
        "crime"         to "Sucesos",
        "domestic"      to "Nacional",
        "education"     to "Educación",
        "entertainment" to "Entretenimiento",
        "environment"   to "Medio ambiente",
        "food"          to "Gastronomía",
        "health"        to "Salud",
        "lifestyle"     to "Estilo de vida",
        "politics"      to "Política",
        "science"       to "Ciencia",
        "sports"        to "Deportes",
        "technology"    to "Tecnología",
        "top"           to "Destacadas",
        "tourism"       to "Turismo",
        "world"         to "Internacional",
        "other"         to "Otros"
    )

    /** Devuelve la traducción o "Otros" si no existe. */
    fun toSpanish(key: String?): String =
        EN_TO_ES[key?.trim()?.lowercase().orEmpty()] ?: "Otros"

    fun toEnglish(spanishLabel: String?): String? =
        EN_TO_ES.entries.firstOrNull { it.value == spanishLabel }?.key
}
