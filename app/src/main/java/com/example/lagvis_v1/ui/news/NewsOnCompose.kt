package com.example.lagvis_v1.ui.news

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lagvis_v1.ui.theme.AppFont
import com.example.lagvis_v1.ui.common.DropdownOutlinedM3
import com.example.lagvis_v1.ui.common.HeaderGradientParallax
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.model.news.NewsItemKt

class NewsOnCompose {


    @Composable
    fun NewsScreen(
        listaCategorias: List<String> = NewsCategoryTranslations.EN_TO_ES.values.toList(),
    ) {
        val vm: NewsViewModelKt = androidx.lifecycle.viewmodel.compose.viewModel(factory = NewsViewModelFactoryKt())

        var categoriaNoticia by rememberSaveable { mutableStateOf("Selecciona categoría") }
        var currentIndex by rememberSaveable { mutableStateOf(0) }

        // 👇 Nuevo: indica si ya hiciste una petición al VM
        var hasRequested by rememberSaveable { mutableStateOf(false) }

        val state by vm.state.observeAsState(UiState.Loading())

        val list: List<NewsItemKt> = when (val s = state) {
            is UiState.Success -> s.data.orEmpty()
            else -> emptyList()
        }

        LaunchedEffect(list.size) {
            if (list.isNotEmpty() && currentIndex !in list.indices) currentIndex = 0
        }

        Scaffold(
            topBar = {
                HeaderGradientParallax(
                    title = "Noticias",
                    subtitle = "Elige tu categoría",
                    showBack = false,
                    leadingIcon = Icons.Outlined.Newspaper
                )
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                // ===== Selector categoría =====
                item {
                    Column {
                        Text(
                            "Seleccione una categoría personalizada",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = AppFont
                        )
                        Spacer(Modifier.height(8.dp))

                        DropdownOutlinedM3(
                            value = categoriaNoticia,
                            options = listaCategorias,
                            onSelect = { seleccionEs ->
                                categoriaNoticia = seleccionEs
                                currentIndex = 0
                                hasRequested = true // 👈 marcamos que ya se solicitó
                                vm.loadByCategory(categoryEs = seleccionEs, country = "es")
                            },
                            placeholder = "Selecciona categoría",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // ===== Contenido según estado =====
                when (state) {
                    is UiState.Loading -> {
                        item {
                            Spacer(Modifier.height(16.dp))
                            if (!hasRequested) {
                                // 👇 Mensaje inicial en vez de barra de carga
                                Text(
                                    text = "Elige una categoría para ver noticias.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = AppFont
                                )
                            } else {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }

                    is UiState.Error -> {
                        val msg = (state as UiState.Error).message ?: "Error al cargar noticias"
                        item {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = AppFont
                            )
                        }
                    }

                    is UiState.Success -> {
                        if (list.isEmpty()) {
                            item {
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    if (!hasRequested)
                                        "Elige una categoría para ver noticias."
                                    else
                                        "No hay noticias para esta categoría.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = AppFont
                                )
                            }
                        } else {
                            val current = list[currentIndex]

                            item {
                                Spacer(Modifier.height(8.dp))
                                NewsDetailCard(
                                    title = current.title.orEmpty(),
                                    pubDate = current.pubDate.orEmpty(),
                                    creator = current.creator,
                                    link = current.link,
                                    onPrev = { if (currentIndex > 0) currentIndex-- },
                                    onSave = {
                                        val uid = com.google.firebase.auth.FirebaseAuth
                                            .getInstance().currentUser?.uid
                                        if (!uid.isNullOrBlank()) vm.save(uid, current)
                                    },
                                    onNext = { if (currentIndex < list.lastIndex) currentIndex++ }
                                )
                            }

                            item {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "${currentIndex + 1} / ${list.size}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = AppFont
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}

@Composable
fun NewsDetailCard(
    title: String,
    pubDate: String,
    creator: String?,
    link: String?,
    modifier: Modifier = Modifier,
    onPrev: () -> Unit = {},
    onSave: () -> Unit = {},
    onNext: () -> Unit = {},
    onOpenLink: (() -> Unit)? = null,
) {
    val ctx = LocalContext.current

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(16.dp)) {

            // Título
            Text(
                text = title.ifBlank { "Sin título" },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = AppFont
            )

            Spacer(Modifier.height(12.dp))

            // Fecha (línea 1 con texto fijo)
            Text(
                text = "📅 Fecha noticia:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = AppFont
            )

            // Fecha (línea 2 con valor)
            Text(
                text = pubDate.ifBlank { "—" },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = AppFont,
                modifier = Modifier.padding(top = 6.dp)
            )

            // Creador (línea 1 con texto fijo)
            Spacer(Modifier.height(10.dp))
            Text(
                text = "🖋️ Noticia publicada por:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = AppFont
            )

            // Creador (línea 2 con valor)
            Text(
                text = (creator ?: "").ifBlank { "—" },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = AppFont,
                modifier = Modifier.padding(top = 6.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Enlace
            if (!link.isNullOrBlank()) {
                Spacer(Modifier.height(25.dp))
                Text(
                    text = "🔗 $link",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = AppFont,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                // Botón abrir enlace (si lo quieres separado)
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        onOpenLink?.invoke() ?: run {
                            // Abrir en navegador por defecto
                            runCatching {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                ctx.startActivity(intent)
                            }
                        }
                    }
                ) {
                    Text("Abrir enlace")
                }
            }

            // Botonera
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onPrev,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Anterior", fontFamily = AppFont)
                }
                /*Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Guardar", fontFamily = AppFont)
                }*/
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Siguiente", fontFamily = AppFont)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewNews() {
    NewsOnCompose().NewsScreen()
    }
