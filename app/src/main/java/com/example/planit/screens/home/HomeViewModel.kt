package com.example.planit.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planit.model.Project
import com.example.planit.data.FirestoreRetrofitClient
import com.example.planit.model.ArrayValue
import com.example.planit.model.FirestoreField
import com.example.planit.model.FirestoreRequest
import com.example.planit.model.Task
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel : ViewModel() {
    private val _projects = mutableStateOf<List<Project>>(emptyList())
    val projects = _projects

    private fun Project.toFirestoreRequest(): FirestoreRequest {
        return FirestoreRequest(
            fields = mapOf(
                "id" to FirestoreField(stringValue = this.id.toString()),
                "name" to FirestoreField(stringValue = this.name),
                "description" to FirestoreField(stringValue = this.description),
                "startDate" to FirestoreField(stringValue = this.startDate),
                "finalDate" to FirestoreField(stringValue = this.finalDate),
                "tasks" to FirestoreField(
                    arrayValue = ArrayValue(
                        values = this.tasks.map { FirestoreField(stringValue = it.name) }
                    )
                )
            )
        )
    }

    fun fetchProjects() {
        viewModelScope.launch {
            try {
                val response = FirestoreRetrofitClient.apiService.getProjects()
                val projectList = response.documents.mapNotNull { doc ->
                    try {
                        Project(
                            id = UUID.fromString(doc.fields["id"]?.stringValue.orEmpty()),
                            name = doc.fields["name"]?.stringValue.orEmpty().trim(),
                            description = doc.fields["description"]?.stringValue.orEmpty().trim(),
                            startDate = doc.fields["startDate"]?.stringValue.orEmpty().trim(),
                            finalDate = doc.fields["finalDate"]?.stringValue.orEmpty().trim(),
                            tasks = doc.fields["tasks"]?.arrayValue?.values?.map { taskField ->
                                Task(name = taskField.stringValue.orEmpty())
                            } ?: emptyList(),
                            documentPath = doc.name
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

    fun deleteProject(documentPath: String?) {
        if (documentPath.isNullOrEmpty()) {
            println("Invalid document path for deletion.")
            return
        }

        viewModelScope.launch {
            try {
                val response = FirestoreRetrofitClient.apiService.deleteProject(documentPath)
                if (response.isSuccessful) {
                    fetchProjects() // Refresh the project list
                } else {
                    println("Error deleting project: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun updateProject(updatedProject: Project) {
        viewModelScope.launch {
            try {
                FirestoreRetrofitClient.apiService.addProject(updatedProject.toFirestoreRequest())

                fetchProjects()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
