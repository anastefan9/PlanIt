package com.example.planit.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planit.data.FirestoreRetrofitClient
import com.example.planit.model.ArrayValue
import com.example.planit.model.FirestoreField
import com.example.planit.model.FirestoreRequest
import com.example.planit.model.Project
import com.example.planit.model.Task
import kotlinx.coroutines.launch
import java.util.UUID

class ProjectsViewModel: ViewModel() {
    private val _projects = mutableStateOf<List<Project>>(emptyList())
    val projects: State<List<Project>> = _projects


    fun fetchProjects() {
        viewModelScope.launch {
            try {
                val response = FirestoreRetrofitClient.apiService.getProjects()

                val projectList = response.documents.mapNotNull { doc ->
                    try {

                        val idString = doc.fields["id"]?.stringValue.orEmpty()
                        val projectId = if (idString.isNotBlank()) UUID.fromString(idString) else UUID.randomUUID()


                        Project(
                            id = projectId,
                            name = doc.fields["name"]?.stringValue.orEmpty().trim(),
                            description = doc.fields["description"]?.stringValue.orEmpty().trim(),
                            startDate = doc.fields["startDate"]?.stringValue.orEmpty().trim(),
                            finalDate = doc.fields["finalDate"]?.stringValue.orEmpty().trim(),
                            tasks = doc.fields["tasks"]?.arrayValue?.values?.map { taskField ->
                                Task(name = taskField.stringValue.orEmpty())
                            } ?: emptyList(),
                            // Extract the documentPath to use in operations like deletion
                            documentPath = doc.name.replace(
                                "projects/<your-project-id>/databases/(default)/documents/",
                                ""
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                _projects.value = projectList
                println("Mapped Projects: $projectList")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addProject(project: Project) {
        viewModelScope.launch {
            try {
                val firestoreRequest = FirestoreRequest(
                    fields = mapOf(
                        "id" to FirestoreField(stringValue = project.id.toString()),
                        "name" to FirestoreField(stringValue = project.name),
                        "description" to FirestoreField(stringValue = project.description),
                        "startDate" to FirestoreField(stringValue = project.startDate),
                        "finalDate" to FirestoreField(stringValue = project.finalDate),
                        "tasks" to FirestoreField(
                            arrayValue = ArrayValue(
                                values = project.tasks.map { task -> FirestoreField(stringValue = task.name) }
                            )
                        )
                    )
                )

                val response = FirestoreRetrofitClient.apiService.addProject(firestoreRequest)
                val addedProject = project.copy(documentPath = response.name)
                fetchProjects() // Refresh after adding
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}