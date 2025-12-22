package com.example.starthub.ui.catalogue

import android.view.LayoutInflater
import android.widget.TextView
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.starthub.R
import com.example.starthub.data.remote.dto.ProjectDto

class ProjectAdapter(
    private val projects: List<ProjectDto>
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
    }

    override fun getItemCount() = projects.size

    class ProjectViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val projectName: TextView = itemView.findViewById(R.id.project_name)
        private val projectDescription: TextView = itemView.findViewById(R.id.project_description)

        fun bind(project: ProjectDto) {
            projectName.text = project.name
            projectDescription.text = project.description
        }
    }
}
