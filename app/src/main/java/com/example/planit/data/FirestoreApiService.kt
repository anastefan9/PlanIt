package com.example.planit.data

import com.example.planit.model.FirestoreRequest
import com.example.planit.model.FirestoreResponse
import retrofit2.Response
import retrofit2.http.*

interface FirestoreApiService {
    @GET("documents/projects")
    suspend fun getProjects(): FirestoreResponse

    @POST("documents/projects")
    suspend fun addProject(@Body project: FirestoreRequest): FirestoreResponse

    @DELETE("{documentPath}")
    suspend fun deleteProject(@Path("documentPath", encoded = true) documentPath: String): Response<Unit>

    @PUT("{documentPath}")
    suspend fun updateProject(
        @Path(value = "documentPath", encoded = true) documentPath: String,
        @Body project: FirestoreRequest
    ): Response<Unit>
}
