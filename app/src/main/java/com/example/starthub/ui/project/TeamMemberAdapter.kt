package com.example.starthub.ui.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.starthub.R

/**
 * TeamMemberAdapter - RecyclerView adapter for displaying team members
 *
 * This adapter binds team member data to the item_team_member layout.
 * Each item shows:
 * - Team member avatar (circular image)
 * - Member name
 * - Member role/position
 *
 * Features:
 * - Efficient ViewHolder pattern
 * - Image loading support (placeholder for now)
 * - Clean UI with circular avatars
 *
 * @param teamMembers List of team members to display
 */
class TeamMemberAdapter(
    private val teamMembers: List<ProjectDetailActivity.TeamMember>
) : RecyclerView.Adapter<TeamMemberAdapter.TeamMemberViewHolder>() {

    /**
     * Create new ViewHolder instances
     * Called by RecyclerView when it needs a new ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamMemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_member, parent, false)
        return TeamMemberViewHolder(view)
    }

    /**
     * Bind data to ViewHolder
     * Called by RecyclerView to display data at specified position
     *
     * @param holder ViewHolder to bind data to
     * @param position Position in the dataset
     */
    override fun onBindViewHolder(holder: TeamMemberViewHolder, position: Int) {
        val teamMember = teamMembers[position]
        holder.bind(teamMember)
    }

    /**
     * Get total number of items in dataset
     *
     * @return Number of team members
     */
    override fun getItemCount(): Int = teamMembers.size

    /**
     * ViewHolder for team member items
     *
     * Holds references to views in item_team_member layout
     * to avoid repeated findViewById calls (improves performance)
     */
    class TeamMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Views
        private val memberAvatarImageView: ImageView = itemView.findViewById(R.id.memberAvatarImageView)
        private val memberNameTextView: TextView = itemView.findViewById(R.id.memberNameTextView)
        private val memberRoleTextView: TextView = itemView.findViewById(R.id.memberRoleTextView)

        /**
         * Bind team member data to views
         *
         * @param teamMember Team member data to display
         */
        fun bind(teamMember: ProjectDetailActivity.TeamMember) {
            memberNameTextView.text = teamMember.name
            memberRoleTextView.text = teamMember.role

            // Load avatar image
            // For now, use placeholder. To load actual images, use Glide or Coil:
            // Glide.with(itemView.context)
            //     .load(teamMember.avatarUrl)
            //     .placeholder(R.drawable.img_profile_placeholder)
            //     .error(R.drawable.img_profile_placeholder)
            //     .into(memberAvatarImageView)

            // For now, just set placeholder
            memberAvatarImageView.setImageResource(R.drawable.img_profile_placeholder)
        }
    }
}