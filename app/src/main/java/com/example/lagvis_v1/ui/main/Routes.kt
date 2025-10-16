package com.example.lagvis_v1.ui.main

sealed class Route(val path: String) {
    data object Home              : Route("home")               // ConvenioSelector
    data object News              : Route("news")               // NewsViewFrag
    data object Finiquitos        : Route("finiquitos")         // CalculadoraFiniquitosFragment
    data object Profile           : Route("profile")            // ProfileOnCompose
    data object VidaLaboral       : Route("vida_laboral")       // PaginaVidaLaboralFragment
    data object Despidos          : Route("despidos")           // DatosGeneralesDespidoFragment
    data object NoticiasGuardadas : Route("noticias_guardadas") // NoticiasGuardadasFragment
    data object Calendario        : Route("calendario")         // CalendarioLaboral
    data object ConvenioIa : Route("convenioIa") // ConvenioVisualizer
}
