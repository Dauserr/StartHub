package com.example.starthub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starthub.data.local.prefs.TokenManager
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.data.remote.dto.LoginRequest
import com.example.starthub.data.remote.dto.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // Login State
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // Registration State
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    /**
     * Perform login with email and password
     */
    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(loginRequest)

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse?.code == "SUCCESS" && loginResponse.access_token != null) {
                        // Save token
                        tokenManager.saveToken(loginResponse.access_token)
                        _loginState.value = LoginState.Success(loginResponse.access_token)
                    } else {
                        _loginState.value = LoginState.Error(
                            loginResponse?.detail ?: "Ошибка входа"
                        )
                    }
                } else {
                    _loginState.value = LoginState.Error("Ошибка сервера: ${response.code()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(
                    e.message ?: "Проверьте подключение к интернету"
                )
            }
        }
    }

    /**
     * Perform registration with email and password
     */
    fun register(email: String, password: String) {
        _registrationState.value = RegistrationState.Loading

        viewModelScope.launch {
            try {
                val registerRequest = RegisterRequest(email, password)
                val response = RetrofitClient.apiService.register(registerRequest)

                if (response.isSuccessful) {
                    val registerResponse = response.body()

                    if (registerResponse?.code == "SUCCESS") {
                        // Registration successful - now login automatically
                        autoLoginAfterRegistration(email, password)
                    } else {
                        _registrationState.value = RegistrationState.Error(
                            registerResponse?.detail ?: "Ошибка регистрации"
                        )
                    }
                } else {
                    _registrationState.value = RegistrationState.Error(
                        "Ошибка сервера: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(
                    e.message ?: "Проверьте подключение к интернету"
                )
            }
        }
    }

    /**
     * Auto-login after successful registration
     */
    private suspend fun autoLoginAfterRegistration(email: String, password: String) {
        try {
            val loginRequest = LoginRequest(email, password)
            val response = RetrofitClient.apiService.login(loginRequest)

            if (response.isSuccessful) {
                val loginResponse = response.body()

                if (loginResponse?.code == "SUCCESS" && loginResponse.access_token != null) {
                    // Save token
                    tokenManager.saveToken(loginResponse.access_token)
                    _registrationState.value = RegistrationState.Success
                } else {
                    _registrationState.value = RegistrationState.Success
                }
            } else {
                _registrationState.value = RegistrationState.Success
            }
        } catch (e: Exception) {
            // Registration was successful, but auto-login failed
            // Still consider it a success
            _registrationState.value = RegistrationState.Success
        }
    }

    /**
     * Reset login state to idle
     */
    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    /**
     * Reset registration state to idle
     */
    fun resetRegistrationState() {
        _registrationState.value = RegistrationState.Idle
    }

    // Login State Sealed Class
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val token: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    // Registration State Sealed Class
    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }
}