package com.example.starthub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.starthub.data.local.prefs.TokenManager
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.ui.auth.LoginActivity
import com.example.starthub.ui.catalogue.CatalogueFragment
import com.example.starthub.ui.home.MainFragment
import com.example.starthub.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tokenManager = TokenManager(this)

        RetrofitClient.setContextProvider { this }
        RetrofitClient.setTokenProvider {
            runBlocking { tokenManager.getToken().first() }
        }

        val token = runBlocking { tokenManager.getToken().first() }
        if (token == null) {
            navigateToLoginActivity()
            return
        }

        setupBottomNavigation()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .commit()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> MainFragment()
                R.id.nav_catalogue -> CatalogueFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
                true
            } ?: false
        }
    }

    fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun handleLogout() {
        runBlocking {
            tokenManager.clearAll()
        }
        navigateToLoginActivity()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}