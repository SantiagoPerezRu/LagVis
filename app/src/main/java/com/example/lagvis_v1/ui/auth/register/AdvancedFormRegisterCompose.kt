package com.example.lagvis_v1.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lagvis_v1.R
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.ui.auth.login.NavKeys
import com.example.lagvis_v1.ui.auth.uicompose.systemcomponents.AppButton
import com.example.lagvis_v1.ui.theme.AppFont
import com.example.lagvis_v1.ui.theme.LagVis_V1Theme
import com.example.lagvis_v1.ui.common.LookupViewModel
import com.example.lagvis_v1.ui.common.LookupViewModelFactory
import com.example.lagvis_v1.ui.common.UiItem
import com.example.lagvis_v1.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AdvancedFormRegisterCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ⬇️ Lee posibles extras (todos nullables)
        val initialGiven  : String?    = intent.getStringExtra(NavKeys.EXTRA_GIVEN)
        val initialFamily : String?    = intent.getStringExtra(NavKeys.EXTRA_FAMILY)
        val initialBirthS : String?    = intent.getStringExtra(NavKeys.EXTRA_BIRTH)
        val initialBirth  : LocalDate? = initialBirthS?.let { LocalDate.parse(it) }

        setContent {
            LagVis_V1Theme {
                val vm: AdvancedFormViewModel = viewModel(factory = AdvancedFormViewModelFactory())
                val state by vm.submit.observeAsState()
                val userUid = remember { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

                LaunchedEffect(state) {
                    when (state) {
                        is UiState.Success -> {
                            startActivity(Intent(this@AdvancedFormRegisterCompose, MainActivity::class.java))
                            finish()
                        }
                        is UiState.Error -> {
                            val msg = (state as UiState.Error).message ?: "Error desconocido"
                            Toast.makeText(this@AdvancedFormRegisterCompose, msg, Toast.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }

                AdvancedFormRegisterScreen(
                    userUid = userUid,
                    // ⬇️ Prefills desde Intent
                    initialGivenName = initialGiven,
                    initialFamilyName = initialFamily,
                    initialBirthDate = initialBirth,
                    onNext = { data ->
                        vm.send(
                            data.uid,
                            data.nombre,
                            data.apellido,
                            data.apellido2,
                            data.comunidad,
                            data.sector,
                            data.fechaNacimiento
                        )
                    }
                )
            }
        }
    }
}

/* -------------------- UI -------------------- */

data class AdvancedRegisterData(
    val uid: String,
    val nombre: String,
    val apellido: String,
    val apellido2: String,
    val comunidad: String,       // ID en String
    val sector: String,          // ID en String
    val fechaNacimiento: String  // "dd/MM/yyyy"
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFormRegisterScreen(
    userUid: String,
    initialGivenName: String? = null,
    initialFamilyName: String? = null,
    initialBirthDate: LocalDate? = null,
    modifier: Modifier = Modifier,
    onNext: (AdvancedRegisterData) -> Unit = {}
) {
    // Animación de entrada
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val cardScale by animateFloatAsState(
        targetValue = if (started) 1f else 0.98f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "scale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    val cardTranslateY by animateFloatAsState(
        targetValue = if (started) 0f else 20f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "ty"
    )

    // Estados con prefill
    var nombre by rememberSaveable { mutableStateOf(initialGivenName.orEmpty()) }
    var apellido by rememberSaveable { mutableStateOf(initialFamilyName.orEmpty()) }
    var apellido2 by rememberSaveable { mutableStateOf("") }

    // Selección visible (texto) + IDs reales
    var comunidadUi by rememberSaveable { mutableStateOf("") }         // nombre mostrado
    var selectedComunidadId by rememberSaveable { mutableStateOf("") } // id real
    var sectorUi by rememberSaveable { mutableStateOf("") }            // nombre mostrado
    var selectedSectorId by rememberSaveable { mutableStateOf("") }    // id real

    // Lookup ViewModel
    val lookupVm: LookupViewModel = viewModel(factory = LookupViewModelFactory())
    val comunidades: List<UiItem> by lookupVm.comunidades.collectAsStateWithLifecycle(initialValue = emptyList())
    val sectores: List<UiItem> by lookupVm.sectores.collectAsStateWithLifecycle(initialValue = emptyList())

    // DatePicker con fecha inicial (si viene)
    val initialMillis = remember(initialBirthDate) {
        initialBirthDate
            ?.atStartOfDay(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
    }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val fechaSeleccionada: LocalDate? = dateState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    val fechaStr = fechaSeleccionada?.format(dateFormatter) ?: ""

    // Validaciones mínimas
    val nombreOk = nombre.trim().length >= 2
    val apellidoOk = apellido.trim().length >= 2
    val comunidadOk = selectedComunidadId.isNotBlank()
    val sectorOk = selectedSectorId.isNotBlank()
    val fechaOk = fechaSeleccionada != null
    val formOk = nombreOk && apellidoOk && comunidadOk && sectorOk && fechaOk && userUid.isNotBlank()

    // Fondo / header
    val cs = MaterialTheme.colorScheme
    val gradient = Brush.verticalGradient(
        listOf(cs.primary.copy(alpha = 0.25f), cs.primaryContainer.copy(alpha = 0.35f), cs.surface)
    )
    val headerHeight = 220.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // ---------- HEADER (aquí estaba faltando) ----------
        Box(
            Modifier
                .fillMaxWidth()
                .height(headerHeight)
        ) {
            HeaderWave(Modifier.matchParentSize())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 56.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Formulario\nde registro.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = AppFont,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // ---------- CONTENIDO ----------
        Box(
            Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
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
                val scrollState = rememberScrollState()
                Column(
                    Modifier
                        .verticalScroll(scrollState)
                        .imePadding()
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 4.dp)
                    )

                    // Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre", fontFamily = AppFont, fontWeight = FontWeight.SemiBold) },
                        isError = nombre.isNotEmpty() && !nombreOk,
                        supportingText = { if (nombre.isNotEmpty() && !nombreOk) Text("Mínimo 2 caracteres") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Apellido
                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text("Apellido", fontFamily = AppFont, fontWeight = FontWeight.SemiBold) },
                        isError = apellido.isNotEmpty() && !apellidoOk,
                        supportingText = { if (apellido.isNotEmpty() && !apellidoOk) Text("Mínimo 2 caracteres") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Segundo apellido (opcional)
                    OutlinedTextField(
                        value = apellido2,
                        onValueChange = { apellido2 = it },
                        label = { Text("Segundo apellido (opcional)", fontFamily = AppFont, fontWeight = FontWeight.SemiBold) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ---------- Comunidad (dropdown) ----------
                    var expandedCA by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedCA,
                        onExpandedChange = { expandedCA = it }
                    ) {
                        OutlinedTextField(
                            value = comunidadUi,
                            onValueChange = { /* readOnly */ },
                            readOnly = true,
                            label = { Text("Comunidad Autónoma", fontFamily = AppFont, fontWeight = FontWeight.SemiBold) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCA) },
                            isError = comunidadUi.isNotEmpty() && !comunidadOk,
                            supportingText = { if (!comunidadOk && comunidadUi.isNotEmpty()) Text("Selecciona una opción") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            enabled = comunidades.isNotEmpty()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCA,
                            onDismissRequest = { expandedCA = false }
                        ) {
                            comunidades.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.nombre) },
                                    onClick = {
                                        comunidadUi = item.nombre
                                        selectedComunidadId = item.id
                                        expandedCA = false
                                    }
                                )
                            }
                        }
                    }

                    // ---------- Sector (dropdown) ----------
                    var expandedSector by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedSector,
                        onExpandedChange = { expandedSector = it }
                    ) {
                        OutlinedTextField(
                            value = sectorUi,
                            onValueChange = { /* readOnly */ },
                            readOnly = true,
                            label = { Text("Sector laboral", fontFamily = AppFont, fontWeight = FontWeight.SemiBold) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSector) },
                            isError = sectorUi.isNotEmpty() && !sectorOk,
                            supportingText = { if (!sectorOk && sectorUi.isNotEmpty()) Text("Selecciona una opción") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            enabled = sectores.isNotEmpty()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSector,
                            onDismissRequest = { expandedSector = false }
                        ) {
                            sectores.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.nombre) },
                                    onClick = {
                                        sectorUi = item.nombre
                                        selectedSectorId = item.id
                                        expandedSector = false
                                    }
                                )
                            }
                        }
                    }

                    // Fecha
                    OutlinedTextField(
                        value = fechaStr,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha de nacimiento", fontFamily = AppFont, fontWeight = FontWeight.SemiBold) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Filled.CalendarToday, contentDescription = "Elegir fecha")
                            }
                        },
                        isError = !fechaOk,
                        supportingText = { if (!fechaOk) Text("Requerida") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = { showDatePicker = false }) { Text("Aceptar") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                            }
                        ) { DatePicker(state = dateState) }
                    }

                    // Botón Siguiente
                    AppButton(
                        text = "Siguiente",
                        enabled = formOk,
                        onClick = {
                            onNext(
                                AdvancedRegisterData(
                                    uid = userUid,
                                    nombre = nombre.trim(),
                                    apellido = apellido.trim(),
                                    apellido2 = apellido2.trim(),
                                    comunidad = selectedComunidadId,
                                    sector = selectedSectorId,
                                    fechaNacimiento = fechaStr
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Composable
fun HeaderWave(modifier: Modifier = Modifier, height: Dp = 220.dp) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .statusBarsPadding()
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(
                brush = Brush.verticalGradient(
                    0f to cs.primaryContainer,
                    1f to cs.primary
                ),
                size = size
            )
            val path = Path().apply {
                moveTo(0f, h * 0.55f)
                cubicTo(w * 0.25f, h * 0.50f, w * 0.25f, h * 0.85f, w * 0.5f, h * 0.80f)
                cubicTo(w * 0.75f, h * 0.75f, w * 0.85f, h * 0.55f, w, h * 0.60f)
                lineTo(w, 0f); lineTo(0f, 0f); close()
            }
            drawPath(path, color = cs.primary.copy(alpha = 0.25f))
        }
    }
}

/* -------------------- Preview -------------------- */
@Preview(showBackground = true)
@Composable
private fun AdvancedFormRegisterPreviewLight() {
    LagVis_V1Theme {
        AdvancedFormRegisterScreen(userUid = "demo-uid")
    }
}
