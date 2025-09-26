// file: app/src/main/java/com/example/lagvis_v1/ui/profile/ProfileComposeActivity.kt
package com.example.lagvis_v1.ui.profile

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lagvis_v1.R
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.model.UserProfileKt
import com.example.lagvis_v1.ui.auth.uicompose.ui.theme.AppFont
import com.example.lagvis_v1.ui.auth.uicompose.ui.theme.LagVis_V1Theme
import com.google.firebase.auth.FirebaseAuth

class ProfileOnCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LagVis_V1Theme {
                val vm: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())
                val state by vm.state.observeAsState(UiState.Loading())

                val uid = remember { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

                LaunchedEffect(uid) {
                    vm.getProfileData(uid)
                }

                when (val s = state) {
                    is UiState.Loading -> ProfileLoading()

                    is UiState.Success -> {
                        val user = s.data
                        if (user != null) {
                            ProfileScreen(
                                user = user,
                                onChangePasswordClick = {
                                    val email = FirebaseAuth.getInstance().currentUser?.email
                                    if (email.isNullOrBlank()) {
                                        Toast.makeText(
                                            this@ProfileOnCompose,
                                            "No hay email en la sesión actual",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                        Toast.makeText(
                                            this@ProfileOnCompose,
                                            "Te hemos enviado un correo para restablecer la contraseña",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            )
                        } else {
                            ProfileEmpty { vm.getProfileData(uid) }
                        }
                    }

                    is UiState.Error -> {
                        val msg = s.message ?: "Error desconocido"
                        ProfileError(
                            message = msg,
                            onRetry = { vm.getProfileData(uid) }
                        )
                        LaunchedEffect(msg) {
                            Toast.makeText(this@ProfileOnCompose, msg, Toast.LENGTH_LONG).show()
                        }
                    }

                    else -> ProfileEmpty { vm.getProfileData(uid) }
                }
            }
        }
    }
}

/* ======================= UI COMPOSE SIN TOP BAR ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserProfileKt,
    onChangePasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val scroll = rememberScrollState()
    val headerHeight = 220.dp

    Scaffold(
        // manejamos los insets manualmente
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { inner ->
        Box(Modifier.fillMaxSize()) {

            // ======= CONTENIDO (debajo del header; no solapa) =======
            Column(
                Modifier
                    .padding(inner)
                    .padding(top = headerHeight) // empuja contenido bajo el header
                    .verticalScroll(scroll)
            ) {

                // Tarjeta compacta con iniciales + nombre completo
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initialsOf(user),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                fontFamily = AppFont
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = fullNameOf(user),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                ),
                                fontFamily = AppFont,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            AnimatedVisibility(
                                visible = user.sectorLaboral.isNotBlank(),
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = user.sectorLaboral,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    fontFamily = AppFont,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Secciones
                SectionCard("Nombre", Icons.Outlined.Person, nonEmptyOrDash(user.nombre))
                SectionCard("Primer apellido", Icons.Outlined.Badge, nonEmptyOrDash(user.apellido))
                SectionCard("Segundo apellido", Icons.Outlined.Badge, nonEmptyOrDash(user.apellido2))
                SectionCard("Fecha de nacimiento", Icons.Outlined.Cake, nonEmptyOrDash(user.fechaNacimiento))
                SectionCard("Comunidad autónoma", Icons.Outlined.Map, nonEmptyOrDash(user.comunidadAutonoma))
                SectionCard("Sector laboral", Icons.Outlined.WorkOutline, nonEmptyOrDash(user.sectorLaboral))

                Spacer(Modifier.height(8.dp))

                FilledTonalButton(
                    onClick = onChangePasswordClick,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Outlined.LockReset, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cambiar contraseña", fontFamily = AppFont)
                }

                Spacer(Modifier.height(28.dp))
            }

            // ======= HEADER superpuesto (recibe los toques) =======
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .zIndex(1f) // asegura que queda por encima
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.top_background),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.15f),
                    contentScale = ContentScale.Crop
                )

                // Barra: ←  Tu perfil                              🔒
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { (ctx as? Activity)?.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Tu perfil",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        color = Color.White,
                        fontFamily = AppFont,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = onChangePasswordClick) {
                        Icon(
                            imageVector = Icons.Outlined.LockReset,
                            contentDescription = "Cambiar contraseña",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    label: String,
    icon: ImageVector,
    value: String
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.padding(8.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    fontFamily = AppFont
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = AppFont,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/* ======================= Helpers ======================= */
private fun initialsOf(u: UserProfileKt): String {
    val n1 = u.nombre.trim().takeIf { it.isNotEmpty() }?.firstOrNull()?.uppercase() ?: "?"
    val a1 = u.apellido.trim().takeIf { it.isNotEmpty() }?.firstOrNull()?.uppercase() ?: ""
    return "$n1$a1"
}

private fun fullNameOf(u: UserProfileKt): String =
    listOf(u.nombre, u.apellido, u.apellido2)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { "Usuario" }

private fun nonEmptyOrDash(value: String?): String =
    value?.takeIf { it.isNotBlank() } ?: "—"

/* ======================= States ======================= */
@Composable
private fun ProfileLoading() {
    Box(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}

@Composable
private fun ProfileError(message: String, onRetry: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Vaya…",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = AppFont,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, fontFamily = AppFont)
            Spacer(Modifier.height(16.dp))
            FilledTonalButton(onClick = onRetry) { Text("Reintentar", fontFamily = AppFont) }
        }
    }
}

@Composable
private fun ProfileEmpty(onRetry: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Sin datos de perfil",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = AppFont,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onRetry) { Text("Cargar de nuevo", fontFamily = AppFont) }
        }
    }
}

/* ======================= Previews ======================= */
@Preview(showBackground = true)
@Composable
private fun ProfilePreviewLight() {
    LagVis_V1Theme {
        ProfileScreen(
            user = UserProfileKt(
                nombre = "María",
                apellido = "López",
                apellido2 = "García",
                comunidadAutonoma = "Comunidad de Madrid",
                sectorLaboral = "Tecnología",
                fechaNacimiento = "1995-07-21"
            ),
            onChangePasswordClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePreviewDark() {
    LagVis_V1Theme {
        ProfileScreen(
            user = UserProfileKt(
                nombre = "Javier",
                apellido = "Santos",
                apellido2 = "",
                comunidadAutonoma = "Andalucía",
                sectorLaboral = "Sanidad",
                fechaNacimiento = "1988-02-11"
            ),
            onChangePasswordClick = {}
        )
    }
}
