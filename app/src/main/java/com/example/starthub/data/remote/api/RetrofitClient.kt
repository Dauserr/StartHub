package com.example.starthub.data.remote.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://starthub.kz/api/v2/"

    private var tokenProvider: (() -> String?)? = null
    private var contextProvider: (() -> Context)? = null

    fun setTokenProvider(provider: () -> String?) {
        tokenProvider = provider
    }

    fun setContextProvider(provider: () -> Context) {
        contextProvider = provider
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(
                AuthInterceptor(
                    tokenProvider = { tokenProvider?.invoke() },
                    contextProvider = { contextProvider?.invoke()!! }
                )
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService: ApiService
        get() = retrofit.create(ApiService::class.java)
}
