package com.example.lagvis_v1.core.util

object LagVisConstantesKt {

//     public static final String BASE_URL = BuildConfig.API_BASE_URL;
    @JvmField
    val BASE_URL2: String =
        "https://lagvis-backend-service-160368818046.europe-southwest1.run.app"

    // Endpoints (derivados de BASE_URL2). @JvmField => accesibles como campos estáticos desde Java.
    @JvmField val ENDPOINT_MOSTRAR: String = "$BASE_URL2/mostrar_.php"
    @JvmField val ENDPOINT_INSERTAR: String = "$BASE_URL2/insertar_.php"
    // public static final String ENDPOINT_ACTUALIZAR = BASE_URL + "/actualizar_.php"; No lo uso
    @JvmField val ENDPOINT_ELIMINAR: String = "$BASE_URL2/eliminar_.php"
    @JvmField val ENDPOINT_GUARDAR_NOTICIA: String = "$BASE_URL2/guardar_noticia.php"
    @JvmField val ENDPOINT_LISTAR_NOTICIAS: String = "$BASE_URL2/listar_noticias_guardadas.php"
    @JvmField val ENDPOINT_INSERTAR_VALORACION: String = "$BASE_URL2/insertar_valoracion.php"

    // Tablas de mapeo para IDs (equivalente a los switch-case).
    private val SECTOR_ID: Map<String, Int> = mapOf(
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

    private val COMUNIDAD_ID: Map<String, Int> = mapOf(
        "Andalucía" to 1,
        "Aragón" to 2,
        "Asturias" to 3,
        "Illes Balears" to 4,
        "Canarias" to 5,
        "Cantabria" to 6,
        "Castilla y León" to 7,
        "Castilla-La Mancha" to 8,
        "Cataluña" to 9,
        "Comunidad Valenciana" to 10,
        "Extremadura" to 11,
        "Galicia" to 12,
        "La Rioja" to 13,
        "Comunidad de Madrid" to 14,
        "Región de Murcia" to 15,
        "Navarra" to 16,
        "País Vasco" to 17,
        "Ceuta" to 18,
        "Melilla" to 19
    )

    /**
     * Devuelve el ID de un sector a partir de su nombre. Devuelve -1 si no se encuentra o es null.
     */
    @JvmStatic
    fun getSectorId(nombreSector: String?): Int =
        nombreSector?.let { SECTOR_ID[it] } ?: -1

    /**
     * Devuelve el ID de una comunidad autónoma a partir de su nombre. Devuelve -1 si no se encuentra o es null.
     */
    @JvmStatic
    fun getComunidadId(nombreComunidad: String?): Int =
        nombreComunidad?.let { COMUNIDAD_ID[it] } ?: -1
}
