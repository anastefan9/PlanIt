package com.example.planit.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planit.model.Project
import com.example.planit.network.PlanItApi
import com.example.planit.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ProjectRepository = ProjectRepository()) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchProjects()
    }

    fun fetchProjects() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val fetchedProjects = repository.fetchProjects()
                _projects.value = fetchedProjects
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load projects: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchProjectByName(projectName: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val fetchedProject = repository.fetchProjectByName(projectName)
                _projects.value = _projects.value.map {
                    if (it.name == projectName) fetchedProject else it
                }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load project: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProject(newProject: Project) {
        viewModelScope.launch {
            try {
                repository.addProject(newProject)
                _projects.value = _projects.value + newProject
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add project: ${e.message}"
            }
        }
    }

    fun updateProjectByName(projectName: String, updatedProject: Project) {
        viewModelScope.launch {
            try {
                repository.updateProjectByName(projectName, updatedProject)
                _projects.value = _projects.value.map {
                    if (it.name == projectName) updatedProject else it
                }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update project: ${e.message}"
            }
        }
    }

    fun deleteProjectByName(projectName: String) {
        viewModelScope.launch {
            try {
                repository.deleteProjectByName(projectName)
                _projects.value = _projects.value.filter { it.name != projectName }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete project: ${e.message}"
            }
        }
    }
}