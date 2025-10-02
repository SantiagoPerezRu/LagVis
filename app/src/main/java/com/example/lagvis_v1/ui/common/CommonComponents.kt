@file:Suppress("UnusedImport")
package com.example.lagvis_v1.ui.common
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.lagvis_v1.ui.auth.uicompose.ui.theme.AppFont

    /* ================== Dropdown M3 reutilizable ================== */

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DropdownOutlinedM3(
        value: String,
        options: List<String>,
        onSelect: (String) -> Unit,
        placeholder: String,
        modifier: Modifier = Modifier
    ) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = modifier
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { /* readOnly */ },
                readOnly = true,
                label = { Text(placeholder, fontFamily = AppFont) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                fontFamily = AppFont,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }


/* =========================================================
 * 1) Aurora Glass — blobs + tarjeta translúcida
 * ========================================================= */
@Composable
fun HeaderAuroraGlass(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    height: Dp = 240.dp,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        // Fondo aurora con degradados y blobs
        Canvas(Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            // base vertical
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        cs.primary.copy(alpha = 0.95f),
                        cs.primary.copy(alpha = 0.70f),
                        cs.surface
                    )
                ),
                size = size
            )
            // blob izq
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(cs.secondary.copy(alpha = 0.60f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.25f),
                    radius = h * 0.55f
                ),
                radius = h * 0.55f,
                center = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.25f)
            )
            // blob der
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(cs.tertiary.copy(alpha = 0.55f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.20f),
                    radius = h * 0.50f
                ),
                radius = h * 0.50f,
                center = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.20f)
            )
            // cierre curvo inferior
            val curve = Path().apply {
                moveTo(0f, h * 0.80f)
                cubicTo(w * 0.25f, h * 0.92f, w * 0.75f, h * 0.68f, w, h * 0.80f)
                lineTo(w, h); lineTo(0f, h); close()
            }
            drawPath(
                curve,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.10f), Color.Transparent)
                )
            )
        }

        // barra superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                }
            } else Spacer(Modifier.width(48.dp))

            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f),
                maxLines = 1
            )

            if (onAction != null) {
                IconButton(onClick = onAction) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = "Acción", tint = Color.White)
                }
            } else Spacer(Modifier.width(48.dp))
        }

        // tarjeta “glass”
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.14f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .align(Alignment.BottomCenter)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black, color = Color.White
                    ),
                    lineHeight = MaterialTheme.typography.headlineSmall.lineHeight
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHeaderAuroraGlass() {
    MaterialTheme {
        HeaderAuroraGlass(
            title = "Tu perfil",
            subtitle = "Gestiona tu información",
            showBack = true,
            onBack = {},
            onAction = {}
        )
    }
}

/* =========================================================
 * 2) Gradient Parallax — imagen de fondo + overlay
 * ========================================================= */
@Composable
fun HeaderGradientParallax(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    height: Dp = 220.dp,
    imageRes: Int? = null,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    leadingIcon: ImageVector? = null
) {
    val overlay = Brush.verticalGradient(
        0f to Color.Black.copy(alpha = 0.45f),
        0.6f to Color.Transparent,
        1f to Color.Black.copy(alpha = 0.50f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // degradado si no hay imagen
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
            )
        }

        Box(Modifier.matchParentSize().background(overlay))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                }
            } else Spacer(Modifier.width(48.dp))

            /*   Text(
                   text = title,
                   color = Color.White,
                   style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                   modifier = Modifier.weight(1f),
                   maxLines = 1
               )*/

            if (onAction != null) {
                IconButton(onClick = onAction) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = "Acción", tint = Color.White)
                }
            } else Spacer(Modifier.width(48.dp))
        }

        // copy en la parte baja
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically){
                if (leadingIcon !=null ){
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(text = subtitle, color = Color.White.copy(alpha = 0.9f))
            }

        }
    }
}

@Preview
@Composable
private fun PreviewHeaderGradientParallax() {
    MaterialTheme { HeaderGradientParallax("Explorar", "Recomendado para ti") }
}

/* =========================================================
 * 3) Collapsing LargeTopAppBar — M3 nativo con scroll
 *    (úsalo junto a un LazyColumn con nestedScroll)
 * ========================================================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderCollapsingLargeTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        titleContentColor = MaterialTheme.colorScheme.onSurface
    )
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LargeTopAppBar(
        title = {
            Column {
                Text(title, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
            }
        },
        actions = {
            if (onAction != null) {
                IconButton(onClick = onAction) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = "Acción")
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = colors
    )
    // Nota: En tu pantalla, añade:
    // val nested = scrollBehavior.nestedScrollConnection
    // Scaffold(modifier = Modifier.nestedScroll(nested)) { ...LazyColumn(...) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewHeaderCollapsingLargeTopBar() {
    MaterialTheme { HeaderCollapsingLargeTopBar("Noticias", "Hoy") }
}

/* =========================================================
 * 4) Hero con Avatar — perfil / cuenta / autor
 * ========================================================= */
@Composable
fun HeaderHeroWithAvatar(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    avatar: (@Composable () -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                Brush.linearGradient(
                    listOf(cs.primary, cs.primaryContainer, cs.surfaceVariant)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                }
            } else Spacer(Modifier.width(48.dp))
            Spacer(Modifier.weight(1f))
            if (onAction != null) {
                IconButton(onClick = onAction) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = "Acción", tint = Color.White)
                }
            } else Spacer(Modifier.width(48.dp))
        }

        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (avatar != null) {
                    Box(
                        Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) { avatar() }
                    Spacer(Modifier.width(14.dp))
                }
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            subtitle,
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHeaderHeroWithAvatar() {
    MaterialTheme {
        HeaderHeroWithAvatar(
            title = "María Gómez",
            subtitle = "@mariag",
            avatar = {
                // Avatar de ejemplo
                Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.4f)))
            }
        )
    }
}

/* =========================================================
 * 5) Minimal Edge-to-Edge — top bar transparente sobre imagen
 *    Perfecto para pantallas de detalle con hero image
 * ========================================================= */
@Composable
fun HeaderEdgeToEdge(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    height: Dp = 260.dp,
    imageRes: Int? = null,
    darkScrim: Boolean = true,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null
) {
    val scrim = if (darkScrim)
        Brush.verticalGradient(0f to Color.Black.copy(alpha = 0.55f), 0.7f to Color.Transparent)
    else
        Brush.verticalGradient(0f to Color.White.copy(alpha = 0.55f), 0.7f to Color.Transparent)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(Modifier.matchParentSize().background(MaterialTheme.colorScheme.primary))
        }

        // scrim superior para contraste de iconos
        Box(Modifier.fillMaxWidth().height(140.dp).background(scrim))

        // barra flotante
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                FilledIconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.35f),
                        contentColor = Color.White
                    ),
                    shape = CircleShape
                ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") }
            } else Spacer(Modifier.width(40.dp))

            Spacer(Modifier.weight(1f))

            if (onAction != null) {
                FilledIconButton(
                    onClick = onAction,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.35f),
                        contentColor = Color.White
                    ),
                    shape = CircleShape
                ) { Icon(Icons.Outlined.MoreVert, contentDescription = "Acción") }
            } else Spacer(Modifier.width(40.dp))
        }

        // títulos en la base
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(text = subtitle, color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHeaderEdgeToEdge() {
    MaterialTheme { HeaderEdgeToEdge(title = "Artículo destacado", subtitle = "Fotografía urbana") }
}
