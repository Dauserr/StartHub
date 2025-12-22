package com.example.starthub.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.starthub.data.local.database.ProjectDatabase
import com.example.starthub.data.local.model.ProjectEntity
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.data.remote.dto.ProjectDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

class ProjectRepository(private val context: Context) {
    private val projectDao = ProjectDatabase.getDatabase(context).projectDao()

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun dtoToEntity(dto: ProjectDto): ProjectEntity {
        return ProjectEntity(
            id = dto.id,
            name = dto.name,
            slug = dto.slug,
            description = dto.description,
            goal_description = dto.goal_description,
            goal_sum = dto.goal_sum,
            current_sum = dto.current_sum,
            deadline = dto.deadline,
            stage = dto.stage,
            status = dto.status,
            is_favorite = dto.is_favorite,
            company_name = dto.company?.name,
            user_name = dto.user?.first_name + " " + (dto.user?.last_name ?: "")
        )
    }

    private fun entityToDto(entity: ProjectEntity): ProjectDto {
        return ProjectDto(
            id = entity.id,
            name = entity.name,
            slug = entity.slug,
            description = entity.description,
            goal_description = entity.goal_description,
            media = null,
            categories = null,
            company = null,
            user = null,
            funding_model = null,
            goal_sum = entity.goal_sum,
            current_sum = entity.current_sum,
            deadline = entity.deadline,
            stage = entity.stage,
            status = entity.status,
            is_favorite = entity.is_favorite
        )
    }

    suspend fun getProjects(): Flow<List<ProjectDto>> {
        return try {
            if (isNetworkAvailable()) {
                val response = RetrofitClient.apiService.getProjects()

                if (response.isSuccessful) {
                    val projects = response.body() ?: emptyList()
                    projectDao.clearAllProjects()
                    projectDao.insertProjects(projects.map { dtoToEntity(it) })
                    flowOf(projects)
                } else if (response.code() == 401) {
                    throw HttpException(response)
                } else {
                    projectDao.getAllProjects().map { entities ->
                        entities.map { entityToDto(it) }
                    }
                }
            } else {
                projectDao.getAllProjects().map { entities ->
                    entities.map { entityToDto(it) }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
