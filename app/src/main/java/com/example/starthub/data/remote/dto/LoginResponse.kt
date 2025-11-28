package com.example.starthub.data.remote.dto

data class LoginResponse(
    val access_token: String? = null,
    val code: String,
    val detail: String? = null
)