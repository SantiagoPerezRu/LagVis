package com.example.lagvis_v1.ui.compose

import VisualizadorPaginas.PaginaVidaLaboralFragment
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
//import androidx.fragment.app.commit
import com.example.lagvis_v1.ui.calendario.CalendarioLaboral
import com.example.lagvis_v1.ui.compose.ui.theme.LagVis_V1Theme
import com.example.lagvis_v1.ui.convenio.ConvenioSelectorFrag
import com.example.lagvis_v1.ui.despidos.ActivityDatosGeneralesDespido
import com.example.lagvis_v1.ui.finiquitos.CalculadoraFiniquitosFragment
import com.example.lagvis_v1.ui.news.NewsViewFrag
import com.example.lagvis_v1.ui.news.NoticiasGuardadasFragment
import com.example.lagvis_v1.ui.profile.ProfileViewFrag

class HomeMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val activity = this@HomeMenuActivity

        setContent {
            LagVis_V1Theme {
                HomeMenu(
                    userName = null, // pásalo desde tu back cuando lo tengas
                    onFeatureClick = { id -> navigateTo(activity, id) }
                )
            }
        }
    }
}

/* -------------------- Navegación -------------------- */

private fun navigateTo(activity: Activity, id: FeatureId) {
    try {
        when (id) {
            FeatureId.CONVENIOS_HOME ->
                FragmentHostActivity.launch(activity, ConvenioSelectorFrag::class.java)

            FeatureId.NEWS ->
                FragmentHostActivity.launch(activity, NewsViewFrag::class.java)

            FeatureId.SAVED_NEWS ->
                FragmentHostActivity.launch(activity, NoticiasGuardadasFragment::class.java)

            FeatureId.CALENDAR -> {
                // Parece Activity (no 'Frag'), así que Intent directo
                activity.startActivity(Intent(activity, CalendarioLaboral::class.java))
            }

            FeatureId.FINIQUITO_CALC ->
                FragmentHostActivity.launch(activity, CalculadoraFiniquitosFragment::class.java)

            FeatureId.DESPIDO_CALC -> {
                // Es Activity
                activity.startActivity(Intent(activity, ActivityDatosGeneralesDespido::class.java))
            }

            FeatureId.VIDA_LABORAL ->
                FragmentHostActivity.launch(activity, PaginaVidaLaboralFragment::class.java)

            FeatureId.MI_PERFIL ->
                FragmentHostActivity.launch(activity, ProfileViewFrag::class.java)
        }
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            activity,
            "Pantalla no encontrada para ${id.name}. Revisa el mapeo.",
            Toast.LENGTH_SHORT
        ).show()
    } catch (e: Throwable) {
        Toast.makeText(
            activity,
            "Error al abrir pantalla: ${e.message}",
            Toast.LENGTH_SHORT
        ).show()
    }
}

/** Activity genérica que hospeda cualquier Fragment por clase */
class FragmentHostActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Contenedor sin XML
        val containerId = View.generateViewId()
        val container = FragmentContainerView(this).apply {
            id = containerId
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(container)

        if (savedInstanceState == null) {
            val className = intent.getStringExtra(EXTRA_CLASS)
            val args = intent.getBundleExtra(EXTRA_ARGS)

            require(!className.isNullOrBlank()) { "Fragment class not provided" }

            val fragment = supportFragmentManager.fragmentFactory
                .instantiate(classLoader, className)
                .apply { arguments = args }

            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(containerId, fragment, "ROOT")
                .commit()

        }
    }

    companion object {
        private const val EXTRA_CLASS = "extra_fragment_class"
        private const val EXTRA_ARGS = "extra_fragment_args"

        fun <T : Fragment> launch(
            activity: Activity,
            fragmentClass: Class<T>,
            args: Bundle? = null
        ) {
            val intent = Intent(activity, FragmentHostActivity::class.java).apply {
                putExtra(EXTRA_CLASS, fragmentClass.name)
                if (args != null) putExtra(EXTRA_ARGS, args)
            }
            activity.startActivity(intent)
        }
    }
}

/* -------------------- Modelo y UI -------------------- */

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

@Immutable
data class FeatureItem(
    val id: FeatureId,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeMenu(
    userName: String?,
    onFeatureClick: (FeatureId) -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val palettes = remember(cs) {
        listOf(
            listOf(cs.primary, cs.primaryContainer),
            listOf(cs.tertiary, cs.tertiaryContainer),
            listOf(cs.secondary, cs.secondaryContainer),
            listOf(cs.primary, cs.secondaryContainer)
        )
    }

    val items = remember {
        listOf(
            FeatureItem(
                id = FeatureId.CONVENIOS_HOME,
                title = "Convenios",
                description = "Accede a la pantalla principal de convenios.",
                icon = Icons.Outlined.LibraryBooks,
                gradient = palettes[0]
            ),
            FeatureItem(
                id = FeatureId.NEWS,
                title = "Noticias",
                description = "Actualidad y cambios en normativa laboral.",
                icon = Icons.Outlined.Article,
                gradient = palettes[1]
            ),
            FeatureItem(
                id = FeatureId.SAVED_NEWS,
                title = "Noticias guardadas",
                description = "Tus artículos favoritos, siempre a mano.",
                icon = Icons.Outlined.Bookmark,
                gradient = palettes[2]
            ),
            FeatureItem(
                id = FeatureId.CALENDAR,
                title = "Calendario laboral",
                description = "Festivos y días clave por comunidad.",
                icon = Icons.Outlined.Event,
                gradient = palettes[3]
            ),
            FeatureItem(
                id = FeatureId.FINIQUITO_CALC,
                title = "Calc. finiquitos",
                description = "Estima tu finiquito de forma orientativa.",
                icon = Icons.Outlined.AttachMoney,
                gradient = palettes[1]
            ),
            FeatureItem(
                id = FeatureId.DESPIDO_CALC,
                title = "Calc. despidos",
                description = "Calcula la indemnización por despido.",
                icon = Icons.Outlined.Calculate,
                gradient = palettes[0]
            ),
            FeatureItem(
                id = FeatureId.VIDA_LABORAL,
                title = "Vida laboral",
                description = "Acceso a tu informe de la Seguridad Social.",
                icon = Icons.Outlined.Work,
                gradient = palettes[2]
            ),
            FeatureItem(
                id = FeatureId.MI_PERFIL,
                title = "Mi perfil",
                description = "Datos personales y ajustes de la cuenta.",
                icon = Icons.Outlined.AccountCircle,
                gradient = palettes[3]
            )
        )
    }

    val gridState = rememberLazyGridState()

    Scaffold(topBar = {}) { padding ->
        val contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = padding.calculateTopPadding() + 16.dp,
            bottom = padding.calculateBottomPadding() + 16.dp
        )
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2), // 2 por fila
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header de bienvenida (ocupa 2 columnas)
            item(span = { GridItemSpan(maxLineSpan) }) {
                WelcomeHeader(
                    userName = userName,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(items, key = { it.id.name }) { item ->
                FeatureCard(
                    item = item,
                    onClick = { onFeatureClick(item.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp)
                )
            }
        }
    }
}

@Composable
private fun WelcomeHeader(
    userName: String?,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val gradient = Brush.linearGradient(listOf(cs.primary, cs.secondaryContainer))

    ElevatedCard(
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(20.dp)
        ) {
            // blobs decorativos
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
                    .clip(RoundedCornerShape(70.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )
                    .graphicsLayer { alpha = 0.5f }
            )
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-10).dp, y = 10.dp)
                    .clip(RoundedCornerShape(45.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
                    .graphicsLayer { alpha = 0.6f }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Bienvenido de nuevo",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                    Text(
                        text = userName ?: "Usuario",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "¿Qué quieres hacer hoy?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.92f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    item: FeatureItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(28.dp)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.98f else 1f, label = "pressScale")

    ElevatedCard(
        onClick = onClick,
        shape = shape,
        interactionSource = interaction,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
            .graphicsLayer { val s = scale; scaleX = s; scaleY = s }
            .semantics { role = Role.Button; contentDescription = item.title }
    ) {
        // Tira superior con gradiente del item
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Brush.linearGradient(item.gradient))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(item.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = item.icon, contentDescription = null, tint = Color.White)
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "Home – Claro", widthDp = 420)
@Composable
private fun HomeMenuPreviewLight() {
    LagVis_V1Theme {
        HomeMenu(userName = "Santi", onFeatureClick = {})
    }
}

@Preview(showBackground = true, name = "Home – Oscuro", widthDp = 420)
@Composable
private fun HomeMenuPreviewDark() {
    LagVis_V1Theme {
        HomeMenu(userName = "Santi", onFeatureClick = {})
    }
}
