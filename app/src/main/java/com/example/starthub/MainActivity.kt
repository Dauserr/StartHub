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
 * Features:
 * - Token validation on startup
 * - Bottom navigation management
 * - Fragment switching
 * - Logout handling
 * - Token expiration handling
 * - Proper back button behavior
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
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state
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
     *
     * Handles navigation between:
     * - Catalogue (project listing)
     * - Profile (user profile)
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
     * - Token has expired (401 error from API)
     * - User logs out from ProfileFragment
     *
     * Using FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK ensures:
     * - Start LoginActivity in a new task
     * - Clear all activities in the current task
     * - User cannot press back to return to MainActivity
     *
     * Called from:
     * - onCreate() - if no token found
     * - CatalogueFragment - when token expires
     * - ProfileFragment - when user logs out
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
     *
     * Process:
     * 1. Clear the authentication token from TokenManager
     * 2. Navigate to LoginActivity
     * 3. Clear the activity back stack
     *
     * This ensures the user must log in again to access the app.
     */
    fun handleLogout() {
        // Clear the authentication token
        runBlocking {
            tokenManager.clearAll()
        }

        // Navigate to LoginActivity
        navigateToLoginActivity()
    }

    /**
     * Handle token expiration globally
     *
     * Called from fragments when API returns 401 Unauthorized.
     * This indicates the token has expired or is invalid.
     *
     * Process:
     * 1. Clear the expired token
     * 2. Navigate to LoginActivity
     *
     * Note: Toast message is optional - you can uncomment it if desired
     */
    fun handleTokenExpired() {
        // Clear the expired token
        runBlocking {
            tokenManager.clearAll()
        }

        // Optional: Show a message to the user
        // Toast.makeText(this, "Сеанс истек. Пожалуйста, войдите снова.", Toast.LENGTH_SHORT).show()

        // Navigate to LoginActivity
        navigateToLoginActivity()
    }

    /**
     * Override back button behavior
     *
     * When user presses back in MainActivity:
     * - Move app to background (minimize)
     * - Don't close the app
     * - Don't navigate back to LoginActivity
     *
     * This provides better UX - user can resume the app later
     * without having to log in again.
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Move app to background instead of closing
        moveTaskToBack(true)
    }

    /**
     * Handle activity resume
     *
     * Optional: You can add token validation here to check if token
     * is still valid when user returns to the app after some time.
     *
     * Uncomment and implement if needed for extra security.
     */
    /*
    override fun onResume() {
        super.onResume()

        // Optional: Validate token on resume
        val token = runBlocking { tokenManager.getToken().first() }
        if (token == null) {
            navigateToLoginActivity()
        }
    }
    */
}