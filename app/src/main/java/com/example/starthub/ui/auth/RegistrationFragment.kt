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
import com.example.starthub.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

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

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        initViews(view)
        setupClickListeners()
        observeRegistrationState()
    }

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

    private fun setupClickListeners() {
        createAccountButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (validateInput(email, password, confirmPassword)) {
                viewModel.register(email, password)
            }
        }

        loginTextView.setOnClickListener {
            // Go back to LoginFragment
            (requireActivity() as LoginActivity).showLogin()
        }
    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
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

    private fun setLoadingState(isLoading: Boolean) {
        createAccountButton.isEnabled = !isLoading
        emailEditText.isEnabled = !isLoading
        passwordEditText.isEnabled = !isLoading
        confirmPasswordEditText.isEnabled = !isLoading
        loginTextView.isEnabled = !isLoading

        createAccountButton.text = if (isLoading) "Создание аккаунта..." else getString(R.string.create_account_button)
    }

    private fun onRegistrationSuccess() {
        Toast.makeText(
            requireContext(),
            "Регистрация успешна!",
            Toast.LENGTH_SHORT
        ).show()

        // Navigate to MainActivity after successful registration
        (requireActivity() as LoginActivity).navigateToMainActivity()
    }

    private fun showError(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}