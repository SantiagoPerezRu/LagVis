package com.example.lagvis_v1;

import android.os.Build;

public class LagVisConstantes {
    public static final String BASE_URL = BuildConfig.API_BASE_URL;

    // Url para scripts php
    public static final String ENDPOINT_MOSTRAR = BASE_URL + "/mostrar_.php";
    public static final String ENDPOINT_INSERTAR = BASE_URL + "/insertar_.php";
    //public static final String ENDPOINT_ACTUALIZAR = BASE_URL + "/actualizar_.php"; No lo uso
    public static final String ENDPOINT_ELIMINAR = BASE_URL + "/eliminar_.php";
    public static final String ENDPOINT_GUARDAR_NOTICIA = BASE_URL + "/guardar_noticia.php";
    public static final String ENDPOINT_LISTAR_NOTICIAS = BASE_URL + "/listar_noticias_guardadas.php";
 //   public static final String ENDPOINT_OBTENER_NOTICIAS = BASE_URL + "/obtener_noticias_guardadas.php";


}
