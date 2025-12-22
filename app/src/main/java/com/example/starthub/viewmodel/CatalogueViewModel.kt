package com.example.starthub.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.data.remote.dto.ProjectDto
import kotlinx.coroutines.launch

class CatalogueViewModel : ViewModel() {

    private val _projectsState = MutableLiveData<ProjectsState>()
    val projectsState: LiveData<ProjectsState> = _projectsState

    fun fetchProjects() {
        _projectsState.value = ProjectsState.Loading

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getProjects()

                if (response.isSuccessful) {
                    val projects = response.body() ?: emptyList()
                    _projectsState.value = ProjectsState.Success(projects)
                } else {
                    _projectsState.value = ProjectsState.Error("Failed to load projects")
                }
            } catch (e: Exception) {
                _projectsState.value = ProjectsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class ProjectsState {
        object Loading : ProjectsState()
        data class Success(val projects: List<ProjectDto>) : ProjectsState()
        data class Error(val message: String) : ProjectsState()
    }
}
