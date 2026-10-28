package com.example.segundoparcial.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcialDashboardScreen(
    userEmail: String?,
    onNavigateToCapture: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToMap: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GeoTracker - Panel") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Salir")
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GeoTracker",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (!userEmail.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Usuario: $userEmail",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigateToCapture,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Capturar Nueva Ubicación GPS")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNavigateToList,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Ver Listado de Ubicaciones")
            }

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButton(
                onClick = onNavigateToMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Ver Mapa Interactivo de Ubicaciones")
            }
        }
    }
}
