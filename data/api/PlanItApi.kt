package com.example.planit.network

import com.example.planit.model.Project
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PlanItApi {

    @GET("/projects")
    suspend fun getProjects(): List<Project>

    @GET("/projects/{name}")
    suspend fun getProjectByName(@Path("name") name: String): Project

    @POST("/projects")
    suspend fun addProject(@Body project: Project)

    @PUT("/projects/{name}")
    suspend fun updateProjectByName(@Path("name") name: String, @Body project: Project)

    @DELETE("/projects/{name}")
    suspend fun deleteProjectByName(@Path("name") name: String)
}