package com.example.planit.data

import com.example.planit.model.Project
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

//    private val db = FirebaseFirestore.getInstance()
//
//
//    suspend fun getProjects(): List<Project> {
//        return try {
//            db.collection("projects")
//                .get()
//                .await()
//                .documents.mapNotNull { it.toObject(Project::class.java) }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
//
//    suspend fun addProject(project: Project) {
//        try {
//            db.collection("projects")
//                .document(project.id.toString())
//                .set(project)
//                .await()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//
//    suspend fun deleteProject(projectId: String) {
//        try {
//            db.collection("projects")
//                .document(projectId)
//                .delete()
//                .await()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
}
