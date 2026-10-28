package com.example.segundoparcial.data.model

import com.google.gson.annotations.SerializedName

data class ParcialLoginRequest(
    @SerializedName("usuario") val usuario: String,
    @SerializedName("password") val password: String
)

data class ParcialUserData(
    @SerializedName("id") val id: Any? = null,
    @SerializedName("usuario") val usuario: String? = null,
    @SerializedName("email") val email: String? = null
)

data class ParcialLoginData(
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: ParcialUserData? = null
)

data class ParcialLoginResponse(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: ParcialLoginData? = null
)
