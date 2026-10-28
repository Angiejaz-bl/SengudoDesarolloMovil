package com.example.segundoparcial.data.repository

import com.example.segundoparcial.data.local.SessionManager
import com.example.segundoparcial.data.model.ParcialLoginData
import com.example.segundoparcial.data.model.ParcialLoginRequest
import com.example.segundoparcial.data.remote.ParcialApiService

class ParcialAuthRepository(
    private val apiService: ParcialApiService,
    private val tokenManager: SessionManager,
) {
    suspend fun login(usuario: String, password: String): Result<ParcialLoginData> {
        return try {
            val response = apiService.login(ParcialLoginRequest(usuario = usuario, password = password))
            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.data?.token
                if (body != null && (body.success == true || body.status == "success") && !token.isNullOrBlank()) {
                    val userIdentifier = body.data?.user?.usuario ?: body.data?.user?.email ?: usuario
                    tokenManager.saveAuthData(token, userIdentifier)
                    Result.success(body.data!!)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al iniciar sesión"))
                }
            } else {
                Result.failure(Exception("Error en el servidor: código ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Error de conexión con el servidor"))
        }
    }

    fun getToken(): String? {
        return tokenManager.getToken()
    }

    fun getEmail(): String? {
        return tokenManager.getEmail()
    }

    fun logout() {
        tokenManager.clear()
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }
}
