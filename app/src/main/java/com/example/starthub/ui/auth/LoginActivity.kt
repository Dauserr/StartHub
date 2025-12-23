package com.example.starthub.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.starthub.MainActivity
import com.example.starthub.R
import com.example.starthub.data.local.prefs.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * LoginActivity - Handles authentication flow
 *
 * This activity serves as a container for authentication fragments:
 * - LoginFragment: For user login
 * - RegistrationFragment: For new user registration
 *
 * This activity is the LAUNCHER activity - it's the first screen users see.
 * If user is already logged in (has valid token), they are redirected to MainActivity.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_container)

        // Initialize TokenManager
        tokenManager = TokenManager(this)

        // Check if user is already logged in
        checkExistingToken()

        // Show LoginFragment if this is first launch (no saved state)
        if (savedInstanceState == null) {
            showLoginFragment()
        }
    }

    /**
     * Check if user already has a valid token
     * If yes, navigate directly to MainActivity
     */
    private fun checkExistingToken() {
        val token = runBlocking { tokenManager.getToken().first() }

        if (token != null) {
            // User is already logged in
            navigateToMainActivity()
        }
    }

    /**
     * Show LoginFragment
     * This is the default fragment shown on app launch
     */
    private fun showLoginFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, LoginFragment())
            .commit()
    }

    /**
     * Navigate to RegistrationFragment
     * Called from LoginFragment when user clicks "Registration" link
     */
    fun showRegistration() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, RegistrationFragment())
            .addToBackStack("registration")
            .commit()
    }

    /**
     * Navigate back to LoginFragment
     * Called from RegistrationFragment when user clicks "Login" link
     */
    fun showLogin() {
        // Pop back stack to return to LoginFragment
        supportFragmentManager.popBackStack()
    }

    /**
     * Navigate to MainActivity after successful login or registration
     *
     * This method is called from:
     * - LoginFragment after successful login
     * - RegistrationFragment after successful registration
     *
     * Uses FLAG_ACTIVITY_NEW_TASK and FLAG_ACTIVITY_CLEAR_TASK to:
     * - Start MainActivity in a new task
     * - Clear all activities in the current task
     * - Prevent user from pressing back to return to LoginActivity
     */
    fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Close LoginActivity
    }

    /**
     * Override back button behavior
     * When user presses back in LoginActivity, exit the app instead of doing nothing
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            // There are fragments in back stack, pop them
            super.onBackPressed()
        } else {
            // No fragments in back stack, exit app
            finishAffinity()
        }
    }
}