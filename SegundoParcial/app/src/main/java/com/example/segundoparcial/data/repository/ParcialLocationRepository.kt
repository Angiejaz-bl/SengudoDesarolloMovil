package com.example.segundoparcial.data.repository

import com.example.segundoparcial.data.model.ParcialLocationItem
import com.example.segundoparcial.data.model.ParcialLocationRequest
import com.example.segundoparcial.data.remote.ParcialApiService

class ParcialLocationRepository(
    private val apiService: ParcialApiService,
) {
    suspend fun saveLocation(
        name: String,
        description: String,
        latitude: Double,
        longitude: Double,
    ): Result<ParcialLocationItem> {
        return try {
            val request = ParcialLocationRequest(
                nombre = name,
                descripcion = description,
                latitud = latitude,
                longitud = longitude,
            )
            val response = apiService.saveLocation(request)
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.data
                if (body != null && (body.success == true || body.status == "success") && data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al guardar ubicación"))
                }
            } else {
                Result.failure(Exception("Error en servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Error de red al guardar la ubicación"))
        }
    }

    suspend fun getLocations(): Result<List<ParcialLocationItem>> {
        return try {
            val response = apiService.getLocations()
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.data
                if (body != null && (body.success == true || body.status == "success") && data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception(body?.message ?: "No se pudieron obtener las ubicaciones"))
                }
            } else {
                Result.failure(Exception("Error en servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Error de red al obtener ubicaciones"))
        }
    }
}
