package com.example.lagvis_v1.ui.auth.uicompose.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = Color.White,

    secondary = SecondaryDark,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = Color.Black,

    tertiary = AccentBlue,
    onTertiary = Color.Black,

    background = BgLight,
    onBackground = Color(0xFF000000),
    surface = SurfaceLight,
    onSurface = Color(0xFF000000)
)

private val DarkColors = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = Color.Black,
    primaryContainer = BluePrimaryContainerDark,
    onPrimaryContainer = Color.White,

    secondary = SecondaryDarkDark,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = Color.White,

    tertiary = AccentBlueDark,
    onTertiary = Color.Black,

    background = BgDark,
    onBackground = Color(0xFFFFFFFF),
    surface = SurfaceDark,
    onSurface = Color(0xFFFFFFFF)
)


@Composable
fun LagVis_V1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val ctx = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
    } else {
        if (darkTheme) DarkColors else LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
