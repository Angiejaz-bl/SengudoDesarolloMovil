package com.example.segundoparcial.data.remote

import com.example.segundoparcial.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ParcialRetrofitClient {

    var BASE_URL = "http://202400137angie.aplicacionesweb2026.com/"

    private var apiService: ParcialApiService? = null

    fun getApiService(tokenManager: SessionManager): ParcialApiService {
        if (apiService == null) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ParcialApiService::class.java)
        }
        return apiService!!
    }

    fun resetClient() {
        apiService = null
    }
}
