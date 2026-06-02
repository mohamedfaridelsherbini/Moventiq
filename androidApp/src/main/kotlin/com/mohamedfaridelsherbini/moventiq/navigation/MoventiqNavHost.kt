package com.mohamedfaridelsherbini.moventiq.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mohamedfaridelsherbini.moventiq.ui.home.PlaceholderHomeScreen

@Composable
fun MoventiqNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = modifier,
    ) {
        composable(Routes.Home) {
            PlaceholderHomeScreen()
        }
    }
}
