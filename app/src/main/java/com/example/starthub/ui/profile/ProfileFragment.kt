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
import com.example.starthub.ui.auth.LoginFragment
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
        val tvUserId = view.findViewById<TextView>(R.id.tvUserId)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        lifecycleScope.launch {
            viewModel.user.collect { user ->
                if (user != null) {
                    tvName.text = "${user.first_name} ${user.last_name}"
                    tvEmail.text = "Email: ${user.email}"
                    tvDescription.text = "Bio: ${user.description ?: "No bio added"}"
                    tvPhone.text = "Phone: ${user.phone_numbers.firstOrNull() ?: "No phone"}"
                    tvUserId.text = "ID: ${user.id}"
                }
            }
        }
        lifecycleScope.launch {
            viewModel.logoutSuccess.collect { success ->
                if (success) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, LoginFragment())
                        .commit()
                }
            }
        }

        btnLogout.setOnClickListener {
            viewModel.logout()
        }

        viewModel.fetchUserProfile()
    }

}
