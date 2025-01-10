package com.example.planit

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.planit.screens.addProject.AddProjectScreen
import com.example.planit.screens.home.HomeScreen
import com.example.planit.viewModel.ProjectsViewModel


private object Routes {
    const val home = "home"
    const val addProject = "addProject"
}

sealed class Screen(
    val route: String,
) {
    object Home: Screen(Routes.home)
    object AddProject: Screen(Routes.addProject)
}

@Composable
fun RootNavGraph(navHostController: NavHostController) {
    val startDestination = Screen.Home.route
    val projectsViewModel: ProjectsViewModel = viewModel()

    NavHost(navController = navHostController, startDestination = startDestination) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                toAddProject = { navHostController.navigate(Screen.AddProject.route) },
                projectsViewModel = projectsViewModel)
        }
        composable(route = Screen.AddProject.route) {
            AddProjectScreen(
                closeProject = { navHostController.popBackStack() },
                onSave = { newProject ->
                    projectsViewModel.addProject(newProject)
                    navHostController.popBackStack()
                })
        }
    }
}