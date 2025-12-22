package com.example.starthub.data.remote.api

import android.content.Context
import android.content.Intent
import com.example.starthub.MainActivity
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: () -> String?,
    private val contextProvider: () -> Context
) : Interceptor {
    companion object {
        private var is401Handled = false
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenProvider()

        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else request

        val response = chain.proceed(newRequest)

        if (response.code == 401 && !is401Handled) {
            is401Handled = true
            val context = contextProvider()
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        return response
    }
}


