package com.example.starthub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.starthub.data.local.prefs.TokenManager
import com.example.starthub.data.remote.api.RetrofitClient
import com.example.starthub.ui.auth.LoginFragment
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tokenManager = TokenManager(this)
        RetrofitClient.setTokenProvider {
            runBlocking {
                tokenManager.getToken().first()
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }
}
