package com.example.starthub.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.data.remote.dto.ProjectDto
import com.example.starthub.data.repository.ProjectRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException


class CatalogueViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProjectRepository(application)
    private val _projectsState = MutableLiveData<ProjectsState>()
    val projectsState: LiveData<ProjectsState> = _projectsState

    fun fetchProjects() {
        _projectsState.value = ProjectsState.Loading

        viewModelScope.launch {
            try {
                repository.getProjects().collect { projects ->
                    if (projects.isNotEmpty()) {
                        _projectsState.value = ProjectsState.Success(projects)
                    } else {
                        _projectsState.value = ProjectsState.Error("No projects found")
                    }
                }
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _projectsState.value = ProjectsState.TokenExpired
                } else {
                    _projectsState.value = ProjectsState.Error(e.message ?: "Unknown error")
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
        object TokenExpired : ProjectsState()
    }
}
