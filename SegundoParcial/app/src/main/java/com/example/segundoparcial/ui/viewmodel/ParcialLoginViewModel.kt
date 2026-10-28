package com.example.segundoparcial.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.segundoparcial.data.repository.ParcialAuthRepository
import com.example.segundoparcial.ui.state.ParcialLoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ParcialLoginViewModel(
    private val repository: ParcialAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParcialLoginState(isLoggedIn = repository.isLoggedIn()))
    val uiState: StateFlow<ParcialLoginState> = _uiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun login() {
        val current = _uiState.value
        if (current.email.isBlank() || current.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingrese correo y contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.login(current.email.trim(), current.password)
            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Credenciales incorrectas"
                    )
                }
            }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.update { ParcialLoginState(isLoggedIn = false) }
    }
}
