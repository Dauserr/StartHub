package com.example.starthub.ui.catalogue

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
import com.example.starthub.R
import com.example.starthub.viewmodel.CatalogueViewModel

class CatalogueFragment : Fragment() {

    private lateinit var projectsRecycler: RecyclerView
    private lateinit var loadingBar: ProgressBar
    private lateinit var errorText: TextView

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

        projectsRecycler = view.findViewById(R.id.projects_recycler)
        loadingBar = view.findViewById(R.id.loading_bar)
        errorText = view.findViewById(R.id.error_text)

        viewModel = ViewModelProvider(this)[CatalogueViewModel::class.java]

        projectsRecycler.layoutManager = LinearLayoutManager(requireContext())

        observeProjects()
        viewModel.fetchProjects()
    }

    private fun observeProjects() {
        viewModel.projectsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CatalogueViewModel.ProjectsState.Loading -> {
                    loadingBar.visibility = View.VISIBLE
                    projectsRecycler.visibility = View.GONE
                    errorText.visibility = View.GONE
                }
                is CatalogueViewModel.ProjectsState.Success -> {
                    loadingBar.visibility = View.GONE
                    projectsRecycler.visibility = View.VISIBLE
                    errorText.visibility = View.GONE
                    projectsRecycler.adapter = ProjectAdapter(state.projects)
                }
                is CatalogueViewModel.ProjectsState.Error -> {
                    loadingBar.visibility = View.GONE
                    projectsRecycler.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
                    errorText.text = state.message
                }
            }
        }
    }
}
