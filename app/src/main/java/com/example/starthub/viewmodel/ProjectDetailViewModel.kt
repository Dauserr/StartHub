package com.example.starthub.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.data.remote.dto.ProjectDto
import kotlinx.coroutines.launch

/**
 * ProjectDetailViewModel - Handles fetching individual project details
 *
 * Features:
 * - Fetch project by ID from API
 * - Loading state management
 * - Error handling
 *
 * States:
 * - Loading: Fetching project data
 * - Success: Project data loaded
 * - Error: Failed to load project
 */
class ProjectDetailViewModel : ViewModel() {

    private val _projectState = MutableLiveData<ProjectState>()
    val projectState: LiveData<ProjectState> = _projectState

    /**
     * Fetch project details by ID
     *
     * @param projectId ID of the project to fetch
     */
    fun fetchProjectDetail(projectId: Int) {
        _projectState.value = ProjectState.Loading

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getProject(projectId)

                if (response.isSuccessful) {
                    val project = response.body()
                    if (project != null) {
                        _projectState.value = ProjectState.Success(project)
                    } else {
                        _projectState.value = ProjectState.Error("Проект не найден")
                    }
                } else {
                    _projectState.value = ProjectState.Error("Ошибка загрузки: ${response.code()}")
                }
            } catch (e: Exception) {
                _projectState.value = ProjectState.Error(
                    e.message ?: "Проверьте подключение к интернету"
                )
            }
        }
    }

    /**
     * Sealed class representing project loading states
     */
    sealed class ProjectState {
        object Loading : ProjectState()
        data class Success(val project: ProjectDto) : ProjectState()
        data class Error(val message: String) : ProjectState()
    }
}