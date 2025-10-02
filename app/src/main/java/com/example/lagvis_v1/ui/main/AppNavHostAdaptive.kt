// file: app/src/main/java/com/example/lagvis_v1/ui/main/AppNavHostAdaptive.kt
package com.example.lagvis_v1.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.lagvis_v1.ui.convenio.ConveniosScreenM3
import kotlinx.coroutines.launch

// ---- PROFILE (Compose) ----
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.model.UserProfileKt
import com.example.lagvis_v1.ui.profile.ProfileStateScreen
import com.example.lagvis_v1.ui.profile.ProfileViewModel
import com.example.lagvis_v1.ui.profile.ProfileViewModelFactory
import com.google.firebase.auth.FirebaseAuth

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
        DrawerItem("Noticias", Route.News.path, Icons.Outlined.Article),
        DrawerItem("Finiquitos", Route.Finiquitos.path, Icons.Outlined.Calculate),
        DrawerItem("Perfil", Route.Profile.path, Icons.Outlined.Person),
        DrawerItem("Vida laboral", Route.VidaLaboral.path, Icons.Outlined.WorkHistory),
        DrawerItem("Despidos", Route.Despidos.path, Icons.Outlined.Gavel),
        DrawerItem("Guardadas", Route.NoticiasGuardadas.path, Icons.Outlined.BookmarkBorder),
        DrawerItem("Calendario", Route.Calendario.path, Icons.Outlined.CalendarMonth),
    )

    // Qué hacer al tocar un item del drawer (todo navega dentro del NavHost)
    val onDrawerSelect: (String) -> Unit = { route ->
        nav.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(nav.graph.startDestinationId) { saveState = true }
        }
        if (!isPermanent) scope.launch { drawerState.close() }
    }

    val scaffold: @Composable () -> Unit = {
        Scaffold(
            topBar = { /* Sin TopAppBar: opción B */ }
        ) { padding ->
            Box(Modifier.fillMaxSize()) {

                // Botón de menú flotante (sin fondo) respetando status bar
                if (!isPermanent) {
                    Surface(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(start = 12.dp, top = 8.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.0f),
                        tonalElevation = 0.dp
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Outlined.Menu, contentDescription = "Menú")
                        }
                    }
                }

                // Contenido de navegación
                NavHost(
                    navController = nav,
                    startDestination = Route.Home.path, // Inicio = ConveniosScreenM3
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {

                    // ====== HOME → Tu pantalla compose ya migrada ======
                    composable(Route.Home.path) {
                        ConveniosScreenM3(
                            onSubmit = { comunidad, sector ->
                                // TODO: navega a resultados internos si quieres
                                Toast.makeText(ctx, "Buscando: $comunidad · $sector", Toast.LENGTH_SHORT).show()
                                // nav.navigate("resultados/${Uri.encode(comunidad)}/${Uri.encode(sector)}")
                            }
                        )
                    }

                    // ====== PROFILE → ProfileStateScreen con ViewModel ======
                    composable(Route.Profile.path) {
                        val vm: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())
                        // Tu VM devuelve UiState<UserProfileKt> (no null). Por eso tipamos así:
                        val state by vm.state.observeAsState(UiState.Loading())

                        val uid = remember { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
                        LaunchedEffect(uid) { vm.getProfileData(uid) }

                        ProfileStateScreen(
                            state = state as UiState<UserProfileKt?>, // ahora coincide el tipo
                            onBack = { nav.popBackStack() },
                            onRetry = { vm.getProfileData(uid) },
                            onChangePasswordClick = {
                                val email = FirebaseAuth.getInstance().currentUser?.email
                                if (email.isNullOrBlank()) {
                                    Toast.makeText(ctx, "No hay email en la sesión actual", Toast.LENGTH_LONG).show()
                                } else {
                                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                    Toast.makeText(
                                        ctx,
                                        "Te hemos enviado un correo para restablecer la contraseña",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                    }

                    // ====== Resto de pantallas (placeholders por ahora) ======
                    /* composable(Route.News.path) { ScreenText("Noticias") }
                    composable(Route.Finiquitos.path) { ScreenText("Calculadora de Finiquitos") }
                    composable(Route.VidaLaboral.path) { ScreenText("Vida Laboral") }
                    composable(Route.Despidos.path) { ScreenText("Despidos") }
                    composable(Route.NoticiasGuardadas.path) { ScreenText("Noticias Guardadas") }
                    composable(Route.Calendario.path) { ScreenText("Calendario Laboral") } */
                }
            }
        }
    }

    if (isPermanent) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerTonalElevation = 2.dp
                ) {
                    DrawerContent(
                        items = items,
                        selected = nav.currentRoute(),
                        onSelect = onDrawerSelect
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
                        selected = nav.currentRoute(),
                        onSelect = onDrawerSelect
                    )
                }
            }
        ) { scaffold() }
    }
}

/* ====================== Helpers / UI drawer ====================== */

private data class DrawerItem(val label: String, val route: String, val icon: ImageVector)

@Composable
private fun DrawerContent(
    items: List<DrawerItem>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    Text("LagVis", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
    items.forEach { item ->
        NavigationDrawerItem(
            label = { Text(item.label) },
            selected = selected == item.route,
            onClick = { onSelect(item.route) },
            icon = { Icon(item.icon, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

