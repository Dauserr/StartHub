package com.example.starthub

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.starthub.data.local.prefs.TokenManager
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.ui.auth.LoginActivity
import com.example.starthub.ui.catalogue.CatalogueFragment
import com.example.starthub.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * MainActivity - Main application screen for authenticated users
 *
 * This activity is only accessible after successful login.
 * It manages the bottom navigation and fragment switching between:
 * - Catalogue (project listing)
 * - Profile (user profile)
 *
 * If user is not authenticated, they are redirected to LoginActivity.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TokenManager
        tokenManager = TokenManager(this)

        // Setup Retrofit with context and token providers
        RetrofitClient.setContextProvider { this }
        RetrofitClient.setTokenProvider {
            runBlocking { tokenManager.getToken().first() }
        }

        // Check if user is authenticated
        val token = runBlocking { tokenManager.getToken().first() }

        if (token == null) {
            // No token found - user is not logged in
            // Redirect to LoginActivity
            navigateToLoginActivity()
            return
        }

        // User is authenticated - setup main UI
        setupUI(savedInstanceState)
    }

    /**
     * Setup the main UI components
     * Only called if user is authenticated
     */
    private fun setupUI(savedInstanceState: Bundle?) {
        // Initialize bottom navigation
        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.visibility = View.VISIBLE // Always visible in MainActivity

        // Setup bottom navigation listener
        setupBottomNavigation()

        // Show default fragment (only if not restoring state)
        if (savedInstanceState == null) {
            showCatalogue()
            // Set catalogue as selected in bottom nav
            bottomNav.selectedItemId = R.id.nav_catalogue
        }
    }

    /**
     * Setup bottom navigation item selection listener
     */
    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_catalogue -> {
                    showCatalogue()
                    true
                }
                R.id.nav_profile -> {
                    showProfile()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Show CatalogueFragment
     * Displays list of all projects
     */
    private fun showCatalogue() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CatalogueFragment())
            .commit()
    }

    /**
     * Show ProfileFragment
     * Displays user profile and settings
     */
    private fun showProfile() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ProfileFragment())
            .commit()
    }

    /**
     * Navigate to LoginActivity and clear the back stack
     *
     * This is called when:
     * - User is not authenticated (no token)
     * - Token has expired
     * - User logs out
     *
     * Using FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK ensures
     * the user cannot press back to return to MainActivity
     */
    fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Close MainActivity
    }

    /**
     * Handle user logout
     *
     * Called from ProfileFragment when user clicks logout button.
     * Clears the authentication token and redirects to LoginActivity.
     */
    fun handleLogout() {
        // Clear the authentication token
        runBlocking {
            tokenManager.clearToken()
        }

        // Navigate to LoginActivity
        navigateToLoginActivity()
    }

    /**
     * Optional: Handle token expiration globally
     * You can call this from your API error handler
     */
    fun handleTokenExpired() {
        // Clear the expired token
        runBlocking {
            tokenManager.clearToken()
        }

        // Show a message to the user (optional)
        // Toast.makeText(this, "Сеанс истек. Пожалуйста, войдите снова.", Toast.LENGTH_SHORT).show()

        // Navigate to LoginActivity
        navigateToLoginActivity()
    }

    /**
     * Override onBackPressed to prevent going back to LoginActivity
     * When user presses back in MainActivity, minimize the app instead
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Move app to background instead of going back to LoginActivity
        moveTaskToBack(true)
    }
}