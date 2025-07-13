package com.example.lagvis_v1;

import android.os.Build;

public class LagVisConstantes {
   // public static final String BASE_URL = BuildConfig.API_BASE_URL;

    public static final String BASE_URL2 = "https://lagvis-backend-service-160368818046.europe-southwest1.run.app";

    // Url para scripts php
    public static final String ENDPOINT_MOSTRAR = BASE_URL2 + "/mostrar_.php";
    public static final String ENDPOINT_INSERTAR = BASE_URL2 + "/insertar_.php";
    //public static final String ENDPOINT_ACTUALIZAR = BASE_URL + "/actualizar_.php"; No lo uso
    public static final String ENDPOINT_ELIMINAR = BASE_URL2 + "/eliminar_.php";
    public static final String ENDPOINT_GUARDAR_NOTICIA = BASE_URL2 + "/guardar_noticia.php";
    public static final String ENDPOINT_LISTAR_NOTICIAS = BASE_URL2 + "/listar_noticias_guardadas.php";
    //   public static final String ENDPOINT_OBTENER_NOTICIAS = BASE_URL + "/obtener_noticias_guardadas.php";

    public static final String ENDPOINT_INSERTAR_VALORACION = BASE_URL2 + "/insertar_valoracion.php";

    /**
     * Devuelve el ID de un sector a partir de su nombre.
     *
     * @param nombreSector El nombre del sector (ej: "Hosteleria").
     * @return El ID correspondiente como un entero (int). Devuelve -1 si no se encuentra.
     */
    public static int getSectorId(String nombreSector) {
        if (nombreSector == null) {
            return -1;
        }
        switch (nombreSector) {
            case "Hosteleria":
                return 1;
            case "Construcción":
                return 2;
            case "Call Center":
                return 3;
            case "Oficinas y Despachos":
                return 4;
            case "Ayuda a Domicilio":
                return 5;
            case "Comercio Vario":
                return 6;
            case "Limpieza Edificios Y Locales":
                return 7;
            case "Metal":
                return 8;
            case "Transporte de Mercancias":
                return 9;
            case "Centros Enseñanza Privada":
                return 10;
            case "Seguridad Privada":
                return 11;
            default:
                return -1; // Valor por defecto si no se encuentra el sector
        }
    }

    /**
     * Devuelve el ID de una comunidad autónoma a partir de su nombre.
     *
     * @param nombreComunidad El nombre de la comunidad (ej: "Comunidad de Madrid").
     * @return El ID correspondiente como un entero (int). Devuelve -1 si no se encuentra.
     */
    public static int getComunidadId(String nombreComunidad) {
        if (nombreComunidad == null) {
            return -1;
        }

        switch (nombreComunidad) {
            case "Andalucía":
                return 1;
            case "Aragón":
                return 2;
            case "Asturias":
                return 3;
            case "Illes Balears":
                return 4;
            case "Canarias":
                return 5;
            case "Cantabria":
                return 6;
            case "Castilla y León":
                return 7;
            case "Castilla-La Mancha":
                return 8;
            case "Cataluña":
                return 9;
            case "Comunidad Valenciana":
                return 10;
            case "Extremadura":
                return 11;
            case "Galicia":
                return 12;
            case "La Rioja":
                return 13;
            case "Comunidad de Madrid":
                return 14;
            case "Región de Murcia":
                return 15;
            case "Navarra":
                return 16;
            case "País Vasco":
                return 17;
            case "Ceuta":
                return 18;
            case "Melilla":
                return 19;
            default:
                return -1;
        }
    }


}
