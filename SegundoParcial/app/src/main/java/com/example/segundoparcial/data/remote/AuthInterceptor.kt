package com.example.segundoparcial.data.remote

import com.example.segundoparcial.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenManager.getToken()

        if (original.header("Authorization") != null || token.isNullOrBlank() || original.url.encodedPath.endsWith("login")) {
            return chain.proceed(original)
        }

        val requestBuilder = original.newBuilder()
            .header("Authorization", "Bearer $token")

        return chain.proceed(requestBuilder.build())
    }
}
