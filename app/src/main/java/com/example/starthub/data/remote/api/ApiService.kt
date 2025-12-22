package com.example.starthub.data.remote.api

import com.example.starthub.data.remote.dto.LoginRequest
import com.example.starthub.data.remote.dto.LoginResponse
import com.example.starthub.data.remote.dto.RegisterRequest
import com.example.starthub.data.remote.dto.RegisterResponse
import com.example.starthub.data.remote.dto.ProjectDto
import com.example.starthub.data.remote.dto.UserProfileDto

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {//retrofit uses interface

    // Authentication endpoints
    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    // Projects endpoints
    @GET("projects/")
    suspend fun getProjects(
        @Query("page_number") pageNumber: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<List<ProjectDto>>

    @GET("projects/{id}/")
    suspend fun getProject(@Path("id") projectId: Int): Response<ProjectDto>

    @GET("users/me/")
    suspend fun getUserProfile(): UserProfileDto
}