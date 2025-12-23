package com.example.starthub.ui.project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.starthub.R
import com.example.starthub.data.remote.dto.ProjectDto
import com.example.starthub.viewmodel.ProjectDetailViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

/**
 * ProjectDetailActivity - Displays detailed information about a project
 *
 * Features:
 * - Project title, description, and images
 * - Project goals and tasks
 * - Team members list
 * - Crowdfunding information with progress bar
 * - Social media links
 * - Share functionality
 * - Back navigation
 *
 * Receives project ID via intent extras
 */
class ProjectDetailActivity : AppCompatActivity() {

    // ViewModel
    private lateinit var viewModel: ProjectDetailViewModel

    // Header Views
    private lateinit var backButton: ImageButton
    private lateinit var shareButton: ImageButton
    private lateinit var headerTitleTextView: TextView

    // Project Image
    private lateinit var projectImageView: ImageView

    // Project Info Views
    private lateinit var projectTitleTextView: TextView
    private lateinit var authorNameTextView: TextView
    private lateinit var categoryChip: Chip
    private lateinit var projectDescriptionTextView: TextView
    private lateinit var goalsTextView: TextView

    // Team Members
    private lateinit var teamMembersRecyclerView: RecyclerView

    // Crowdfunding Views
    private lateinit var crowdfundingLayout: LinearLayout
    private lateinit var crowdfundingToggle: ImageView
    private lateinit var fundingInfoLayout: LinearLayout
    private lateinit var fundingModelTextView: TextView
    private lateinit var progressPercentageTextView: TextView
    private lateinit var fundingProgressBar: ProgressBar
    private lateinit var collectedAmountTextView: TextView
    private lateinit var goalAmountTextView: TextView
    private lateinit var supportProjectButton: MaterialButton

    // Social Links
    private lateinit var socialLinksLayout: LinearLayout
    private lateinit var facebookButton: ImageButton
    private lateinit var instagramButton: ImageButton
    private lateinit var linkedinButton: ImageButton

    // Loading
    private lateinit var loadingProgressBar: ProgressBar

    // Current project
    private var currentProject: ProjectDto? = null

    companion object {
        const val EXTRA_PROJECT_ID = "project_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ProjectDetailViewModel::class.java]

        // Initialize views
        initViews()

        // Setup click listeners
        setupClickListeners()

        // Observe project data
        observeProject()

        // Get project ID from intent and fetch project
        val projectId = intent.getIntExtra(EXTRA_PROJECT_ID, -1)
        if (projectId != -1) {
            viewModel.fetchProjectDetail(projectId)
        } else {
            showError("Invalid project ID")
            finish()
        }
    }

    /**
     * Initialize all views
     */
    private fun initViews() {
        // Header
        backButton = findViewById(R.id.backButton)
        shareButton = findViewById(R.id.shareButton)
        headerTitleTextView = findViewById(R.id.headerTitleTextView)

        // Project Image
        projectImageView = findViewById(R.id.projectImageView)

        // Project Info
        projectTitleTextView = findViewById(R.id.projectTitleTextView)
        authorNameTextView = findViewById(R.id.authorNameTextView)
        categoryChip = findViewById(R.id.categoryChip)
        projectDescriptionTextView = findViewById(R.id.projectDescriptionTextView)
        goalsTextView = findViewById(R.id.goalsTextView)

        // Team Members
        teamMembersRecyclerView = findViewById(R.id.teamMembersRecyclerView)
        teamMembersRecyclerView.layoutManager = LinearLayoutManager(this)

        // Crowdfunding
        crowdfundingLayout = findViewById(R.id.crowdfundingLayout)
        crowdfundingToggle = findViewById(R.id.crowdfundingToggle)
        fundingInfoLayout = findViewById(R.id.fundingInfoLayout)
        fundingModelTextView = findViewById(R.id.fundingModelTextView)
        progressPercentageTextView = findViewById(R.id.progressPercentageTextView)
        fundingProgressBar = findViewById(R.id.fundingProgressBar)
        collectedAmountTextView = findViewById(R.id.collectedAmountTextView)
        goalAmountTextView = findViewById(R.id.goalAmountTextView)
        supportProjectButton = findViewById(R.id.supportProjectButton)

        // Social Links
        socialLinksLayout = findViewById(R.id.socialLinksLayout)
        facebookButton = findViewById(R.id.facebookButton)
        instagramButton = findViewById(R.id.instagramButton)
        linkedinButton = findViewById(R.id.linkedinButton)

        // Loading
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
    }

    /**
     * Setup all click listeners
     */
    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Share button
        shareButton.setOnClickListener {
            shareProject()
        }

        // Crowdfunding toggle (expand/collapse)
        crowdfundingLayout.setOnClickListener {
            toggleCrowdfundingInfo()
        }

        // Support project button
        supportProjectButton.setOnClickListener {
            supportProject()
        }

        // Social media buttons
        facebookButton.setOnClickListener {
            openSocialLink("facebook")
        }

        instagramButton.setOnClickListener {
            openSocialLink("instagram")
        }

        linkedinButton.setOnClickListener {
            openSocialLink("linkedin")
        }
    }

    /**
     * Observe project data from ViewModel
     */
    private fun observeProject() {
        viewModel.projectState.observe(this) { state ->
            when (state) {
                is ProjectDetailViewModel.ProjectState.Loading -> {
                    showLoading()
                }
                is ProjectDetailViewModel.ProjectState.Success -> {
                    hideLoading()
                    displayProject(state.project)
                }
                is ProjectDetailViewModel.ProjectState.Error -> {
                    hideLoading()
                    showError(state.message)
                }
            }
        }
    }

    /**
     * Display project data in views
     */
    private fun displayProject(project: ProjectDto) {
        currentProject = project

        // Header title
        headerTitleTextView.text = project.name

        // Project title and author
        projectTitleTextView.text = project.name
        authorNameTextView.text = project.user?.let {
            "${it.first_name} ${it.last_name}"
        } ?: "Unknown Author"

        // Category
        categoryChip.text = project.categories?.firstOrNull()?.name ?: "General"

        // Description
        projectDescriptionTextView.text = project.description

        // Goals and tasks
        goalsTextView.text = project.goal_description

        // Team members (if available)
        // Note: Your backend might not have team members in ProjectDto
        // This is a placeholder - you'll need to add team members to your API
        displayTeamMembers(emptyList())

        // Crowdfunding info
        displayCrowdfunding(project)

        // Project image (placeholder - you'll need image loading library like Glide or Coil)
        // Glide.with(this).load(project.media?.firstOrNull()).into(projectImageView)
    }

    /**
     * Display team members in RecyclerView
     */
    private fun displayTeamMembers(teamMembers: List<TeamMember>) {
        if (teamMembers.isEmpty()) {
            teamMembersRecyclerView.visibility = View.GONE
            // You can show a "No team members" message if needed
        } else {
            teamMembersRecyclerView.visibility = View.VISIBLE
            val adapter = TeamMemberAdapter(teamMembers)
            teamMembersRecyclerView.adapter = adapter
        }
    }

    /**
     * Display crowdfunding information
     */
    private fun displayCrowdfunding(project: ProjectDto) {
        // Funding model
        val fundingModel = project.funding_model?.name ?: "Not specified"
        fundingModelTextView.text = "Модель финансирования:\n$fundingModel"

        // Calculate progress percentage
        val percentage = if (project.goal_sum > 0) {
            ((project.current_sum / project.goal_sum) * 100).toInt()
        } else {
            0
        }

        // Display progress
        progressPercentageTextView.text = "$percentage%"
        fundingProgressBar.progress = percentage

        // Display amounts
        collectedAmountTextView.text = "₸${formatAmount(project.current_sum)}"
        goalAmountTextView.text = "₸${formatAmount(project.goal_sum)}"
    }

    /**
     * Format amount with thousand separators
     */
    private fun formatAmount(amount: Double): String {
        return String.format("%,.0f", amount).replace(",", " ")
    }

    /**
     * Toggle crowdfunding info visibility
     */
    private fun toggleCrowdfundingInfo() {
        if (fundingInfoLayout.visibility == View.VISIBLE) {
            fundingInfoLayout.visibility = View.GONE
            crowdfundingToggle.rotation = 0f
        } else {
            fundingInfoLayout.visibility = View.VISIBLE
            crowdfundingToggle.rotation = 180f
        }
    }

    /**
     * Share project
     */
    private fun shareProject() {
        currentProject?.let { project ->
            val shareText = """
                Посмотрите на этот проект: ${project.name}
                
                ${project.description}
                
                https://starthub.kz/projects/${project.slug}
            """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, project.name)
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            startActivity(Intent.createChooser(shareIntent, "Поделиться проектом"))
        }
    }

    /**
     * Support project (open payment or external link)
     */
    private fun supportProject() {
        currentProject?.let { project ->
            // TODO: Implement payment integration
            // For now, show a message
            Toast.makeText(
                this,
                "Функция поддержки проекта в разработке",
                Toast.LENGTH_SHORT
            ).show()

            // Example: Open project website
            // val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://starthub.kz/projects/${project.slug}"))
            // startActivity(intent)
        }
    }

    /**
     * Open social media link
     */
    private fun openSocialLink(platform: String) {
        currentProject?.let { project ->
            // TODO: Add social links to ProjectDto in your backend
            // For now, show a placeholder message
            Toast.makeText(
                this,
                "Открыть $platform (в разработке)",
                Toast.LENGTH_SHORT
            ).show()

            // Example implementation:
            // val url = when (platform) {
            //     "facebook" -> project.social_links?.facebook
            //     "instagram" -> project.social_links?.instagram
            //     "linkedin" -> project.social_links?.linkedin
            //     else -> null
            // }
            // url?.let {
            //     val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            //     startActivity(intent)
            // }
        }
    }

    /**
     * Show loading state
     */
    private fun showLoading() {
        loadingProgressBar.visibility = View.VISIBLE
    }

    /**
     * Hide loading state
     */
    private fun hideLoading() {
        loadingProgressBar.visibility = View.GONE
    }

    /**
     * Show error message
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Data class for team members
     * TODO: Replace with actual DTO from your backend
     */
    data class TeamMember(
        val id: Int,
        val name: String,
        val role: String,
        val avatarUrl: String?
    )
}