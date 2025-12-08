package com.example.starthub.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.starthub.R
import com.example.starthub.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var errorTextView: TextView

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailEditText = view.findViewById(R.id.email_input)
        passwordEditText = view.findViewById(R.id.password_input)
        loginButton = view.findViewById(R.id.login_button)
        registerLink = view.findViewById(R.id.register_link)
        errorTextView = view.findViewById(R.id.error_text)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginButton.setOnClickListener {
            performLogin()
        }

        registerLink.setOnClickListener {
            Toast.makeText(requireContext(), "Register clicked", Toast.LENGTH_SHORT).show()
        }

        observeLoginState()
    }

    private fun performLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty()) {
            errorTextView.text = "Email cannot be empty"
            return
        }

        if (password.isEmpty()) {
            errorTextView.text = "Password cannot be empty"
            return
        }

        errorTextView.text = ""
        loginViewModel.login(email, password)
    }

    private fun observeLoginState() {
        loginViewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    loginButton.isEnabled = false
                    errorTextView.text = "Logging in..."
                    errorTextView.visibility = View.VISIBLE
                }
                is LoginViewModel.LoginState.Success -> {
                    loginButton.isEnabled = true
                    errorTextView.text = "Login successful!"
                    errorTextView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Welcome!", Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.LoginState.Error -> {
                    loginButton.isEnabled = true
                    errorTextView.text = state.message
                    errorTextView.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }
}
