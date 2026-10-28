package com.example.segundoparcial.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("geotracker_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_EMAIL = "user_email"
    }

    fun saveAuthData(token: String, email: String?) {
        prefs.edit()
            .putString(KEY_JWT_TOKEN, token)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_JWT_TOKEN, null)
    }

    fun getEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
