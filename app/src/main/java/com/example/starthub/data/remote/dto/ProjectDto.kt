package com.example.starthub.data.remote.dto

data class ProjectDto(
    val id: Int,
    val name: String,
    val slug: String,
    val goal_description: String,
    val description: String,
    val media: List<String>?,
    val categories: List<CategoryDto>?,
    val company: CompanyDto?,
    val user: UserDto?,
    val funding_model: FundingModelDto?,
    val goal_sum: Double,
    val current_sum: Double,
    val deadline: String,
    val stage: String,
    val status: String,
    val is_favorite: Boolean
)

data class CategoryDto(
    val id: Int,
    val name: String,
    val slug: String
)

data class CompanyDto(
    val id: Int,
    val name: String,
    val slug: String,
    val founder: FounderDto?,
    val country_code: String,
    val business_id: String,
    val established_date: String
)

data class FounderDto(
    val name: String,
    val surname: String,
    val description: String
)

data class UserDto(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String
)

data class FundingModelDto(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String,
    val recommended: Boolean
)
