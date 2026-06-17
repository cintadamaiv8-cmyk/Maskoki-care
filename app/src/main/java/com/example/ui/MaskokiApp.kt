package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Splash : Screen("splash", "Splash", Icons.Filled.Home)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Home)
    object Feeding : Screen("feeding", "Pakan", Icons.Filled.Pets)
    object FeedingHistory : Screen("feeding_history", "Riwayat Pakan", Icons.Filled.History)
    object Water : Screen("water", "Air", Icons.Filled.WaterDrop)
    object Maintenance : Screen("maintenance", "Tank", Icons.Filled.LocalDrink)
    object Settings : Screen("settings", "Pengaturan", Icons.Filled.Settings)
}

@Composable
fun MaskokiApp(viewModel: MaskokiViewModel) {
    val navController = rememberNavController()

    val bottomNavScreens = listOf(
        Screen.Dashboard,
        Screen.Feeding,
        Screen.Water,
        Screen.Maintenance,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Splash.route) {
                NavigationBar(containerColor = Color(0xFF1A1A1A)) {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(Screen.Dashboard.route) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) { SplashScreen(navController) }
            composable(Screen.Dashboard.route) { DashboardScreen(viewModel, navController) }
            composable(Screen.Feeding.route) { FeedingScreen(viewModel, navController) }
            composable(Screen.FeedingHistory.route) { FeedingHistoryScreen(viewModel) }
            composable(Screen.Water.route) { WaterChangeScreen(viewModel) }
            composable(Screen.Maintenance.route) { MaintenanceScreen(viewModel) }
            composable(Screen.Settings.route) { SettingsScreen(viewModel) }
        }
    }
}
