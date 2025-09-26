// file: app/src/main/java/com/example/lagvis_v1/ui/auth/LoginOnCompose.kt
package com.example.lagvis_v1.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
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
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
                // Tu ViewModel clásico (email/contraseña)
                val vm: AuthViewModelKt = viewModel(factory = AuthViewModelFactoryKt())
                val loginState by vm.login.observeAsState(null)

                var pendingEmail by remember { mutableStateOf("") }
                var pendingPass by remember { mutableStateOf("") }
                var pendingRemember by remember { mutableStateOf(false) }

                // Persistencia + navegación tras éxito (email/contraseña)
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
                            startActivity(Intent(this@LoginOnCompose, RegisterOnCompose::class.java))
                        },
                        // 🔥 nuevo: éxito Google -> navegar a Main
                        onGoogleSuccess = {
                            startActivity(Intent(this@LoginOnCompose, MainActivity::class.java))
                            finish()
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
    onSignUpClick: () -> Unit = {},
    onGoogleSuccess: () -> Unit = {}
) {
    // Animación de entrada
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val cardScale by animateFloatAsState(
        targetValue = if (started) 1f else 0.98f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "scale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    val cardTranslateY by animateFloatAsState(
        targetValue = if (started) 0f else 20f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "translateY"
    )

    // Estado de campos
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var pass by rememberSaveable { mutableStateOf(initialPass) }
    var remember by rememberSaveable { mutableStateOf(initialRemember) }

    // Loading solo para Google (independiente de email/contraseña)
    var loadingGoogle by remember { mutableStateOf(false) }

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
                Icon(
                    painter = painterResource(R.mipmap.ic_launcher_foreground),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(88.dp)
                        .padding(top = 8.dp)
                )

                Text("Bienvenido", style = MaterialTheme.typography.headlineSmall, color = cs.onSurface)
                Text("Inicia sesión para continuar", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = remember, onCheckedChange = { remember = it })
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

                AppButton(
                    text = if (loading) "Entrando…" else "Entrar",
                    enabled = !loading && email.isNotBlank() && pass.isNotBlank(),
                    onClick = { onLoginClick(email.trim(), pass.trim(), remember) },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Divider visual ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(Modifier.weight(1f))
                    Text("  o  ", color = cs.onSurfaceVariant)
                    Divider(Modifier.weight(1f))
                }

                // ---------- BOTÓN GOOGLE ----------
                GoogleSignInButton(
                    loading = loadingGoogle,
                    onClick = { loadingGoogle = true },
                    onResult = { success, message ->
                        loadingGoogle = false
                        if (success) {
                            onGoogleSuccess()
                        } else {
                            Toast.makeText(this@LoginOnCompose, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(onClick = onSignUpClick, modifier = Modifier.align(Alignment.End)) {
                    Text("Crear cuenta")
                }
            }
        }
    }
}

/* ------------ Botón Google Compose ------------ */

@Composable
private fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    onClick: () -> Unit = {},
    onResult: (success: Boolean, message: String?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    OutlinedButton(
        onClick = {
            if (loading) return@OutlinedButton
            onClick()
            scope.launch {
                val res = signInWithGoogleAndFirebase(context)
                res.onSuccess {
                    onResult(true, null)
                }.onFailure {
                    onResult(false, it.message)
                }
            }
        },
        modifier = modifier
            .height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_settings_24),
                contentDescription = "Google",
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = if (loading) "Conectando…" else "Continuar con Google",
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(18.dp)) // compensar icono y centrar el texto
        }
    }
}

/* ------------ Lógica de Google Sign-In (Credential Manager + Firebase) ------------ */

private suspend fun signInWithGoogleAndFirebase(context: Context): Result<Unit> = runCatching {
    // Construir la opción de Google con tu WEB CLIENT ID
    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(context.getString(R.string.default_web_client_id))
        .setFilterByAuthorizedAccounts(false) // muestra todas las cuentas
        .setAutoSelectEnabled(false)
        .build()

    val request = androidx.credentials.GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    val cm = androidx.credentials.CredentialManager.create(context)
    val response = cm.getCredential(context, request)
    val credential = response.credential

    // Credential Manager devuelve un CustomCredential con un Bundle
    require(
        credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) { "Tipo de credential no soportado: ${credential::class.java.simpleName}" }

    // ¡IMPORTANTE!: GoogleIdTokenCredential.createFrom espera el Bundle
    val googleToken = GoogleIdTokenCredential.createFrom(credential.data)
    val idToken = googleToken.idToken ?: error("idToken nulo")

    val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
    val authResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCred).await()
    requireNotNull(authResult.user) { "Usuario Firebase nulo" }
}

/* ------------ Task.await helper ------------ */

private suspend fun <T> Task<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
        addOnCanceledListener { cont.cancel() }
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
