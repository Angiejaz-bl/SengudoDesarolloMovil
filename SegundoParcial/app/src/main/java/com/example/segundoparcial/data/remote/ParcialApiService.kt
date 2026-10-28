package com.example.segundoparcial.data.remote

import com.example.segundoparcial.data.model.ParcialLocationRequest
import com.example.segundoparcial.data.model.ParcialLocationResponse
import com.example.segundoparcial.data.model.ParcialLoginRequest
import com.example.segundoparcial.data.model.ParcialLoginResponse
import com.example.segundoparcial.data.model.ParcialSingleLocationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ParcialApiService {
    @POST("login")
    suspend fun login(
        @Body request: ParcialLoginRequest
    ): Response<ParcialLoginResponse>

    @POST("parcial/ubicaciones")
    suspend fun saveLocation(
        @Body request: ParcialLocationRequest
    ): Response<ParcialSingleLocationResponse>

    @GET("parcial/ubicaciones")
    suspend fun getLocations(): Response<ParcialLocationResponse>
}
