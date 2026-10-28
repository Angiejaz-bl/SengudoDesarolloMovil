package com.example.segundoparcial.data.model

import com.google.gson.annotations.SerializedName

data class ParcialLocationRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double
)

data class ParcialLocationItem(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("latitud") val latitud: Double? = null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitud") val longitud: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("fecha_creacion") val fechaCreacion: String? = null
) {
    val displayName: String get() = nombre ?: name ?: "Sin nombre"
    val displayDescription: String get() = descripcion ?: description ?: "Sin descripción"
    val displayLatitude: Double get() = latitud ?: latitude ?: 0.0
    val displayLongitude: Double get() = longitud ?: longitude ?: 0.0
}

data class ParcialLocationResponse(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<ParcialLocationItem>? = null
)

data class ParcialSingleLocationResponse(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: ParcialLocationItem? = null
)
