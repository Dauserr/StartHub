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
 * RegistrationFragment - New user registration screen
 *
 * Features:
 * - Email and password input with validation
 * - Password confirmation with matching validation
 * - Registration button with loading state
 * - Link to login screen
 * - Error handling and display
 * - Auto-login after successful registration
 */
class RegistrationFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    // Views
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var createAccountButton: MaterialButton
    private lateinit var loginTextView: android.widget.TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_registration, container, false)
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

        // Observe registration state
        observeRegistrationState()
    }

    /**
     * Initialize all views
     */
    private fun initViews(view: View) {
        emailInputLayout = view.findViewById(R.id.emailInputLayout)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        confirmPasswordInputLayout = view.findViewById(R.id.confirmPasswordInputLayout)
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText)
        createAccountButton = view.findViewById(R.id.createAccountButton)
        loginTextView = view.findViewById(R.id.loginTextView)
    }

    /**
     * Setup click listeners for all interactive elements
     */
    private fun setupClickListeners() {
        // Create account button click
        createAccountButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (validateInput(email, password, confirmPassword)) {
                // Clear any previous errors
                emailInputLayout.error = null
                passwordInputLayout.error = null
                confirmPasswordInputLayout.error = null

                // Perform registration
                viewModel.register(email, password)
            }
        }

        // Login link click
        loginTextView.setOnClickListener {
            navigateToLogin()
        }
    }

    /**
     * Validate email, password, and password confirmation input
     *
     * @return true if all input is valid, false otherwise
     */
    private fun validateInput(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
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

        // Confirm password validation
        if (confirmPassword.isEmpty()) {
            confirmPasswordInputLayout.error = "Подтвердите пароль"
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordInputLayout.error = "Пароли не совпадают"
            isValid = false
        } else {
            confirmPasswordInputLayout.error = null
        }

        return isValid
    }

    /**
     * Observe registration state from ViewModel
     */
    private fun observeRegistrationState() {
        lifecycleScope.launch {
            viewModel.registrationState.collect { state ->
                when (state) {
                    is AuthViewModel.RegistrationState.Idle -> {
                        setLoadingState(false)
                    }
                    is AuthViewModel.RegistrationState.Loading -> {
                        setLoadingState(true)
                    }
                    is AuthViewModel.RegistrationState.Success -> {
                        setLoadingState(false)
                        onRegistrationSuccess()
                    }
                    is AuthViewModel.RegistrationState.Error -> {
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
        confirmPasswordEditText.isEnabled = !isLoading
        loginTextView.isEnabled = !isLoading

        // Update button state
        createAccountButton.isEnabled = !isLoading
        createAccountButton.text = if (isLoading) {
            "Создание аккаунта..."
        } else {
            getString(R.string.create_account_button)
        }
    }

    /**
     * Handle successful registration
     * Navigate to MainActivity (user is auto-logged in after registration)
     */
    private fun onRegistrationSuccess() {
        Toast.makeText(
            requireContext(),
            "Регистрация успешна!",
            Toast.LENGTH_SHORT
        ).show()

        // Navigate to MainActivity through LoginActivity
        // User is automatically logged in after successful registration
        (requireActivity() as LoginActivity).navigateToMainActivity()

        // Reset state for next time
        viewModel.resetRegistrationState()
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
        // For email already exists error, show it on email field
        if (message.contains("email", ignoreCase = true) ||
            message.contains("существует", ignoreCase = true)) {
            emailInputLayout.error = message
        }
    }

    /**
     * Navigate back to LoginFragment
     */
    private fun navigateToLogin() {
        (requireActivity() as LoginActivity).showLogin()
    }

    /**
     * Clear input fields when fragment is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Clear sensitive data
        emailEditText.text?.clear()
        passwordEditText.text?.clear()
        confirmPasswordEditText.text?.clear()
    }
}