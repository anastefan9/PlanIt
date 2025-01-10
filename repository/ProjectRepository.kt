package com.example.planit.repository

import com.example.planit.model.Project
import com.example.planit.network.PlanItApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProjectRepository {

    private val api: PlanItApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(PlanItApi::class.java)
    }

    suspend fun fetchProjects(): List<Project> {
        return api.getProjects()
    }

    suspend fun fetchProjectByName(projectName: String): Project {
        return api.getProjectByName(projectName)
    }

    suspend fun addProject(newProject: Project) {
        api.addProject(newProject)
    }

    suspend fun updateProjectByName(projectName: String, updatedProject: Project) {
        api.updateProjectByName(projectName, updatedProject)
    }

    suspend fun deleteProjectByName(projectName: String) {
        api.deleteProjectByName(projectName)
    }
}
