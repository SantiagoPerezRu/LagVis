// RegisterOnCompose.kt
package com.example.lagvis_v1.uicompose

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lagvis_v1.R
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.ui.auth.AdvancedFormRegister
import com.example.lagvis_v1.ui.auth.AuthViewModel
import com.example.lagvis_v1.ui.auth.AuthViewModelFactory
import com.example.lagvis_v1.ui.main.MainActivity
import com.example.lagvis_v1.uicompose.systemcomponents.AppButton
import com.example.lagvis_v1.uicompose.ui.theme.LagVis_V1Theme

class RegisterOnCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LagVis_V1Theme {
                val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory())
                val registerState by vm.signup.observeAsState(null)

                var pendingEmail by remember { mutableStateOf("") }
                var pendingPass by remember { mutableStateOf("") }

                if (registerState is UiState.Success<*>) {
                    startActivity(Intent(this@RegisterOnCompose, AdvancedFormRegisterCompose::class.java))
                    finish()
                }

                RegisterScreen(
                    onBackToLogin = { finish() },
                    onRegisterClick = { email, pass ->
                        pendingEmail = email
                        pendingPass = pass
                         vm.signUp(email, pass)
                    }
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBackToLogin: () -> Unit = {},
    onRegisterClick: (email: String, password: String) -> Unit = { _, _ -> }
) {
    // Animación de entrada
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

    // Estado de campos
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    // Validaciones típicas
    val isEmailValid = remember(email) { Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() }
    val passRules = remember(pass) {
        PasswordRules(
            lengthOk = pass.length >= 6,     // ajusta a 8 si quieres
            hasDigit = pass.any { it.isDigit() },
            hasLetter = pass.any { it.isLetter() }
        )
    }
    val passwordsMatch = remember(pass, confirm) { pass.isNotEmpty() && pass == confirm }
    val isFormValid = isEmailValid && passRules.allOk && passwordsMatch

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
                // Logo
                Icon(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(72.dp)
                        .padding(top = 2.dp)
                )

                Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall, color = cs.onSurface)
                Text("Introduce tu email y contraseña", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)

                // EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    isError = email.isNotEmpty() && !isEmailValid,
                    supportingText = {
                        if (email.isNotEmpty() && !isEmailValid) Text("Email no válido")
                    },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // PASSWORD
                PasswordField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = "Contraseña",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.fillMaxWidth(),
                    supporting = {
                        PasswordChecklist(passRules)
                    }
                )

                // CONFIRM PASSWORD
                ConfirmPasswordField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = "Repite la contraseña",
                    passwordToMatch = pass,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        if (isFormValid) onRegisterClick(email.trim(), pass)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Botón registrar
                AppButton(
                    text = "Registrarme",
                    enabled = isFormValid,
                    onClick = { onRegisterClick(email.trim(), pass) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(onClick = onBackToLogin, modifier = Modifier.align(Alignment.End)) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }
            }
        }
    }
}

/* ------------------ Helpers de contraseña ------------------ */

private data class PasswordRules(
    val lengthOk: Boolean,
    val hasDigit: Boolean,
    val hasLetter: Boolean
) {
    val allOk get() = lengthOk && hasDigit && hasLetter
}

@Composable
private fun PasswordChecklist(r: PasswordRules) {
    val cs = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        RuleText("Mín. 6 caracteres", r.lengthOk, cs)
        RuleText("Al menos 1 número", r.hasDigit, cs)
        RuleText("Al menos 1 letra", r.hasLetter, cs)
    }
}

@Composable
private fun RuleText(text: String, ok: Boolean, cs: ColorScheme) {
    val color = if (ok) cs.primary else cs.error
    Text(text, color = color, style = MaterialTheme.typography.bodySmall)
}

/* ---------- Campos reutilizables ---------- */

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Contraseña",
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null,
    supporting: @Composable (() -> Unit)? = null
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onDone = { onImeAction?.invoke() },
            onGo = { onImeAction?.invoke() },
            onSend = { onImeAction?.invoke() }
        ),
        trailingIcon = {
            val icon = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
            val desc = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
            IconButton(onClick = { visible = !visible }) {
                Icon(icon, contentDescription = desc)
            }
        },
        supportingText = {
            supporting?.invoke()
        }
    )
}

@Composable
private fun ConfirmPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    passwordToMatch: String,
    modifier: Modifier = Modifier,
    label: String = "Repite la contraseña",
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    val matches = value.isNotEmpty() && value == passwordToMatch

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        singleLine = true,
        isError = value.isNotEmpty() && !matches,
        supportingText = {
            if (value.isNotEmpty() && !matches) Text("Las contraseñas no coinciden")
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onDone = { onImeAction?.invoke() }
        ),
        trailingIcon = {
            val icon = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
            val desc = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
            IconButton(onClick = { visible = !visible }) {
                Icon(icon, contentDescription = desc)
            }
        }
    )
}

/* ------------------ Preview ------------------ */

@Preview(showBackground = true)
@Composable
private fun RegisterPreviewLight() {
    LagVis_V1Theme(darkTheme = false) {
        RegisterScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreviewDark() {
    LagVis_V1Theme(darkTheme = true) {
        RegisterScreen()
    }
}
