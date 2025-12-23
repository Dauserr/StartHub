package com.example.starthub.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.starthub.R
import com.example.starthub.data.local.prefs.TokenManager
import com.example.starthub.viewmodel.AuthViewModel
import com.example.starthub.viewmodel.AuthViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

/**
 * LoginFragment - User login screen
 *
 * Features:
 * - Email and password input with validation
 * - Login button with loading state
 * - Link to registration screen
 * - Forgot password link (placeholder)
 * - Error handling and display
 */
class LoginFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    // Views
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var registrationTextView: android.widget.TextView
    private lateinit var forgotPasswordTextView: android.widget.TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel with TokenManager
        val tokenManager = TokenManager(requireContext())
        val factory = AuthViewModelFactory(tokenManager)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Initialize views
        initViews(view)

        // Setup click listeners
        setupClickListeners()

        // Observe login state
        observeLoginState()
    }

    /**
     * Initialize all views
     */
    private fun initViews(view: View) {
        emailInputLayout = view.findViewById(R.id.emailInputLayout)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)
        registrationTextView = view.findViewById(R.id.registrationTextView)
        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView)
    }

    /**
     * Setup click listeners for all interactive elements
     */
    private fun setupClickListeners() {
        // Login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                // Clear any previous errors
                emailInputLayout.error = null
                passwordInputLayout.error = null

                // Perform login
                viewModel.login(email, password)
            }
        }

        // Registration link click
        registrationTextView.setOnClickListener {
            navigateToRegistration()
        }

        // Forgot password link click
        forgotPasswordTextView.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    /**
     * Validate email and password input
     *
     * @return true if input is valid, false otherwise
     */
    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        // Email validation
        if (email.isEmpty()) {
            emailInputLayout.error = "Введите email"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Неверный формат email"
            isValid = false
        } else {
            emailInputLayout.error = null
        }

        // Password validation
        if (password.isEmpty()) {
            passwordInputLayout.error = "Введите пароль"
            isValid = false
        } else if (password.length < 6) {
            passwordInputLayout.error = "Пароль должен содержать минимум 6 символов"
            isValid = false
        } else {
            passwordInputLayout.error = null
        }

        return isValid
    }

    /**
     * Observe login state from ViewModel
     */
    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is AuthViewModel.LoginState.Idle -> {
                        setLoadingState(false)
                    }
                    is AuthViewModel.LoginState.Loading -> {
                        setLoadingState(true)
                    }
                    is AuthViewModel.LoginState.Success -> {
                        setLoadingState(false)
                        onLoginSuccess()
                    }
                    is AuthViewModel.LoginState.Error -> {
                        setLoadingState(false)
                        showError(state.message)
                    }
                }
            }
        }
    }

    /**
     * Set loading state for UI elements
     *
     * @param isLoading true to show loading state, false to hide
     */
    private fun setLoadingState(isLoading: Boolean) {
        // Disable/enable input fields
        emailEditText.isEnabled = !isLoading
        passwordEditText.isEnabled = !isLoading
        registrationTextView.isEnabled = !isLoading
        forgotPasswordTextView.isEnabled = !isLoading

        // Update button state
        loginButton.isEnabled = !isLoading
        loginButton.text = if (isLoading) {
            "Вход..."
        } else {
            getString(R.string.login_button)
        }
    }

    /**
     * Handle successful login
     * Navigate to MainActivity
     */
    private fun onLoginSuccess() {
        Toast.makeText(
            requireContext(),
            "Успешный вход!",
            Toast.LENGTH_SHORT
        ).show()

        // Navigate to MainActivity through LoginActivity
        (requireActivity() as LoginActivity).navigateToMainActivity()

        // Reset state for next time
        viewModel.resetLoginState()
    }

    /**
     * Show error message to user
     *
     * @param message Error message to display
     */
    private fun showError(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()

        // Optionally, you can also show error in the input layout
        // emailInputLayout.error = message
    }

    /**
     * Navigate to RegistrationFragment
     */
    private fun navigateToRegistration() {
        (requireActivity() as LoginActivity).showRegistration()
    }

    /**
     * Show forgot password dialog
     * This is a placeholder - implement actual forgot password logic
     */
    private fun showForgotPasswordDialog() {
        Toast.makeText(
            requireContext(),
            "Восстановление пароля в разработке",
            Toast.LENGTH_SHORT
        ).show()

        // TODO: Implement forgot password functionality
        // Options:
        // 1. Show dialog to enter email
        // 2. Call API to send password reset email
        // 3. Navigate to password reset screen
    }

    /**
     * Clear input fields when fragment is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Clear sensitive data
        emailEditText.text?.clear()
        passwordEditText.text?.clear()
    }
}