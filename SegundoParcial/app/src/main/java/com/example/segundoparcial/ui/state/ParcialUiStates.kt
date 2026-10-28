package com.example.segundoparcial.ui.state

import com.example.segundoparcial.data.model.ParcialLocationItem

data class ParcialLoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

data class ParcialLocationCaptureState(
    val name: String = "",
    val description: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class ParcialLocationListState(
    val locations: List<ParcialLocationItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class ParcialMapState(
    val locations: List<ParcialLocationItem> = emptyList(),
    val selectedLocation: ParcialLocationItem? = null,
    val showDialog: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
