package com.example.planit

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.planit.screens.addProject.AddProjectScreen
import com.example.planit.screens.home.HomeScreen
import com.example.planit.viewModel.HomeViewModel

private object Routes {
    const val home = "home"
    const val addProject = "addProject"
}

sealed class Screen(val route: String) {
    object Home : Screen(Routes.home)
    object AddProject : Screen(Routes.addProject)
}

@Composable
fun RootNavGraph(navHostController: NavHostController) {
    val startDestination = Screen.Home.route
    val homeViewModel: HomeViewModel = viewModel() // Initialize HomeViewModel

    NavHost(navController = navHostController, startDestination = startDestination) {
        // Home Screen
        composable(route = Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onProjectSelected = { projectName ->
                    // Navigate to a project details screen (implement if needed)
                    navHostController.navigate("projectDetails/$projectName")
                },
                onAddProject = {
                    // Navigate to Add Project Screen
                    navHostController.navigate(Screen.AddProject.route)
                }
            )
        }

        // Add Project Screen
        composable(route = Screen.AddProject.route) {
            AddProjectScreen(
                closeProject = { navHostController.popBackStack() },
                onSave = { newProject ->
                    homeViewModel.addProject(newProject)
                    navHostController.popBackStack()
                }
            )
        }
    }
}