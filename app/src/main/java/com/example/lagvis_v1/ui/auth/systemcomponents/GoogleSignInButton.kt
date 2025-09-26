package com.example.lagvis_v1.ui.auth


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lagvis_v1.auth.GoogleAuthUiClient
import com.example.lagvis_v1.ui.auth.uicompose.ui.theme.AppFont
import kotlinx.coroutines.launch

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onSuccess: (uid: String) -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val client = remember { GoogleAuthUiClient(ctx) }

    OutlinedButton(
        onClick = {
            scope.launch {
                val res = client.signIn()
                res.onSuccess { user ->
                    Toast.makeText(ctx, "Hola ${user.displayName ?: ""}", Toast.LENGTH_SHORT).show()
                    onSuccess(user.uid)
                }.onFailure {
                    onError(it.message ?: "Error al iniciar sesión")
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = androidx.compose.ui.graphics.Color.White,
            contentColor = androidx.compose.ui.graphics.Color.Black
        ),
        border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xFFE0E0E0))
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_light),
                contentDescription = "Google",
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Continuar con Google",
                modifier = Modifier.weight(1f),
                fontFamily = AppFont
            )
            Spacer(Modifier.width(18.dp)) // para compensar el icono y centrar el texto
        }
    }
}
