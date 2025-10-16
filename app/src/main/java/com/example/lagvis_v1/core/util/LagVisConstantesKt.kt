// core/util/LagVisConstantesKt.kt
package com.example.lagvis_v1.core.util

object LagVisConstantesKt {

    // ===== BASE URLs =====
    @JvmField val BASE_URL2: String =
        "https://lagvis-backend-service-160368818046.europe-southwest1.run.app"

    // ===== Endpoints =====
    @JvmField val ENDPOINT_INSERTAR: String = "$BASE_URL2/insertar_.php"
    @JvmField val ENDPOINT_MOSTRAR:  String = "$BASE_URL2/mostrar_.php"
    @JvmField val ENDPOINT_ELIMINAR: String = "$BASE_URL2/eliminar_.php"
    @JvmField val ENDPOINT_GUARDAR_NOTICIA: String = "$BASE_URL2/guardar_noticia.php"
    @JvmField val ENDPOINT_LISTAR_NOTICIAS: String = "$BASE_URL2/listar_noticias_guardadas.php"
    @JvmField val ENDPOINT_INSERTAR_VALORACION: String = "$BASE_URL2/insertar_valoracion.php"

    // ===== Claves exactas que espera el backend (-> ID) =====
    private val COMUNIDAD_ID = mapOf(
        "Andalucía" to 1, "Aragón" to 2, "Asturias" to 3, "Illes Balears" to 4, "Canarias" to 5,
        "Cantabria" to 6, "Castilla y León" to 7, "Castilla-La Mancha" to 8, "Cataluña" to 9,
        "Comunidad Valenciana" to 10, "Extremadura" to 11, "Galicia" to 12, "La Rioja" to 13,
        "Comunidad de Madrid" to 14, "Región de Murcia" to 15, "Navarra" to 16, "País Vasco" to 17,
        "Ceuta" to 18, "Melilla" to 19
    )

    private val SECTOR_ID = mapOf(
        "Hosteleria" to 1,
        "Construcción" to 2,
        "Call Center" to 3,
        "Oficinas y Despachos" to 4,
        "Ayuda a Domicilio" to 5,
        "Comercio Vario" to 6,
        "Limpieza Edificios Y Locales" to 7,
        "Metal" to 8,
        "Transporte de Mercancias" to 9,
        "Centros Enseñanza Privada" to 10,
        "Seguridad Privada" to 11
    )

    // ===== Alias (lo que pones bonito en UI → clave backend) =====
    // Normalizamos a lowercase sin espacios extremos para comparar.
    private fun norm(s: String?) = s?.trim()?.lowercase() ?: ""

    private val COMUNIDAD_ALIAS = mapOf(
        "baleares" to "Illes Balears",
        "illes balears" to "Illes Balears",
        "catalunya" to "Cataluña",
        "comunidad valenciana" to "Comunidad Valenciana",
        "madrid" to "Comunidad de Madrid",
        "murcia" to "Región de Murcia",
    )

    private val SECTOR_ALIAS = mapOf(
        "hostelería y turismo" to "Hosteleria",
        "hosteleria y turismo" to "Hosteleria",
        "transporte y logística" to "Transporte de Mercancias",
        "transporte y logistica" to "Transporte de Mercancias",
        "limpieza" to "Limpieza Edificios Y Locales",
        "comercio" to "Comercio Vario",
        "educación" to "Centros Enseñanza Privada",
        "educacion" to "Centros Enseñanza Privada"
    )

    // ===== Listas de UI (puedes mostrarlas al usuario) =====
    // Comunidades “bonitas” (incluye Baleares como alias común)
    @JvmField
    val comunidadesUi: List<String> = listOf(
        "Andalucía", "Aragón", "Asturias", "Baleares", "Canarias", "Cantabria",
        "Castilla y León", "Castilla-La Mancha", "Cataluña", "Comunidad Valenciana",
        "Extremadura", "Galicia", "La Rioja", "Comunidad de Madrid", "Región de Murcia",
        "Navarra", "País Vasco", "Ceuta", "Melilla"
    )

    // Sectores “bonitos”
    @JvmField
    val sectoresUi: List<String> = listOf(
        "Hostelería y Turismo",
        "Construcción",
        "Call Center",
        "Oficinas y Despachos",
        "Ayuda a Domicilio",
        "Comercio",
        "Limpieza",
        "Metal",
        "Transporte y Logística",
        "Centros Enseñanza Privada",
        "Seguridad Privada"
    )

    // ===== APIs públicas: devuelven ID como String (o "-1") =====
    @JvmStatic
    fun getComunidadIdStr(nombre: String?): String {
        if (nombre.isNullOrBlank()) return "-1"
        val key = COMUNIDAD_ALIAS[norm(nombre)] ?: nombre
        val id = COMUNIDAD_ID[key] ?: return "-1"
        return id.toString()
    }

    @JvmStatic
    fun getSectorIdStr(nombre: String?): String {
        if (nombre.isNullOrBlank()) return "-1"
        val key = SECTOR_ALIAS[norm(nombre)] ?: nombre
        val id = SECTOR_ID[key] ?: return "-1"
        return id.toString()
    }
}
