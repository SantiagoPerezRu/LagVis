// file: app/src/main/java/com/example/lagvis_v1/ui/auth/LoginOnCompose.kt
package com.example.lagvis_v1.ui.auth.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await // 👈 usa esta extensión oficial
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.lagvis_v1.ui.auth.RecoverPasswordCompose
import com.example.lagvis_v1.ui.auth.register.RegisterOnCompose
import com.example.lagvis_v1.ui.auth.register.AdvancedFormRegisterCompose
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

private val googleSignInInProgress = AtomicBoolean(false)

class LoginOnCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseAuth.getInstance().currentUser?.let { user ->
            val name = user.displayName?.trim().orEmpty()
            startActivity(
                Intent(this, MainActivity::class.java)
                    .putExtra("uid", user.uid)
                    .putExtra("name", name)
            )
            finish()
            return // ¡muy importante! no sigas montando el login
        }

        val prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val remembered = prefs.getBoolean("remember", false)
        val rememberedEmail = prefs.getString("email", "") ?: ""
        val rememberedPass = prefs.getString("password", "") ?: ""

        setContent {
            LagVis_V1Theme {
                val vm: AuthViewModelKt = viewModel(factory = AuthViewModelFactoryKt())
                val loginState by vm.login.observeAsState(null)

                var pendingEmail by remember { mutableStateOf("") }
                var pendingPass by remember { mutableStateOf("") }
                var pendingRemember by remember { mutableStateOf(false) }

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
                        // Si entras por email/password, sigue igual o cambia si quieres
                        startActivity(Intent(this@LoginOnCompose, MainActivity::class.java))
                        finish()
                    }
                }

                Scaffold { inner ->
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
                        onGoogleSuccess = { user ->
                            Handler(Looper.getMainLooper()).post {
                                val full = user.name.orEmpty().trim()
                                val parts = full.split(Regex("\\s+"))
                                val given  = parts.firstOrNull()?.takeIf { it.isNotBlank() }
                                val family = parts.drop(1).takeIf { it.isNotEmpty() }?.joinToString(" ")?.takeIf { it.isNotBlank() }

                                // Si no tienes fecha de Google (normalmente NO viene), la dejas en null (no la pones).
                                val intent = Intent(this@LoginOnCompose,
                                    AdvancedFormRegisterCompose::class.java).apply {
                                    given?.let  { putExtra(NavKeys.EXTRA_GIVEN, it) }
                                    family?.let { putExtra(NavKeys.EXTRA_FAMILY, it) }
                                     putExtra(NavKeys.EXTRA_BIRTH, "1998-05-21") // ejemplo si la tuvieras
                                }
                                startActivity(intent)
                                finish()
                            }
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
    onGoogleSuccess: (SimpleUser) -> Unit = {}
) {
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

    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var pass by rememberSaveable { mutableStateOf(initialPass) }
    var remember by rememberSaveable { mutableStateOf(initialRemember) }

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

                GoogleSignInButton(
                    loading = loadingGoogle,
                    onClick = { loadingGoogle = true },
                    onResult = { res ->
                        loadingGoogle = false
                        res.onSuccess { user ->
                            onGoogleSuccess(user)
                        }.onFailure {
                            // aquí puedes mostrar un Toast si lo prefieres
                            // Toast.makeText(LocalContext.current, it.message ?: "Error desconocido", Toast.LENGTH_LONG).show()
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
    onResult: (Result<SimpleUser>) -> Unit = { _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    OutlinedButton(
        onClick = {
            if (loading) return@OutlinedButton
            if (!googleSignInInProgress.compareAndSet(false, true)) return@OutlinedButton
            onClick()
            scope.launch {
                try {
                    val res: Result<SimpleUser> = signInWithGoogleAndFirebase(context)
                    onResult(res)
                } finally {
                    googleSignInInProgress.set(false)
                }
            }
        },
        modifier = modifier.height(48.dp),
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
                painter = painterResource(id = R.drawable.baseline_settings_24), // cambia al icono de Google si lo tienes
                contentDescription = "Google",
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = if (loading) "Conectando…" else "Continuar con Google",
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(18.dp))
        }
    }
}


// --- utils para nombre/apellido ---
data class MinimalName(val givenName: String, val familyName: String)

private fun decodeJwtPayload(idToken: String): JSONObject {
    val parts = idToken.split(".")
    require(parts.size >= 2) { "Token JWT mal formado" }
    val payloadB64 = parts[1]
    val decoded = Base64.decode(payloadB64, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    return JSONObject(String(decoded, Charsets.UTF_8))
}

private fun extractMinimalName(idToken: String?, user: FirebaseUser?): MinimalName {
    if (!idToken.isNullOrBlank()) {
        try {
            val c = decodeJwtPayload(idToken)
            val given = c.optString("given_name", "").trim()
            val family = c.optString("family_name", "").trim()
            if (given.isNotEmpty() || family.isNotEmpty()) return MinimalName(given, family)
        } catch (_: Exception) { /* fallback */ }
    }
    user?.displayName?.trim()?.takeIf { it.isNotEmpty() }?.let { dn ->
        val parts = dn.split(Regex("\\s+"))
        val given = parts.first()
        val family = if (parts.size > 1) parts.drop(1).joinToString(" ") else ""
        return MinimalName(given, family)
    }
    return MinimalName("", "")
}

suspend fun signInWithGoogleAndFirebase(context: Context): Result<SimpleUser> = runCatching {
    val activity: Activity = (context as? Activity) ?: error("Context must be an Activity")
    val webClientId: String = context.getString(R.string.default_web_client_id)
    require(webClientId.endsWith(".apps.googleusercontent.com")) {
        "default_web_client_id no parece ser un Web Client ID"
    }

    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(webClientId)
        .setFilterByAuthorizedAccounts(false)
        .setAutoSelectEnabled(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    val cm = CredentialManager.create(activity)
    val response = withContext(Dispatchers.Main) { cm.getCredential(activity, request) }
    val credential = response.credential

    if (credential !is CustomCredential ||
        credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        error("Tipo de credential no soportado: ${credential::class.java.simpleName} (${credential.type})")
    }
    val googleToken = GoogleIdTokenCredential.createFrom(credential.data)
    val idToken: String = googleToken.idToken ?: error("ID Token nulo")

    // Firebase Auth
    val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
    val authResult: AuthResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCred).await()
    val user: FirebaseUser = authResult.user ?: error("Usuario Firebase nulo tras signIn")

    // Nombre mínimo ⇒ combinamos nombre/apellido o caemos a displayName
    val (givenName, familyName) = extractMinimalName(idToken, user)
    val name = listOf(givenName, familyName)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { user.displayName?.trim().orEmpty() }

    // Devolvemos un usuario ligero SIN Realtime Database
    SimpleUser(
        uid = user.uid,
        name = name
    )
}.recoverCatching { e ->
    when (e) {
        is GetCredentialCancellationException ->
            error("Inicio cancelado por el sistema (la actividad se ocultó o el usuario cerró el diálogo).")
        is NoCredentialException ->
            error("No hay credenciales de Google disponibles en este dispositivo.")
        is GetCredentialException ->
            error("Credential Manager: ${e.errorMessage ?: e.message ?: "error desconocido"}")
        else -> throw e
    }
}


data class SimpleUser(
    val uid: String,
    val name: String
)

object NavKeys {
    const val EXTRA_GIVEN  = "prefill_given_name"     // String?
    const val EXTRA_FAMILY = "prefill_family_name"    // String?
    const val EXTRA_BIRTH  = "prefill_birth_yyyy_mm_dd" // String? en ISO: 1990-12-31
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
