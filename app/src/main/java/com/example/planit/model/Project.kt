package com.example.planit.model

import java.util.Date

data class Project(
    val id: Int,
    val name: String,
    val description: String,
    val startDate: Date,
    val finalDate: Date
)
