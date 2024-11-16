package com.gems.toplan.navigation

import com.gems.toplan.ui.model.TodoViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gems.toplan.ui.screen.DoScreen
import com.gems.toplan.ui.screen.ItemScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = NavigationItems.DoScreen.route,
    viewModel: TodoViewModel
) {

    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItems.DoScreen.route) {
            DoScreen(navController)
        }

        composable(
            route = NavigationItems.ItemScreen.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("id") ?: ""
            ItemScreen(rememberNavController(), viewModel, taskId)
        }


    }
}


