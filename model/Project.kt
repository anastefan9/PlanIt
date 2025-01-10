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
    val tasks: List<Task> = emptyList()
)
