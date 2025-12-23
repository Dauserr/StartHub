package com.example.starthub.ui.catalogue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.starthub.R
import com.example.starthub.data.remote.dto.ProjectDto

/**
 * ProjectAdapter - RecyclerView adapter for displaying projects
 *
 * This adapter binds project data to the item_project layout.
 * Each item shows:
 * - Project name
 * - Project description
 *
 * Features:
 * - Efficient ViewHolder pattern
 * - Click listener support (for future project detail screen)
 * - Handles empty state
 *
 * @param projects List of projects to display
 */
class ProjectAdapter(
    private val projects: List<ProjectDto>
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    // Optional: Click listener for when user taps on a project
    private var onItemClickListener: ((ProjectDto) -> Unit)? = null

    /**
     * Create new ViewHolder instances
     * Called by RecyclerView when it needs a new ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    /**
     * Bind data to ViewHolder
     * Called by RecyclerView to display data at specified position
     *
     * @param holder ViewHolder to bind data to
     * @param position Position in the dataset
     */
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)

        // Set click listener (if defined)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(project)
        }
    }

    /**
     * Get total number of items in dataset
     *
     * @return Number of projects
     */
    override fun getItemCount(): Int = projects.size

    /**
     * Set click listener for project items
     *
     * Example usage:
     * ```
     * adapter.setOnItemClickListener { project ->
     *     // Navigate to project detail screen
     *     navigateToProjectDetail(project.id)
     * }
     * ```
     *
     * @param listener Lambda function to handle click events
     */
    fun setOnItemClickListener(listener: (ProjectDto) -> Unit) {
        onItemClickListener = listener
    }

    /**
     * ViewHolder for project items
     *
     * Holds references to views in item_project layout
     * to avoid repeated findViewById calls (improves performance)
     */
    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Views
        private val projectName: TextView = itemView.findViewById(R.id.project_name)
        private val projectDescription: TextView = itemView.findViewById(R.id.project_description)

        /**
         * Bind project data to views
         *
         * @param project Project data to display
         */
        fun bind(project: ProjectDto) {
            projectName.text = project.name
            projectDescription.text = project.description

            // Optional: You can add more fields here if your layout has them
            // For example:
            // projectAuthor.text = project.user?.first_name
            // projectGoal.text = "${project.current_sum} / ${project.goal_sum}"
            // projectStatus.text = project.status
        }
    }
}