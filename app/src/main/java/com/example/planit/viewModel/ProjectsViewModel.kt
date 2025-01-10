package com.example.planit.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.planit.model.Project

class ProjectsViewModel: ViewModel() {
    private val _projects = mutableStateOf<List<Project>>(emptyList())
    val projects: State<List<Project>> = _projects

    fun addProject(project: Project) {
        _projects.value = _projects.value + project
    }

    fun setProjects(updatedList: List<Project>) {
        _projects.value = updatedList
    }
}