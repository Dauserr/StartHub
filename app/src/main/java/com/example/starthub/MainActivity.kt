package com.example.starthub

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.starthub.data.local.prefs.TokenManager
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.ui.auth.LoginFragment
import com.example.starthub.ui.catalogue.CatalogueFragment
import com.example.starthub.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tokenManager = TokenManager(this)

        RetrofitClient.setContextProvider { this }
        RetrofitClient.setTokenProvider {
            runBlocking { tokenManager.getToken().first() }
        }


        if (savedInstanceState == null) {
            val token = runBlocking { tokenManager.getToken().first() }

            if (token != null) {
                showCatalogue()
                setupBottomNav()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .commit()
            }
        }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_catalogue -> {
                    showCatalogue()
                    true
                }
                R.id.nav_profile -> {
                    showProfile()
                    true
                }
                else -> false
            }
        }
    }

    private fun showCatalogue() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CatalogueFragment())
            .commit()
    }

    private fun showProfile() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ProfileFragment())
            .commit()
    }

    fun showBottomNav() {
        findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.VISIBLE
    }
}
