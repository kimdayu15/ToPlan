package com.gems.toplan.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gems.toplan.ui.model.TodoViewModel
import com.gems.toplan.ui.screen.DoScreen
import com.gems.toplan.ui.screen.ItemScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: TodoViewModel = TodoViewModel(),
    startDestination: String = NavigationItems.DoScreen.route
) {
    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItems.DoScreen.route) {
            DoScreen(
                newTask = { navController.navigate(NavigationItems.ItemScreen.createRoute()) },
                navController = navController,
                viewModel = viewModel,
                updateTask = { task ->
                    navController.navigate(NavigationItems.ItemScreen.createRoute(task.id))
                }
            )
        }

        composable(
            route = NavigationItems.ItemScreen.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("id").orEmpty()
            ItemScreen(navController = navController, viewModel = viewModel, taskId = taskId)
        }
    }
}
