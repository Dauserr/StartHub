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
import com.example.starthub.MainActivity
import com.example.starthub.R
import com.example.starthub.viewmodel.CatalogueViewModel

class CatalogueFragment : Fragment() {

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

        viewModel = ViewModelProvider(this)[CatalogueViewModel::class.java]

        val projectsRecycler = view.findViewById<RecyclerView>(R.id.projects_recycler)
        val loadingBar = view.findViewById<ProgressBar>(R.id.loading_bar)
        val errorText = view.findViewById<TextView>(R.id.error_text)

        projectsRecycler.layoutManager = LinearLayoutManager(requireContext())

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
                is CatalogueViewModel.ProjectsState.TokenExpired -> {
                    (requireActivity() as? MainActivity)?.navigateToLoginActivity()
                }
            }
        }

        viewModel.fetchProjects()
    }
}