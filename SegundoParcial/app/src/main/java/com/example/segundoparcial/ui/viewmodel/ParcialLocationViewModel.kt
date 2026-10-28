package com.example.segundoparcial.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.segundoparcial.data.model.ParcialLocationItem
import com.example.segundoparcial.data.repository.ParcialLocationRepository
import com.example.segundoparcial.ui.state.ParcialLocationCaptureState
import com.example.segundoparcial.ui.state.ParcialLocationListState
import com.example.segundoparcial.ui.state.ParcialMapState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ParcialLocationViewModel(
    private val repository: ParcialLocationRepository
) : ViewModel() {

    private val _captureState = MutableStateFlow(ParcialLocationCaptureState())
    val captureState: StateFlow<ParcialLocationCaptureState> = _captureState.asStateFlow()

    private val _listState = MutableStateFlow(ParcialLocationListState())
    val listState: StateFlow<ParcialLocationListState> = _listState.asStateFlow()

    private val _mapState = MutableStateFlow(ParcialMapState())
    val mapState: StateFlow<ParcialMapState> = _mapState.asStateFlow()

    fun onCaptureNameChanged(name: String) {
        _captureState.update { it.copy(name = name, errorMessage = null) }
    }

    fun onCaptureDescriptionChanged(desc: String) {
        _captureState.update { it.copy(description = desc, errorMessage = null) }
    }

    fun setCapturedCoordinates(lat: Double, lng: Double) {
        _captureState.update { it.copy(latitude = lat, longitude = lng) }
    }

    fun saveCurrentLocation() {
        val state = _captureState.value
        val lat = state.latitude
        val lng = state.longitude

        if (state.name.isBlank() || state.description.isBlank()) {
            _captureState.update { it.copy(errorMessage = "Ingrese nombre y descripción") }
            return
        }
        if (lat == null || lng == null) {
            _captureState.update { it.copy(errorMessage = "Capture primero la ubicación GPS") }
            return
        }

        viewModelScope.launch {
            _captureState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.saveLocation(state.name, state.description, lat, lng)
            result.onSuccess {
                _captureState.update {
                    ParcialLocationCaptureState(isSuccess = true)
                }
            }.onFailure { err ->
                _captureState.update {
                    it.copy(isLoading = false, errorMessage = err.localizedMessage ?: "Error al guardar")
                }
            }
        }
    }

    fun resetCaptureState() {
        _captureState.update { ParcialLocationCaptureState() }
    }

    fun fetchLocations() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, errorMessage = null) }
            _mapState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.getLocations()
            result.onSuccess { items ->
                _listState.update { ParcialLocationListState(locations = items, isLoading = false) }
                _mapState.update { ParcialMapState(locations = items, isLoading = false) }
            }.onFailure { error ->
                val msg = error.localizedMessage ?: "Error al cargar ubicaciones"
                _listState.update { it.copy(isLoading = false, errorMessage = msg) }
                _mapState.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    fun onMarkerClicked(location: ParcialLocationItem) {
        _mapState.update {
            it.copy(selectedLocation = location, showDialog = true)
        }
    }

    fun dismissDialog() {
        _mapState.update {
            it.copy(showDialog = false, selectedLocation = null)
        }
    }
}
