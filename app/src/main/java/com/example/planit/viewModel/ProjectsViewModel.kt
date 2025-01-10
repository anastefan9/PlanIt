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
//
//    fun addProject(project: Project) {
//        _projects.value = _projects.value + project
//    }
//
//    fun setProjects(updatedList: List<Project>) {
//        _projects.value = updatedList
//    }

    fun fetchProjects() {
        viewModelScope.launch {
            try {
                val response = FirestoreRetrofitClient.apiService.getProjects()

                val projectList = response.documents.mapNotNull { doc ->
                    try {
                        // Safely extract the id field, fallback to a random UUID if missing or invalid
                        val idString = doc.fields["id"]?.stringValue.orEmpty()
                        val projectId = if (idString.isNotBlank()) UUID.fromString(idString) else UUID.randomUUID()

                        // Map Firestore document fields to the Project class
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
                        null // Skip malformed documents
                    }
                }

                // Update the mutable state with the fetched project list
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
                val addedProject = project.copy(documentPath = response.name) // Get the document path from the response
                fetchProjects() // Refresh after adding
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

//    fun deleteProject(documentPath: String) {
//        viewModelScope.launch {
//            try {
//                FirestoreRetrofitClient.apiService.deleteProject(documentPath)
//                fetchProjects() // Refresh the project list
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
}