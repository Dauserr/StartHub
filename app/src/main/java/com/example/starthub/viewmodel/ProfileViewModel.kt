package com.example.starthub.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starthub.data.local.prefs.TokenManager
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.data.remote.dto.UserProfileDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val context: Context) : ViewModel() {
    private val _user = MutableStateFlow<UserProfileDto?>(null)
    val user: StateFlow<UserProfileDto?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getUserProfile()
                _user.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            TokenManager(context).clearAll()
            // TODO: Navigate to LoginFragment here
        }
    }
}
