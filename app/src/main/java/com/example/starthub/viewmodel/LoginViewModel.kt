package com.example.starthub.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.data.remote.dto.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(loginRequest)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.code == "SUCCESS" && loginResponse.access_token != null) {
                        _loginState.value = LoginState.Success(loginResponse.access_token)
                    } else {
                        _loginState.value = LoginState.Error(
                            loginResponse?.detail ?: "Unknown error"
                        )
                    }
                } else {
                    _loginState.value = LoginState.Error("Login failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val token: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
