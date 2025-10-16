// ============================================================
// FILE: AppNavHostAdaptive.kt - CON RUTA CONVENIO VISUALIZER
// ============================================================
package com.example.lagvis_v1.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.util.Xml
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.model.profile.UserProfileKt
import com.example.lagvis_v1.ui.theme.AppFont
import com.example.lagvis_v1.ui.calendario.CalendarioLaboralScreenM3
import com.example.lagvis_v1.ui.convenio.selector.ConvenioSelectorViewModel
import com.example.lagvis_v1.ui.convenio.selector.ConvenioSelectorViewModelFactory
import com.example.lagvis_v1.ui.convenio.visualizer.ConvenioUiModel
import com.example.lagvis_v1.ui.convenio.visualizer.ConvenioVisualizerWithEdgeHeader
import com.example.lagvis_v1.ui.convenio.selector.ConveniosScreenM3
import com.example.lagvis_v1.ui.convenio.visualizer.ConvenioVisualizerViewModel
import com.example.lagvis_v1.ui.convenio.visualizer.ConvenioVisualizerViewModelFactory
import com.example.lagvis_v1.ui.conveniosIa.ConveniosIaHost
import com.example.lagvis_v1.ui.despidos.CalculadoraDespidosHost
import com.example.lagvis_v1.ui.news.NewsOnCompose
import com.example.lagvis_v1.ui.profile.ProfileStateScreen
import com.example.lagvis_v1.ui.profile.ProfileViewModel
import com.example.lagvis_v1.ui.profile.ProfileViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHostAdaptive(windowSizeClass: WindowSizeClass) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val isPermanent = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    val items = listOf(
        DrawerItem("Inicio", Route.Home.path, Icons.Outlined.Home),
        DrawerItem("Noticias", Route.News.path, Icons.Outlined.Newspaper),
        DrawerItem("Finiquitos", Route.Finiquitos.path, Icons.Outlined.Calculate),
        DrawerItem("Calendario", Route.Calendario.path, Icons.Outlined.CalendarMonth),
        DrawerItem("Vida laboral", Route.VidaLaboral.path, Icons.Outlined.WorkHistory),
        DrawerItem("Perfil", Route.Profile.path, Icons.Outlined.Person),
        DrawerItem("Convenios IA", Route.ConvenioIa.path, Icons.Outlined.Person),
        // DrawerItem("Despidos", Route.Despidos.path, Icons.Outlined.Gavel),
       // DrawerItem("Guardadas", Route.NoticiasGuardadas.path, Icons.Outlined.BookmarkBorder),
    )

    // Email mostrado en el Drawer (puedes usar el real del usuario si quieres)
    val userEmail: String? = "contacto@lagvis.es"

    val onDrawerSelect: (String) -> Unit = { route ->
        nav.navigateSingleTopTo(route)
        if (!isPermanent) scope.launch { drawerState.close() }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val scaffold: @Composable () -> Unit = {
        Box(modifier = Modifier.fillMaxSize()) {

            // Ruta actual para decidir si ocultar TopBar y/o nestedScroll
            val currentRoute = nav.currentRouteOrNull()
            val isVisualizer = currentRoute?.startsWith("convenioVisualizer") == true

            val scaffoldModifier = Modifier

            Scaffold(modifier = scaffoldModifier) {

                NavHost(
                    navController = nav,
                    startDestination = Route.Home.path,
                    modifier = Modifier.fillMaxSize()
                ) {

                    // ====== HOME ======
                    composable(Route.Home.path) {
                        val selectorVm: ConvenioSelectorViewModel =
                            viewModel(factory = ConvenioSelectorViewModelFactory())
                        val navState by selectorVm.nav.observeAsState()

                        LaunchedEffect(navState) {
                            when (val s = navState) {
                                is UiState.Success -> {
                                    val data = s.data
                                    val archivoEncoded = android.net.Uri.encode(data.archivo)
                                    val route = "convenioVisualizer/$archivoEncoded/${data.sectorId}"
                                    nav.navigate(route) { launchSingleTop = true }
                                    selectorVm.consumeNav()

                                }
                                is UiState.Error -> {
                                    Toast.makeText(ctx, s.message ?: "Error", Toast.LENGTH_LONG).show()
                                }
                                else -> Unit
                            }
                        }

                        // Tu pantalla de selector
                        ConveniosScreenM3(
                            onSubmit = { comunidad, sector ->
                                selectorVm.onSiguiente(comunidad, sector)
                            }
                        )
                    }

            // ====== CONVENIO VISUALIZER ======
                    composable(
                        route = "convenioVisualizer/{archivo}/{sectorId}",
                        arguments = listOf(
                            navArgument("archivo") { type = NavType.StringType },
                            navArgument("sectorId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val ctx = LocalContext.current
                        val archivo = backStackEntry.arguments?.getString("archivo")?.let { android.net.Uri.decode(it) }.orEmpty()
                        val sectorId = backStackEntry.arguments?.getInt("sectorId") ?: -1

                        // 🔹 ViewModel de visualización (cache-first: descarga si falta y lee local)
                        val app = ctx.applicationContext as android.app.Application
                        val visualVm: ConvenioVisualizerViewModel =
                            viewModel(factory = ConvenioVisualizerViewModelFactory(app))

                        // Lanza la carga una vez por archivo
                        LaunchedEffect(archivo) {
                            android.util.Log.d("ConvenioVM", "Nav -> load('$archivo')")
                            visualVm.load(archivo)
                        }

                        // Observa estado de carga del XML cacheado
                        val state by visualVm.state.collectAsStateWithLifecycle()

                        when (val s = state) {
                            is UiState.Loading<*> -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            is UiState.Error<*> -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(s.message ?: "Error al cargar el convenio")
                                        Spacer(Modifier.height(12.dp))
                                        Button(onClick = { visualVm.load(archivo) }) { Text("Reintentar") }
                                    }
                                }
                            }
                            is UiState.Success<ConvenioUiModel> -> {
                                val data = s.data
                                ConvenioVisualizerWithEdgeHeader(
                                    data = data,
                                    title = "LagVis  ·  Tu convenio, resumido",
                                    subtitle = "",
                                    imageRes = null,
                                    darkScrim = true,
                                    onBack = { nav.popBackStack() },
                                    onAction = null,
                                    onRate = { rating ->
                                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                                        if (uid != null && sectorId != -1) {

                                        } else {
                                            Toast.makeText(ctx, "Error: usuario o convenio no válidos", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // ====== PROFILE ======
                    composable(Route.Profile.path) {
                        val vm: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())
                        val state by vm.state.observeAsState(UiState.Loading())

                        val uid = remember { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
                        LaunchedEffect(uid) { vm.getProfileData(uid) }

                        ProfileStateScreen(
                            state = state as UiState<UserProfileKt?>,
                            onBack = { nav.popBackStack() },
                            onRetry = { vm.getProfileData(uid) },
                            onChangePasswordClick = {
                                val email = FirebaseAuth.getInstance().currentUser?.email
                                if (email.isNullOrBlank()) {
                                    Toast.makeText(ctx, "No hay email en la sesión actual", Toast.LENGTH_LONG).show()
                                } else {
                                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                    Toast.makeText(ctx, "Te hemos enviado un correo para restablecer la contraseña", Toast.LENGTH_LONG).show()
                                }
                            }
                        )
                    }
                    // ====== CALENDARIO LABORAL ======
                    composable(Route.Calendario.path) {
                        val ctx = LocalContext.current

                        // 1) Fuente simple para los dropdowns (puedes cambiarlo por tu LookupViewModel si quieres)
                        val años = remember { (2025..2026).map { it.toString() } }
                        val provincias = remember {
                            listOf(
                                "A Coruña", "Álava", "Albacete", "Alicante", "Almería", "Asturias", "Ávila",
                                "Badajoz", "Baleares", "Barcelona", "Burgos", "Cáceres", "Cádiz", "Cantabria",
                                "Castellón", "Ciudad Real", "Córdoba", "Cuenca", "Girona", "Granada", "Guadalajara",
                                "Gipuzkoa", "Huelva", "Huesca", "Jaén", "La Rioja", "Las Palmas", "León",
                                "Lleida", "Lugo", "Madrid", "Málaga", "Murcia", "Navarra", "Ourense", "Palencia",
                                "Pontevedra", "Salamanca", "Segovia", "Sevilla", "Soria", "Tarragona",
                                "Santa Cruz de Tenerife", "Teruel", "Toledo", "Valencia", "Valladolid",
                                "Bizkaia", "Zamora", "Zaragoza"
                            )
                        }

                        // 2) Pantalla Compose (ya internamente usa HolidaysViewModelKt para cargar festivos)
                        CalendarioLaboralScreenM3(
                            años = años,
                            provincias = provincias,
                            onBack = { nav.popBackStack() },
                            onDaySelected = { fecha ->
                               // Toast.makeText(ctx, "Día seleccionado: $fecha", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    composable(Route.News.path) {
                        val ctx = LocalContext.current
                        NewsOnCompose().NewsScreen()
                    }
                    composable(Route.Finiquitos.path){
                        val ctx = LocalContext.current
                        CalculadoraDespidosHost()
                    }
                    composable(Route.ConvenioIa.path){
                        val ctx = LocalContext.current
                        ConveniosIaHost(onBack = { nav.popBackStack() })
                    }

                }
            }

            // TopBar flotante (oculta en convenio visualizer)
            if (!isVisualizer) {
                LagVisTopBarFloating(
                    showMenu = !isPermanent,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSearchClick = { nav.navigate(Route.News.path) },
                    onProfileClick = { nav.navigateSingleTopTo(Route.Profile.path) },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(10f)
                )
            }
        }
    }

    // DRAWER ADAPTATIVO
    if (isPermanent) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerTonalElevation = 2.dp
                ) {
                    DrawerContent(
                        items = items,
                        selected = nav.currentRouteOrNull(),
                        onSelect = onDrawerSelect,
                        userEmail = userEmail
                    )
                }
            }
        ) { scaffold() }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerTonalElevation = 2.dp
                ) {
                    DrawerContent(
                        items = items,
                        selected = nav.currentRouteOrNull(),
                        onSelect = onDrawerSelect,
                        userEmail = userEmail
                    )
                }
            }
        ) { scaffold() }
    }
}

/* ====================== Helpers / UI drawer ====================== */

private data class DrawerItem(val label: String, val route: String, val icon: ImageVector)

/** TopBar flotante y transparente que se superpone al contenido */
@Composable
fun LagVisTopBarFloating(
    modifier: Modifier = Modifier,
    showMenu: Boolean,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    ) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Izquierda
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showMenu) {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Outlined.Menu, contentDescription = "Menú", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Text(
                    text = "LagVis",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = if (showMenu) 4.dp else 0.dp),
                    fontFamily = AppFont
                )
            }
            // Derecha
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Outlined.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Outlined.Person, contentDescription = "Perfil", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun DrawerContent(
    items: List<DrawerItem>,
    selected: String?,
    onSelect: (String) -> Unit,
    userEmail: String?
) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        cs.surfaceColorAtElevation(2.dp),
                        cs.surface
                    )
                )
            )
    ) {
        // Header con título + email
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to cs.primary,
                        1f to cs.primaryContainer
                    )
                )
                .padding(top = 28.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Text(
                    text = "LagVis",
                    style = MaterialTheme.typography.bodyLarge,
                    color = cs.onPrimary,
                    fontFamily = AppFont
                )
                if (!userEmail.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.labelLarge,
                        color = cs.onPrimary.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = AppFont
                    )
                }
            }
        }

        Divider(color = cs.outlineVariant.copy(alpha = 0.4f))

        // Items
        Spacer(Modifier.height(4.dp))
        items.forEach { item ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = AppFont
                    )
                },
                selected = selected == item.route,
                onClick = { onSelect(item.route) },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        tint = if (selected == item.route) cs.primary else cs.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(Modifier.weight(1f))

        Text(

            text = "© ${java.time.Year.now()} LagVis",
            style = MaterialTheme.typography.labelSmall,
            color = cs.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            fontFamily = AppFont
        )
    }
}

/* ====================== Helpers navegación ====================== */

@Composable
private fun NavController.currentRouteOrNull(): String? {
    val backstack by currentBackStackEntryAsState()
    return backstack?.destination?.route
}

private fun NavController.navigateSingleTopTo(route: String) {
    this.navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
    }
}

/* ====================== Carga XML local (temporal) ====================== */

private fun cargarConvenioDesdeXML(ctx: Context, nombreArchivo: String): ConvenioUiModel {
    try {
        val resourceId = ctx.resources.getIdentifier(
            nombreArchivo.replace(".xml", ""),
            "raw",
            ctx.packageName
        )
        if (resourceId == 0) {
            return ConvenioUiModel(titulo = "Archivo no encontrado: $nombreArchivo")
        }

        var titulo = ""
        var resumenGeneral = ""
        var diasVacaciones = ""
        var observacionesVacaciones = ""
        var numeroFestivos = ""
        var detalleFestivos = ""
        var regulacionHorasExtra = ""
        var salarioInfo = ""
        var salarioAproximado = ""
        var licenciaMatrimonio = ""
        var licenciaFallecimiento = ""
        var licenciaFormacion = ""
        var licenciaOtros = ""
        var coberturaSeguro = ""
        var importeSeguro = ""
        var igualdad = ""
        var saludLaboral = ""
        var conciliacion = ""
        var representacion = ""
        var detalleManutencion = ""

        ctx.resources.openRawResource(resourceId).use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser().apply { setInput(inputStream, null) }
            var eventType = parser.eventType
            val texto = StringBuilder()

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> texto.setLength(0)
                    XmlPullParser.TEXT -> {
                        val t = parser.text
                        if (!t.isNullOrBlank()) texto.append(t.trim())
                    }
                    XmlPullParser.END_TAG -> {
                        val tag = parser.name
                        val contenido = texto.toString().trim()
                        when (tag) {
                            "titulo" -> titulo = contenido
                            "resumen_general" -> resumenGeneral = contenido
                            "dias" -> diasVacaciones = contenido
                            "observaciones" -> observacionesVacaciones = contenido
                            "numero_dias" -> numeroFestivos = contenido
                            "detalle" -> {
                                if (detalleFestivos.isEmpty()) detalleFestivos = contenido
                                else detalleManutencion = contenido
                            }
                            "regulacion" -> regulacionHorasExtra = contenido
                            "salario" -> salarioInfo = contenido
                            "salario_aproximado" ->
                                salarioAproximado = if (contenido.isNotEmpty()) "Salario Aproximado: $contenido" else ""
                            "matrimonio" ->
                                licenciaMatrimonio = if (contenido.isNotEmpty()) "Matrimonio: $contenido" else ""
                            "fallecimiento_familiares" ->
                                licenciaFallecimiento = if (contenido.isNotEmpty()) "Fallecimiento: $contenido" else ""
                            "formacion" ->
                                licenciaFormacion = if (contenido.isNotEmpty()) "Formación: $contenido" else ""
                            "otros" -> licenciaOtros = if (contenido.isNotEmpty()) "Otros: $contenido" else ""
                            "cobertura" -> coberturaSeguro = contenido
                            "importe" -> importeSeguro = contenido
                            "igualdad" -> igualdad = contenido
                            "salud_laboral" -> saludLaboral = contenido
                            "conciliacion" -> conciliacion = contenido
                            "representacion" -> representacion = contenido
                            "manutencion" -> detalleManutencion = contenido
                        }
                    }
                }
                eventType = parser.next()
            }
        }

        return ConvenioUiModel(
            titulo = titulo,
            resumenGeneral = resumenGeneral,
            diasVacaciones = diasVacaciones,
            observacionesVacaciones = observacionesVacaciones,
            numeroFestivos = numeroFestivos,
            detalleFestivos = detalleFestivos,
            regulacionHorasExtra = regulacionHorasExtra,
            salarioInfo = salarioInfo,
            salarioAproximado = salarioAproximado,
            licenciaMatrimonio = licenciaMatrimonio,
            licenciaFallecimiento = licenciaFallecimiento,
            licenciaFormacion = licenciaFormacion,
            licenciaOtros = licenciaOtros,
            coberturaSeguro = coberturaSeguro,
            importeSeguro = importeSeguro,
            igualdad = igualdad,
            saludLaboral = saludLaboral,
            conciliacion = conciliacion,
            representacion = representacion,
            detalleManutencion = detalleManutencion
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return ConvenioUiModel(titulo = "Error al cargar el convenio: ${e.message}")
    }
}
