package com.example.segundoparcial.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.segundoparcial.ui.state.ParcialLocationCaptureState
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcialLocationCaptureScreen(
    state: ParcialLocationCaptureState,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onCoordinatesCaptured: (Double, Double) -> Unit,
    onOpenMapPickerClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onSuccessHandled: () -> Unit,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    onCoordinatesCaptured(loc.latitude, loc.longitude)
                }
            }
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSuccessHandled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Capturar Ubicación") },
                navigationIcon = {
                    TextButton(onClick = onBackClicked) {
                        Text("Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChanged,
                label = { Text("Nombre del Lugar") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Coordenadas Seleccionadas:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (state.latitude != null && state.longitude != null) {
                        Text("Latitud: ${state.latitude}")
                        Text("Longitud: ${state.longitude}")
                    } else {
                        Text(
                            text = "Obtenga la ubicación mediante GPS o elija un punto en el mapa",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            if (permissionGranted) {
                                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                    if (loc != null) {
                                        onCoordinatesCaptured(loc.latitude, loc.longitude)
                                    }
                                }
                            } else {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Obtener GPS Actual")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ElevatedButton(
                        onClick = onOpenMapPickerClicked,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Elegir en el Mapa")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!state.errorMessage.isNullOrBlank()) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = onSaveClicked,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar Ubicación en Backend")
                }
            }
        }
    }
}
