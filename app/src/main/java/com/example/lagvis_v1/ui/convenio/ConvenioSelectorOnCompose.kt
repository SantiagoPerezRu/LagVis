package com.example.lagvis_v1.ui.convenio

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lagvis_v1.LagVisApp
import com.example.lagvis_v1.ui.auth.uicompose.ui.theme.LagVis_V1Theme
import com.example.lagvis_v1.ui.auth.uicompose.ui.theme.AppFont
import com.example.lagvis_v1.ui.common.DropdownOutlinedM3
import com.example.lagvis_v1.ui.common.HeaderGradientParallax
import com.example.lagvis_v1.ui.common.LookupViewModel
import com.example.lagvis_v1.ui.common.LookupViewModelFactory



/* ======================== UI ======================== */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConveniosScreenM3(
    onSubmit: (comunidad: String, sector: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var comunidad by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val formOk = comunidad.isNotBlank() && sector.isNotBlank()

    val app = LocalContext.current
    val lookupVm: LookupViewModel = viewModel(factory = LookupViewModelFactory())
    val comunidades: List<String> by lookupVm.comunidadesUi.collectAsStateWithLifecycle(initialValue = emptyList())
    val sectores: List<String> by lookupVm.sectoresUi.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        HeaderGradientParallax(
            title = "Convenios\ncolectivos",
            subtitle = "Introduce tu comunidad autónoma y sector",
            showBack = false,
            onBack = {},
            leadingIcon = Icons.Outlined.Search
        )


        // Contenido
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp)
        ) {
            Spacer(Modifier.height(18.dp))

            Text(
                "Comunidad Autónoma",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = AppFont
            )
            Spacer(Modifier.height(8.dp))

            DropdownOutlinedM3(
                value = comunidad,
                options = comunidades,
                onSelect = {
                    comunidad = it
                    if (showError) showError = false
                },
                placeholder = "Selecciona comunidad",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Sector laboral",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = AppFont
            )
            Spacer(Modifier.height(8.dp))

            DropdownOutlinedM3(
                value = sector,
                options = sectores,
                onSelect = {
                    sector = it
                    if (showError) showError = false
                },
                placeholder = "Selecciona sector",
                modifier = Modifier.fillMaxWidth()
            )

            if (showError) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "¡Debes seleccionar una comunidad autónoma y un sector laboral!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    fontFamily = AppFont
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (formOk) onSubmit(comunidad, sector) else showError = true
                },
                enabled = formOk,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Siguiente", fontFamily = AppFont)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {}
                Spacer(Modifier.width(8.dp))
                Text(
                    "Serás redirigido a un resumen claro del convenio.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = AppFont
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/* ======================== Previews ======================== */

@Preview(showBackground = true)
@Composable
private fun ConveniosPreviewLight() {
    LagVis_V1Theme {
        ConveniosScreenM3(
            onSubmit = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConveniosPreviewDark() {
    LagVis_V1Theme(darkTheme = true) {
        ConveniosScreenM3(
            onSubmit = { _, _ -> }
        )
    }
}
