package com.example.planit.model

import java.util.Date
import java.util.UUID

data class Task(
    val name: String,
    val isDone: Boolean = false
)

data class Project(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val startDate: String,
    val finalDate: String,
    val tasks: List<Task> = emptyList(),
    val documentPath: String? = null
)

data class FirestoreRequest(
    val fields: Map<String, FirestoreField>
)

data class FirestoreField(
    val stringValue: String? = null,
    val arrayValue: ArrayValue? = null
)

data class ArrayValue(
    val values: List<FirestoreField>? = null
)

data class FirestoreResponse(
    val documents: List<Document>,
    val name: String
)

data class Document(
    val name: String,
    val fields: Map<String, FirestoreField>
)
