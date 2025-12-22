package com.example.starthub.data.remote.dto

data class UserProfileDto(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val description: String?,
    val email: String,
    val picture: String?,
    val phone_numbers: List<String>
)
