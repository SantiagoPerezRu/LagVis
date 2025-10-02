package com.example.lagvis_v1.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState

fun NavController.navigateSingleTop(route: String, builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.startDestinationId) { saveState = true }
        builder?.invoke(this)
    }
}

@Composable
fun NavController.currentRoute(): String? =
    currentBackStackEntryAsState().value?.destination?.route