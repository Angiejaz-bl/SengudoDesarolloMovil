package com.example.segundoparcial.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.segundoparcial.data.model.ParcialLocationItem
import com.example.segundoparcial.ui.state.ParcialMapState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcialMapScreen(
    state: ParcialMapState,
    onMarkerClicked: (ParcialLocationItem) -> Unit,
    onDismissDialog: () -> Unit,
    onBackClicked: () -> Unit
) {
    val defaultLocation = LatLng(14.6349, -90.5069) // Ciudad de Guatemala por defecto
    val initialPos = if (state.locations.isNotEmpty()) {
        LatLng(state.locations.first().displayLatitude, state.locations.first().displayLongitude)
    } else {
        defaultLocation
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 12f)
    }

    LaunchedEffect(state.locations) {
        if (state.locations.isNotEmpty()) {
            val first = state.locations.first()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(first.displayLatitude, first.displayLongitude),
                13f
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Ubicaciones") },
                navigationIcon = {
                    TextButton(onClick = onBackClicked) {
                        Text("Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (!state.errorMessage.isNullOrBlank()) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = true)
                ) {
                    state.locations.forEach { location ->
                        Marker(
                            state = MarkerState(position = LatLng(location.displayLatitude, location.displayLongitude)),
                            title = location.displayName,
                            snippet = location.displayDescription,
                            onClick = {
                                onMarkerClicked(location)
                                true // Retorna true para suprimir el InfoWindow por defecto
                            }
                        )
                    }
                }

                if (state.locations.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = "No hay ubicaciones registradas para mostrar en el mapa.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (state.showDialog && state.selectedLocation != null) {
                    ParcialLocationDetailDialog(
                        location = state.selectedLocation,
                        onDismiss = onDismissDialog
                    )
                }
            }
        }
    }
}
