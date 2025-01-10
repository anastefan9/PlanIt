package com.example.planit.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planit.R
import com.example.planit.model.Project
import com.example.planit.model.Task
import com.example.planit.viewModel.ProjectsViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    toAddProject: () -> Unit,
    projectsViewModel: ProjectsViewModel,
    modifier: Modifier = Modifier
        .fillMaxSize()
) {
//    val projects = projectsViewModel.projects.value
    LaunchedEffect(Unit) {
        homeViewModel.fetchProjects()
    }

    val projects = homeViewModel.projects.value

    Scaffold(
        topBar = { TopAppBar(title = { Text("Projects") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { toAddProject() }) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (projects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No projects available.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(projects) { project ->
                        ProjectItem(
                            project = project,
                            onProjectChanged = { updatedProject ->
                                homeViewModel.updateProject(updatedProject)
                            },
                            onDeleteProject = {
                                homeViewModel.deleteProject(project.documentPath?.substringAfter("/(default)/"))
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun AddButton(
    onClick: () -> Unit,
    text: String = "Add",
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(colorResource(R.color.purple_200)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text)
        }
    }
}

@Composable
fun ProjectItem(
    project: Project,
    onProjectChanged: (Project) -> Unit,
    onDeleteProject: (String?) -> Unit // Accept nullable documentPath
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = project.name,
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(onClick = { onDeleteProject(project.documentPath) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Project")
            }
        }
        Text(text = project.description)
        Text(text = "Start: ${project.startDate}")
        Text(text = "End: ${project.finalDate}")
        Spacer(modifier = Modifier.height(8.dp))
        // Task List
        project.tasks.forEachIndexed { index, task ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = { isChecked ->
                        val updatedTask = task.copy(isDone = isChecked)
                        val updatedTasks = project.tasks.toMutableList()
                        updatedTasks[index] = updatedTask
                        val updatedProject = project.copy(tasks = updatedTasks)
                        onProjectChanged(updatedProject)
                    }
                )
                Text(text = task.name)
            }
        }
    }
}

