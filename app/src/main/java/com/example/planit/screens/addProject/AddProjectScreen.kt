package com.example.planit.screens.addProject

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planit.R
import com.example.planit.model.Project
import com.example.planit.model.Task
import com.example.planit.viewModel.ProjectsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddProjectScreen(
    closeProject: () -> Unit,
    onSave: (Project) -> Unit,
    projectsViewModel: ProjectsViewModel = viewModel(),
    modifier: Modifier = Modifier
        .fillMaxSize()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        var projectName by remember { mutableStateOf("") }
        var projectDescription by remember { mutableStateOf("") }
        var startDate by remember { mutableStateOf("") }
        var finalDate by remember { mutableStateOf("") }
        var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
        var newTaskName by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                onClick = closeProject,
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    Modifier.size(28.dp)
                )
            }
            Text(
                text = "Add a new project",
                modifier = Modifier
                    .padding(top = 30.dp)
            )

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

            DatePickerFieldToModal(
                label = "Beginning Date",
                selectedDate = startDate,
                onSelectedDateChange = {
                    startDate = it
                }
            )

            DatePickerFieldToModal(
                label = "Ending Date",
                selectedDate = finalDate,
                onSelectedDateChange = {
                    finalDate = it
                })

            Divider()

            Text(text = "Tasks", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newTaskName,
                    onValueChange = { newTaskName = it },
                    label = { Text("New Task") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (newTaskName.isNotBlank()) {
                            // Add new task to the list
                            tasks = tasks + Task(name = newTaskName)
                            newTaskName = ""
                        }
                    },
                ) {
                    Text("+")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                tasks.forEach { task ->
                    Text("- ${task.name}", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { closeProject() }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (projectName.isNotBlank() && startDate.isNotBlank() && finalDate.isNotBlank()) {
                            val newProject = Project(
                                name = projectName,
                                description = projectDescription,
                                startDate = startDate,
                                finalDate = finalDate,
                                tasks = tasks
                            )

                            scope.launch {
                                try {
                                    projectsViewModel.addProject(newProject)
                                    onSave(newProject)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}


@Composable
fun DatePickerFieldToModal(
    label: String,
    selectedDate: String,
    onSelectedDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = { },
        label = { Text(text = label) },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
            .background(MaterialTheme.colorScheme.surface)
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = {
                if (it != null) {
                    onSelectedDateChange(it.toDateString())
                }
             },
            onDismiss = { showModal = false }
        )
    }
}

fun Long.toDateString(): String {
    val dateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return dateFormatter.format(Date(this))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}