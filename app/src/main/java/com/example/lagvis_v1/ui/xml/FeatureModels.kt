package com.example.lagvis_v1.ui.xml

import androidx.annotation.DrawableRes
import com.example.lagvis_v1.R

/** Los IDs de cada feature del home (coinciden con tu navegación) */
enum class FeatureId {
    CONVENIOS_HOME,
    NEWS,
    SAVED_NEWS,
    CALENDAR,
    FINIQUITO_CALC,
    DESPIDO_CALC,
    VIDA_LABORAL,
    MI_PERFIL
}

/** Paleta para elegir el gradiente de cada tarjeta (el adapter decide el drawable) */
enum class Palette { PRIMARY, SECONDARY, TERTIARY, MIXED }

/** Modelo de cada tarjeta del grid */
data class FeatureItem(
    val id: FeatureId,
    val title: String,
    val description: String,
    @DrawableRes val iconRes: Int,
    val palette: Palette = Palette.PRIMARY
)

/** Crea la lista por defecto (ajusta textos/íconos si quieres) */
fun buildFeatureItems(): List<FeatureItem> = listOf(
    FeatureItem(
        id = FeatureId.CONVENIOS_HOME,
        title = "Convenios",
        description = "Accede a la pantalla principal de convenios.",
        iconRes = R.drawable.baseline_library_books_24,
        palette = Palette.PRIMARY
    ),
    FeatureItem(
        id = FeatureId.NEWS,
        title = "Noticias",
        description = "Actualidad y cambios en normativa laboral.",
        iconRes = R.drawable.baseline_newspaper_24,
        palette = Palette.TERTIARY
    ),
    FeatureItem(
        id = FeatureId.SAVED_NEWS,
        title = "Noticias guardadas",
        description = "Tus artículos favoritos, siempre a mano.",
        iconRes = R.drawable.baseline_save_alt_24,
        palette = Palette.SECONDARY
    ),
    FeatureItem(
        id = FeatureId.CALENDAR,
        title = "Calendario laboral",
        description = "Festivos y días clave por comunidad.",
        iconRes = R.drawable.baseline_calendar_month_24,
        palette = Palette.MIXED
    ),
    FeatureItem(
        id = FeatureId.FINIQUITO_CALC,
        title = "Calc. finiquitos",
        description = "Estima tu finiquito de forma orientativa.",
        iconRes = R.drawable.baseline_euro_24,
        palette = Palette.TERTIARY
    ),
    /*FeatureItem(
        id = FeatureId.DESPIDO_CALC,
        title = "Calc. despidos",
        description = "Calcula la indemnización por despido.",
        iconRes = R.drawable.,
        palette = Palette.PRIMARY
    ),*/
    FeatureItem(
        id = FeatureId.VIDA_LABORAL,
        title = "Vida laboral",
        description = "Acceso a tu informe de la Seguridad Social.",
        iconRes = R.drawable.baseline_work_24,
        palette = Palette.SECONDARY
    ),
    FeatureItem(
        id = FeatureId.MI_PERFIL,
        title = "Mi perfil",
        description = "Datos personales y ajustes de la cuenta.",
        iconRes = R.drawable.baseline_settings_24,
        palette = Palette.MIXED
    ),
)
