package com.example.starthub.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val slug: String,
    val description: String,
    val goal_description: String,
    val goal_sum: Double,
    val current_sum: Double,
    val deadline: String,
    val stage: String,
    val status: String,
    val is_favorite: Boolean,
    val company_name: String? = null,
    val user_name: String? = null
)
