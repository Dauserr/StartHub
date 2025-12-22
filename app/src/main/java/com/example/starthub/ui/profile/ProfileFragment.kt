package com.example.starthub.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.starthub.R
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(requireContext()) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Observe user data
        lifecycleScope.launch {
            viewModel.user.collect { user ->
                if (user != null) {
                    tvName.text = "${user.first_name} ${user.last_name}"
                    tvEmail.text = user.email
                    tvDescription.text = user.description ?: "No bio"
                    tvPhone.text = user.phone_numbers.firstOrNull() ?: "No phone"
                }
            }
        }

        // Logout button
        btnLogout.setOnClickListener {
            viewModel.logout()
            // TODO: Navigate to LoginFragment
        }

        // Fetch profile on load
        viewModel.fetchUserProfile()
    }
}
