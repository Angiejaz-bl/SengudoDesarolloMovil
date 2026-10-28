package com.example.segundoparcial.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcialLocationPickerScreen(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onLocationConfirmed: (Double, Double) -> Unit,
    onCancelClicked: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val defaultFallback = LatLng(14.6349, -90.5069) // Ciudad de Guatemala
    var selectedLatLng by remember {
        mutableStateOf(
            if (initialLatitude != null && initialLongitude != null) {
                LatLng(initialLatitude, initialLongitude)
            } else {
                null
            }
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            selectedLatLng ?: defaultFallback,
            15f
        )
    }

    // Si no había coordenadas seleccionadas, intenta centrar en la posición GPS actual
    LaunchedEffect(Unit) {
        if (selectedLatLng == null) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    val currentGps = LatLng(loc.latitude, loc.longitude)
                    selectedLatLng = currentGps
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentGps, 15f)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Elegir ubicación en el Mapa") },
                navigationIcon = {
                    TextButton(onClick = onCancelClicked) {
                        Text("Cancelar")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Coordenadas seleccionadas:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (selectedLatLng != null) {
                        Text(
                            text = "Lat: ${selectedLatLng!!.latitude}\nLng: ${selectedLatLng!!.longitude}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "Toque cualquier punto del mapa para seleccionar la ubicación",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelClicked,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                selectedLatLng?.let {
                                    onLocationConfirmed(it.latitude, it.longitude)
                                }
                            },
                            enabled = selectedLatLng != null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true),
                onMapClick = { latLng ->
                    selectedLatLng = latLng
                }
            ) {
                selectedLatLng?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = "Punto Seleccionado",
                        snippet = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
                    )
                }
            }
        }
    }
}
