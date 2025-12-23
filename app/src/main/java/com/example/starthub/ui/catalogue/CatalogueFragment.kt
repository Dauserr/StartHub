package com.example.starthub.ui.catalogue

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.starthub.MainActivity
import com.example.starthub.R
import com.example.starthub.ui.project.ProjectDetailActivity
import com.example.starthub.viewmodel.CatalogueViewModel

/**
 * CatalogueFragment - Displays list of all projects
 *
 * Features:
 * - Fetches projects from API
 * - Displays projects in RecyclerView
 * - Shows loading state
 * - Shows error messages
 * - Handles token expiration
 * - Offline support via Room database
 *
 * This fragment is one of the main tabs in MainActivity's bottom navigation.
 */
class CatalogueFragment : Fragment() {

    // Views
    private lateinit var projectsRecycler: RecyclerView
    private lateinit var loadingBar: ProgressBar
    private lateinit var errorText: TextView

    // ViewModel
    private lateinit var viewModel: CatalogueViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catalogue, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        initViews(view)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[CatalogueViewModel::class.java]

        // Setup RecyclerView
        setupRecyclerView()

        // Observe projects state
        observeProjects()

        // Fetch projects from API
        viewModel.fetchProjects()
    }

    /**
     * Initialize all views
     */
    private fun initViews(view: View) {
        projectsRecycler = view.findViewById(R.id.projects_recycler)
        loadingBar = view.findViewById(R.id.loading_bar)
        errorText = view.findViewById(R.id.error_text)
    }

    /**
     * Setup RecyclerView with LinearLayoutManager
     */
    private fun setupRecyclerView() {
        projectsRecycler.layoutManager = LinearLayoutManager(requireContext())
        // Adapter will be set when data is loaded
    }

    /**
     * Observe projects state from ViewModel
     *
     * Handles 4 states:
     * - Loading: Show progress bar
     * - Success: Show projects in RecyclerView
     * - Error: Show error message
     * - TokenExpired: Navigate to LoginActivity
     */
    private fun observeProjects() {
        viewModel.projectsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CatalogueViewModel.ProjectsState.Loading -> {
                    showLoading()
                }
                is CatalogueViewModel.ProjectsState.Success -> {
                    showProjects(state)
                }
                is CatalogueViewModel.ProjectsState.Error -> {
                    showError(state)
                }
                is CatalogueViewModel.ProjectsState.TokenExpired -> {
                    handleTokenExpired()
                }
            }
        }
    }

    /**
     * Show loading state
     * - Display progress bar
     * - Hide RecyclerView and error message
     */
    private fun showLoading() {
        loadingBar.visibility = View.VISIBLE
        projectsRecycler.visibility = View.GONE
        errorText.visibility = View.GONE
    }

    /**
     * Show projects in RecyclerView
     * - Hide progress bar and error message
     * - Display RecyclerView with projects
     * - Set adapter with project data
     *
     * @param state Success state containing list of projects
     */
    private fun showProjects(state: CatalogueViewModel.ProjectsState.Success) {
        loadingBar.visibility = View.GONE
        projectsRecycler.visibility = View.VISIBLE
        errorText.visibility = View.GONE

        // Create and set adapter
        val adapter = ProjectAdapter(state.projects)
        projectsRecycler.adapter = adapter
    }

    private fun navigateToProjectDetail(projectId: Int) {
        val intent = Intent(requireContext(), ProjectDetailActivity::class.java)
        intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT_ID, projectId)
        startActivity(intent)
    }

    /**
     * Show error message
     * - Hide progress bar and RecyclerView
     * - Display error message
     *
     * @param state Error state containing error message
     */
    private fun showError(state: CatalogueViewModel.ProjectsState.Error) {
        loadingBar.visibility = View.GONE
        projectsRecycler.visibility = View.GONE
        errorText.visibility = View.VISIBLE
        errorText.text = state.message
    }

    /**
     * Handle token expiration
     *
     * Called when API returns 401 Unauthorized.
     * This means the user's token has expired or is invalid.
     *
     * Delegates to MainActivity which will:
     * 1. Clear the expired token
     * 2. Navigate to LoginActivity
     * 3. Clear the back stack
     */
    private fun handleTokenExpired() {
        (requireActivity() as MainActivity).navigateToLoginActivity()
    }

    /**
     * Refresh projects when fragment becomes visible again
     *
     * Optional: Uncomment if you want to refresh projects
     * every time user navigates back to catalogue tab
     */
    /*
    override fun onResume() {
        super.onResume()
        // Refresh projects
        viewModel.fetchProjects()
    }
    */
}