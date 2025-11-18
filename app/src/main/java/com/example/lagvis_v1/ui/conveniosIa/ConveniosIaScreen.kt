// file: app/src/main/java/com/example/lagvis_v1/ui/conveniosIa/ConveniosIaScreen.kt
package com.example.lagvis_v1.ui.conveniosIa

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lagvis_v1.ui.common.HeaderGradientParallax
import com.example.lagvis_v1.ui.theme.AppFont // Asumiendo que AppFont está aquí

/**
 * Pantalla para seleccionar el PDF y enviarlo a la IA.
 * @param onSubmit Función que se llama con la Uri del PDF para iniciar el resumen.
 * @param isLoading Indica si el proceso de IA está en curso.
 */
@Composable
fun ConveniosIaScreen(
    onSubmit: (Uri) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            // Persiste los permisos de lectura temporalmente (necesario para pasar la Uri a otro hilo)
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flags)

            selectedUri = uri
            Toast.makeText(context, "PDF seleccionado: Listo para resumir.", Toast.LENGTH_SHORT).show()
        } else {
            selectedUri = null
            Toast.makeText(context, "Selección cancelada.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderGradientParallax(
            title = "Resumen con IA",
            subtitle = "Sube tu PDF para obtener un resumen instantáneo del convenio.",
            showBack = false, // Lo gestiona el Host si está dentro de una navegación
            onBack = { /* onBack() */ },
            leadingIcon = Icons.Outlined.Description
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val fileName = selectedUri?.path?.let {
                it.substringAfterLast('/').substringAfterLast(':')
            } ?: "Ningún archivo seleccionado."

            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = if (selectedUri != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedUri == null) "Seleccionar PDF" else "Cambiar PDF", fontFamily = AppFont)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val uri = selectedUri
                    if (uri != null) {
                        onSubmit(uri)
                    }
                },
                enabled = selectedUri != null && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text("Resumir con IA", fontFamily = AppFont)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "El proceso puede tardar hasta 10 segundos, por favor sea paciente.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontFamily = AppFont,
                textAlign = TextAlign.Center
            )
        }
    }
}