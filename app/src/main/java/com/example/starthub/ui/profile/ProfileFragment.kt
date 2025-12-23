package com.example.starthub.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.starthub.MainActivity
import com.example.starthub.R
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(requireContext()) as T
            }
        })[ProfileViewModel::class.java]

        setupViews(view)
        observeUser()
        observeLogout()

        viewModel.fetchUserProfile()
    }

    private fun setupViews(view: View) {
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
                    tvDescription.text = "О себе: ${user.description ?: "Не указано"}"
                    tvPhone.text = "Телефон: ${user.phone_numbers.firstOrNull() ?: "Не указан"}"
                    tvUserId.text = "ID: ${user.id}"
                }
            }
        }

        btnLogout.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Выйти") { dialog, _ ->
                    viewModel.logout()
                    dialog.dismiss()
                }
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun observeUser() {
        lifecycleScope.launch {
            viewModel.user.collect { user ->
                // User data is displayed in setupViews
            }
        }
    }

    private fun observeLogout() {
        lifecycleScope.launch {
            viewModel.logoutSuccess.collect { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Выход выполнен", Toast.LENGTH_SHORT).show()
                    (requireActivity() as? MainActivity)?.handleLogout()
                }
            }
        }
    }
}