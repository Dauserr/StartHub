package com.example.starthub.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.starthub.MainActivity
import com.example.starthub.R
import kotlinx.coroutines.launch

/**
 * ProfileFragment - Displays user profile information
 *
 * Features:
 * - Display user information (name, email, description, phone)
 * - Logout functionality
 * - Loading states
 * - Error handling
 * - Offline support (cached profile from Room)
 *
 * This fragment is one of the main tabs in MainActivity's bottom navigation.
 */
class ProfileFragment : Fragment() {

    // ViewModel
    private lateinit var viewModel: ProfileViewModel

    // Views - User Info
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvUserId: TextView

    // Views - Actions
    private lateinit var btnLogout: Button

    // Views - Loading
    private lateinit var loadingProgressBar: ProgressBar

    // Views - Error
    private lateinit var errorTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel with context
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(requireContext()) as T
            }
        })[ProfileViewModel::class.java]

        // Initialize views
        initViews(view)

        // Setup click listeners
        setupClickListeners()

        // Observe user data
        observeUser()

        // Observe loading state
        observeLoading()

        // Observe logout success
        observeLogout()

        // Fetch user profile on view creation
        viewModel.fetchUserProfile()
    }

    /**
     * Initialize all views
     */
    private fun initViews(view: View) {
        // User information views
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvPhone = view.findViewById(R.id.tvPhone)
        tvUserId = view.findViewById(R.id.tvUserId)

        // Action buttons
        btnLogout = view.findViewById(R.id.btnLogout)

        // Loading and error views (optional - check if they exist in your layout)
        try {
            loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        } catch (e: Exception) {
            // Loading bar not in layout, that's okay
        }

        try {
            errorTextView = view.findViewById(R.id.errorTextView)
        } catch (e: Exception) {
            // Error text not in layout, that's okay
        }
    }

    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        // Logout button
        btnLogout.setOnClickListener {
            handleLogoutClick()
        }
    }

    /**
     * Observe user data from ViewModel
     *
     * Updates UI with user information:
     * - Full name (first name + last name)
     * - Email address
     * - Description/bio
     * - Phone number
     * - User ID
     */
    private fun observeUser() {
        lifecycleScope.launch {
            viewModel.user.collect { user ->
                if (user != null) {
                    // Display user information
                    displayUserInfo(user)
                } else {
                    // No user data available
                    showNoUserData()
                }
            }
        }
    }

    /**
     * Display user information in views
     */
    private fun displayUserInfo(user: com.example.starthub.data.remote.dto.UserProfileDto) {
        // Full name
        val fullName = "${user.first_name} ${user.last_name}".trim()
        tvName.text = if (fullName.isNotEmpty()) fullName else "Имя не указано"

        // Email
        tvEmail.text = if (user.email.isNotEmpty()) {
            "Email: ${user.email}"
        } else {
            "Email: Не указан"
        }

        // Description/Bio
        tvDescription.text = if (!user.description.isNullOrEmpty()) {
            "О себе: ${user.description}"
        } else {
            "О себе: Информация не добавлена"
        }

        // Phone number
        val phoneNumber = user.phone_numbers.firstOrNull()
        tvPhone.text = if (phoneNumber != null && phoneNumber.isNotEmpty()) {
            "Телефон: $phoneNumber"
        } else {
            "Телефон: Не указан"
        }

        // User ID
        tvUserId.text = "ID: ${user.id}"

        // Hide error message if it was visible
        hideError()
    }

    /**
     * Show message when no user data is available
     */
    private fun showNoUserData() {
        tvName.text = "Профиль"
        tvEmail.text = "Загрузка данных..."
        tvDescription.text = ""
        tvPhone.text = ""
        tvUserId.text = ""
    }

    /**
     * Observe loading state from ViewModel
     */
    private fun observeLoading() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (::loadingProgressBar.isInitialized) {
                    loadingProgressBar.visibility = if (isLoading) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }

                // Disable logout button while loading
                btnLogout.isEnabled = !isLoading
            }
        }
    }

    /**
     * Observe logout success from ViewModel
     *
     * When logout is successful:
     * 1. Token is cleared
     * 2. Navigate to LoginActivity via MainActivity
     * 3. Clear back stack
     */
    private fun observeLogout() {
        lifecycleScope.launch {
            viewModel.logoutSuccess.collect { success ->
                if (success) {
                    // Show success message
                    Toast.makeText(
                        requireContext(),
                        "Выход выполнен успешно",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to LoginActivity through MainActivity
                    (requireActivity() as MainActivity).handleLogout()
                }
            }
        }
    }

    /**
     * Handle logout button click
     * Shows confirmation and performs logout
     */
    private fun handleLogoutClick() {
        // Show confirmation dialog (optional)
        showLogoutConfirmation()
    }

    /**
     * Show logout confirmation dialog
     */
    private fun showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти из аккаунта?")
            .setPositiveButton("Выйти") { dialog, _ ->
                // User confirmed logout
                viewModel.logout()
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                // User cancelled
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show error message
     */
    private fun showError(message: String) {
        if (::errorTextView.isInitialized) {
            errorTextView.visibility = View.VISIBLE
            errorTextView.text = message
        } else {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Hide error message
     */
    private fun hideError() {
        if (::errorTextView.isInitialized) {
            errorTextView.visibility = View.GONE
        }
    }

    /**
     * Refresh profile data when fragment becomes visible
     *
     * Optional: Uncomment if you want to refresh profile
     * every time user navigates to profile tab
     */
    /*
    override fun onResume() {
        super.onResume()
        viewModel.fetchUserProfile()
    }
    */
}