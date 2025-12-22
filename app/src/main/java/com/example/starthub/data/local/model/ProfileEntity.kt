package com.example.starthub.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int,
    val first_name: String,
    val last_name: String,
    val description: String?,
    val email: String,
    val picture: String?,
    val phone_numbers: String?,
    val saved_at: Long = System.currentTimeMillis()
)
