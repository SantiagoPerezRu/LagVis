package com.example.lagvis_v1.core.util;

import java.util.*;

public final class AppData {

    public static final Map<String, String> PROVINCIA_TO_SLUG = new LinkedHashMap<>();

    public static final String[] PROVINCIAS;

    static {
        PROVINCIA_TO_SLUG.put("Álava", "alava");
        PROVINCIA_TO_SLUG.put("Albacete", "albacete");
        PROVINCIA_TO_SLUG.put("Alicante", "alicante");
        PROVINCIA_TO_SLUG.put("Almería", "almeria");
        PROVINCIA_TO_SLUG.put("Asturias", "asturias");
        PROVINCIA_TO_SLUG.put("Ávila", "avila");
        PROVINCIA_TO_SLUG.put("Badajoz", "badajoz");
        PROVINCIA_TO_SLUG.put("Barcelona", "barcelona");
        PROVINCIA_TO_SLUG.put("Bizkaia", "bizkaia");
        PROVINCIA_TO_SLUG.put("Burgos", "burgos");
        PROVINCIA_TO_SLUG.put("Cáceres", "caceres");
        PROVINCIA_TO_SLUG.put("Cádiz", "cadiz");
        PROVINCIA_TO_SLUG.put("Cantabria", "cantabria");
        PROVINCIA_TO_SLUG.put("Castellón", "castellon");
        PROVINCIA_TO_SLUG.put("Ceuta", "ceuta");
        PROVINCIA_TO_SLUG.put("Ciudad Real", "ciudad-real");
        PROVINCIA_TO_SLUG.put("Córdoba", "cordoba");
        PROVINCIA_TO_SLUG.put("Cuenca", "cuenca");
        PROVINCIA_TO_SLUG.put("Gipuzkoa", "gipuzkoa");
        PROVINCIA_TO_SLUG.put("Girona", "girona");
        PROVINCIA_TO_SLUG.put("Granada", "granada");
        PROVINCIA_TO_SLUG.put("Guadalajara", "guadalajara");
        PROVINCIA_TO_SLUG.put("Huelva", "huelva");
        PROVINCIA_TO_SLUG.put("Huesca", "huesca");
        PROVINCIA_TO_SLUG.put("Illes Balears", "illes-balears");
        PROVINCIA_TO_SLUG.put("Jaén", "jaen");
        PROVINCIA_TO_SLUG.put("La Coruña", "a-coruna");
        PROVINCIA_TO_SLUG.put("La Rioja", "la-rioja");
        PROVINCIA_TO_SLUG.put("Las Palmas", "las-palmas");
        PROVINCIA_TO_SLUG.put("León", "leon");
        PROVINCIA_TO_SLUG.put("Lleida", "lleida");
        PROVINCIA_TO_SLUG.put("Lugo", "lugo");
        PROVINCIA_TO_SLUG.put("Madrid", "madrid");
        PROVINCIA_TO_SLUG.put("Málaga", "malaga");
        PROVINCIA_TO_SLUG.put("Melilla", "melilla");
        PROVINCIA_TO_SLUG.put("Murcia", "murcia");
        PROVINCIA_TO_SLUG.put("Navarra", "navarra");
        PROVINCIA_TO_SLUG.put("Ourense", "ourense");
        PROVINCIA_TO_SLUG.put("Palencia", "palencia");
        PROVINCIA_TO_SLUG.put("Pontevedra", "pontevedra");
        PROVINCIA_TO_SLUG.put("Salamanca", "salamanca");
        PROVINCIA_TO_SLUG.put("Santa Cruz de Tenerife", "santa-cruz-de-tenerife");
        PROVINCIA_TO_SLUG.put("Segovia", "segovia");
        PROVINCIA_TO_SLUG.put("Sevilla", "sevilla");
        PROVINCIA_TO_SLUG.put("Soria", "soria");
        PROVINCIA_TO_SLUG.put("Tarragona", "tarragona");
        PROVINCIA_TO_SLUG.put("Teruel", "teruel");
        PROVINCIA_TO_SLUG.put("Toledo", "toledo");
        PROVINCIA_TO_SLUG.put("Valencia", "valencia");
        PROVINCIA_TO_SLUG.put("Valladolid", "valladolid");
        PROVINCIA_TO_SLUG.put("Zamora", "zamora");
        PROVINCIA_TO_SLUG.put("Zaragoza", "zaragoza");

        // Creamos array a partir de las claves del mapa
        PROVINCIAS = PROVINCIA_TO_SLUG.keySet().toArray(new String[0]);
    }

    private AppData() { }
}
