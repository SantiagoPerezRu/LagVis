// LoginOnCompose.kt
package com.example.lagvis_v1.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lagvis_v1.R
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.ui.auth.uicompose.systemcomponents.AppButton
import com.example.lagvis_v1.ui.auth.uicompose.systemcomponents.PasswordField
import com.example.lagvis_v1.ui.auth.uicompose.ui.theme.LagVis_V1Theme
import com.example.lagvis_v1.ui.main.MainActivity

class LoginOnCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- SharedPreferences: carga valores recordados ---
        val prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val remembered = prefs.getBoolean("remember", false)
        val rememberedEmail = prefs.getString("email", "") ?: ""
        val rememberedPass = prefs.getString("password", "") ?: ""

        setContent {
            LagVis_V1Theme {
                // ViewModel con tu factory (MISMA LÓGICA)
                val vm: AuthViewModelKt = viewModel(factory = AuthViewModelFactoryKt())
                val loginState by vm.login.observeAsState(null)

                var pendingEmail by remember { mutableStateOf("") }
                var pendingPass by remember { mutableStateOf("") }
                var pendingRemember by remember { mutableStateOf(false) }

                // Persistencia + navegación tras éxito (MISMA LÓGICA)
                LaunchedEffect(loginState) {
                    if (loginState is UiState.Success<*>) {
                        if (pendingRemember) {
                            prefs.edit()
                                .putBoolean("remember", true)
                                .putString("email", pendingEmail)
                                .putString("password", pendingPass)
                                .apply()
                        } else {
                            prefs.edit().clear().apply()
                        }
                        startActivity(Intent(this@LoginOnCompose, MainActivity::class.java))
                        finish()
                    }
                }

                // 🎨 Colores del tema
                val cs = MaterialTheme.colorScheme

                Scaffold(
                    containerColor = cs.background,
                    contentColor = cs.onBackground
                ) { inner ->
                    LoginContent(
                        modifier = Modifier.padding(inner),
                        loading = loginState is UiState.Loading,
                        errorText = (loginState as? UiState.Error)?.message,
                        initialEmail = rememberedEmail,
                        initialPass = rememberedPass,
                        initialRemember = remembered,
                        onLoginClick = { email, pass, remember ->
                            pendingEmail = email
                            pendingPass = pass
                            pendingRemember = remember
                            vm.signIn(email, pass)
                        },
                        onForgotClick = {
                            startActivity(Intent(this@LoginOnCompose, RecoverPasswordCompose::class.java))
                        },
                        onSignUpClick = {
                            startActivity(
                                Intent(
                                    this@LoginOnCompose,
                                    RegisterOnCompose::class.java
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

/* --------------------- SOLO VISTAS --------------------- */

@Composable
private fun LoginContent(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    errorText: String? = null,
    initialEmail: String = "",
    initialPass: String = "",
    initialRemember: Boolean = false,
    onLoginClick: (email: String, pass: String, remember: Boolean) -> Unit = { _, _, _ -> },
    onForgotClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    // Animación de entrada (sin tocar lógica)
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val cardScale by animateFloatAsState(
        targetValue = if (started) 1f else 0.98f,
        animationSpec = tween(700, easing = FastOutSlowInEasing), label = "scale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing), label = "alpha"
    )
    val cardTranslateY by animateFloatAsState(
        targetValue = if (started) 0f else 20f,
        animationSpec = tween(700, easing = FastOutSlowInEasing), label = "translateY"
    )

    // Estado de campos (igual que tenías)
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var pass by rememberSaveable { mutableStateOf(initialPass) }
    var remember by rememberSaveable { mutableStateOf(initialRemember) }

    // 🎨 Fondo degradado usando tu paleta M3
    val cs = MaterialTheme.colorScheme
    val gradient = Brush.verticalGradient(
        listOf(
            cs.primary.copy(alpha = 0.25f),
            cs.primaryContainer.copy(alpha = 0.35f),
            cs.surface
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .graphicsLayer {
                    scaleX = cardScale
                    scaleY = cardScale
                    translationY = cardTranslateY
                }
                .alpha(cardAlpha),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = cs.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Logo (sin teñir para respetar colores del asset)
                Icon(
                    painter = painterResource(R.mipmap.ic_launcher_foreground),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(88.dp)
                        .padding(top = 8.dp)
                )

                Text(
                    "Bienvenido",
                    style = MaterialTheme.typography.headlineSmall,
                    color = cs.onSurface
                )
                Text(
                    "Inicia sesión para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant
                )

                // OutlinedTextField ya usa la paleta del tema
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                PasswordField(
                    value = pass,
                    onValueChange = { pass = it },
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        if (!loading && email.isNotBlank() && pass.isNotBlank()) {
                            onLoginClick(email.trim(), pass.trim(), remember)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Recordarme + ¿Olvidaste?
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = remember,
                            onCheckedChange = { remember = it }
                            // Colores por defecto ya usan cs.primary/secondary
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Recordarme", style = MaterialTheme.typography.bodyMedium, color = cs.onSurface)
                    }
                    TextButton(onClick = onForgotClick) { Text("¿Olvidaste tu contraseña?") }
                }

                if (errorText != null) {
                    Text(
                        text = errorText,
                        color = cs.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                // Botón principal: toma primary/onPrimary del tema
                AppButton(
                    text = if (loading) "Entrando…" else "Entrar",
                    enabled = !loading && email.isNotBlank() && pass.isNotBlank(),
                    onClick = { onLoginClick(email.trim(), pass.trim(), remember) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(onClick = onSignUpClick, modifier = Modifier.align(Alignment.End)) {
                    Text("Crear cuenta")
                }
            }
        }
    }
}

/* ------------ Preview (solo vista) ------------ */
@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    LagVis_V1Theme {
        LoginContent(
            initialEmail = "demo@acme.com",
            initialPass = "123456",
            initialRemember = true
        )
    }
}
