package com.example.planit.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planit.model.Project
import com.example.planit.model.Task
import com.example.planit.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onProjectSelected: (String) -> Unit,
    onAddProject: () -> Unit
) {
    val projects = viewModel.projects.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("PlanIt") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProject) {
                Text("+")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                errorMessage != null -> Text(text = errorMessage, color = MaterialTheme.colors.error)
                projects.isEmpty() -> Text("No projects found.")
                else -> ProjectList(projects, onProjectSelected, viewModel::updateProjectByName, viewModel::deleteProjectByName)
            }
        }
    }
}

@Composable
fun ProjectList(
    projects: List<Project>,
    onProjectSelected: (String) -> Unit,
    onProjectChanged: (String, Project) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(projects.size) { index ->
            val project = projects[index]
            ProjectListItem(project, onProjectSelected, onProjectChanged, onDeleteProject)
        }
    }
}

@Composable
fun ProjectListItem(
    project: Project,
    onProjectSelected: (String) -> Unit,
    onProjectChanged: (String, Project) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    var newTaskName by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = project.name, style = MaterialTheme.typography.h6)
            Text(text = project.description, style = MaterialTheme.typography.body2)
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
                            onProjectChanged(project.name, updatedProject)
                        }
                    )
                    Text(text = task.name)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add New Task
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = newTaskName,
                    onValueChange = { newTaskName = it },
                    label = { Text("New Task") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newTaskName.isNotBlank()) {
                            val newTask = Task(name = newTaskName)
                            val updatedTasks = project.tasks + newTask
                            val updatedProject = project.copy(tasks = updatedTasks)
                            onProjectChanged(project.name, updatedProject)
                            newTaskName = ""
                        }
                    }
                ) {
                    Text("Add Task")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                ClickableText(text = AnnotatedString("View"), onClick = { onProjectSelected(project.name) })
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDeleteProject(project.name) }) {
                    Text("Delete")
                }
            }
        }
    }
}