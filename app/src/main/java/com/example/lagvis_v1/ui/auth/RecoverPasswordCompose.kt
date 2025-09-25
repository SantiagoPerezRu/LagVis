package com.example.lagvis_v1.ui.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.lagvis_v1.ui.auth.uicompose.ui.theme.LagVis_V1Theme

import android.content.Intent
import android.util.Patterns
import android.widget.Toast
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

class RecoverPasswordCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LagVis_V1Theme {
                val vm: AuthViewModelKt = viewModel(factory = AuthViewModelFactoryKt())
                val resetState by vm.reset.observeAsState()

                val ctx = this@RecoverPasswordCompose

                // Reacciones a cambios de estado (igual que en la Activity Java)
                LaunchedEffect(resetState) {
                    when (resetState) {
                        is UiState.Success -> {
                            Toast.makeText(
                                ctx,
                                "¡Email enviado! Revisa tu correo.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        is UiState.Error -> {
                            val msg = (resetState as UiState.Error<*>).message
                                ?: "No se pudo enviar el email"
                            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                        }

                        else -> Unit
                    }
                }

                RecoverPasswordScreen(
                    loading = resetState is UiState.Loading,
                    onSend = { email -> vm.resetPassword(email) },
                    onGoLogin = {
                        startActivity(Intent(ctx, LoginOnCompose::class.java))
                        finish()
                    },
                    onGoRegister = {
                        startActivity(Intent(ctx, RegisterOnCompose::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

/* -------------------- UI -------------------- */

@Composable
fun RecoverPasswordScreen(
    loading: Boolean,
    onSend: (String) -> Unit,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    // Animación entrada suave (match con login/register)
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val cardScale by animateFloatAsState(
        targetValue = if (started) 1f else 0.98f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing), label = "scale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing), label = "alpha"
    )
    val cardTranslateY by animateFloatAsState(
        targetValue = if (started) 0f else 20f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing), label = "ty"
    )

    val cs = MaterialTheme.colorScheme
    val gradient = Brush.verticalGradient(
        listOf(
            cs.primary.copy(alpha = 0.25f),
            cs.primaryContainer.copy(alpha = 0.35f),
            cs.surface
        )
    )

    var email by rememberSaveable { mutableStateOf("") }
    val isEmailValid = remember(email) {
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Header con wave + título
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.TopStart
    ) {
        HeaderWave()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 56.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recuperar contraseña",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    // Tarjeta central con el formulario (email + enviar + enlaces)
    Box(
        Modifier
            .fillMaxSize()
            .padding(top = 170.dp),
        contentAlignment = Alignment.TopCenter
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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icono/logo opcional
                Icon(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp)
                )

                Text(
                    "Introduce tu correo y te enviaremos un enlace para restablecer la contraseña.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    singleLine = true,
                    isError = email.isNotEmpty() && !isEmailValid,
                    supportingText = {
                        if (email.isNotEmpty() && !isEmailValid) Text("Formato de email no válido")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                AppButton(
                    text = if (loading) "Enviando…" else "Enviar",
                    enabled = !loading && isEmailValid,
                    onClick = { onSend(email.trim()) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Enlaces (como en tu Activity original)
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = onGoLogin,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text("Ya lo envié, volver a iniciar sesión")
                }
                TextButton(
                    onClick = onGoRegister,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text("¿No tienes cuenta? Crear cuenta")
                }
            }
        }
    }
}



/* -------------------- Preview -------------------- */
@Preview(showBackground = true)
@Composable
private fun RecoverPasswordPreviewLight() {
    LagVis_V1Theme {
        RecoverPasswordScreen(
            loading = false,
            onSend = {},
            onGoLogin = {},
            onGoRegister = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecoverPasswordPreviewDark() {
    LagVis_V1Theme(darkTheme = true) {
        RecoverPasswordScreen(
            loading = true,
            onSend = {},
            onGoLogin = {},
            onGoRegister = {}
        )
    }
}