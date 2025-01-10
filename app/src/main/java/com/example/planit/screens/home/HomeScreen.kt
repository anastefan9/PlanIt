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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
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
    LaunchedEffect(Unit) {
        homeViewModel.fetchProjects()
    }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
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
                            },
                            onEditProject = { projectToEdit -> selectedProject = projectToEdit }
                        )
                        Divider()
                    }
                }
            }
        }
        if (selectedProject != null) {
            EditProjectDialog(
                project = selectedProject,
                onDismiss = { selectedProject = null },
                onSave = { updatedProject ->
                    homeViewModel.editProject(updatedProject)
                    selectedProject = null
                }
            )
        }
    }
}

@Composable
fun EditProjectDialog(
    project: Project?,
    onDismiss: () -> Unit,
    onSave: (Project) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            EditProjectScreen(
                project = project,
                closeScreen = onDismiss,
                onSave = onSave
            )
        }
    }
}

@Composable
fun EditProjectScreen(
    project: Project?,
    closeScreen: () -> Unit,
    onSave: (Project) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    var projectName by remember { mutableStateOf(project?.name ?: "") }
    var projectDescription by remember { mutableStateOf(project?.description ?: "") }
    var startDate by remember { mutableStateOf(project?.startDate ?: "") }
    var finalDate by remember { mutableStateOf(project?.finalDate ?: "") }
    var tasks by remember { mutableStateOf(project?.tasks ?: emptyList()) }
    var newTaskName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = if (project == null) "Add Project" else "Edit Project", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = { Text("Project Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = projectDescription,
            onValueChange = { projectDescription = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (projectName.isNotBlank()) {
                    val updatedProject = project?.copy(
                        name = projectName,
                        description = projectDescription,
                        startDate = startDate,
                        finalDate = finalDate,
                        tasks = tasks
                    ) ?: Project(
                        name = projectName,
                        description = projectDescription,
                        startDate = startDate,
                        finalDate = finalDate,
                        tasks = tasks
                    )
                    onSave(updatedProject)
                }
            }
        ) {
            Text("Save")
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
    onDeleteProject: (String?) -> Unit,
    onEditProject: (Project) -> Unit
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
            Row {
                IconButton(onClick = { onEditProject(project) }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Project")
                }
                IconButton(onClick = { onDeleteProject(project.documentPath) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Project")
                }
            }
        }
        Text(text = project.description)
        Text(text = "Start: ${project.startDate}")
        Text(text = "End: ${project.finalDate}")
        Spacer(modifier = Modifier.height(8.dp))
    }
}

