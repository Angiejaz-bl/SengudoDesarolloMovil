package com.example.segundoparcial.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.segundoparcial.data.local.SessionManager
import com.example.segundoparcial.data.remote.ParcialRetrofitClient
import com.example.segundoparcial.data.repository.ParcialAuthRepository
import com.example.segundoparcial.data.repository.ParcialLocationRepository
import com.example.segundoparcial.ui.screens.*
import com.example.segundoparcial.ui.viewmodel.ParcialLocationViewModel
import com.example.segundoparcial.ui.viewmodel.ParcialLoginViewModel
import com.example.segundoparcial.ui.viewmodel.ParcialViewModelFactory

@Composable
fun ParcialAppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val tokenManager = SessionManager(context)
    val apiService = ParcialRetrofitClient.getApiService(tokenManager)

    val authRepository = ParcialAuthRepository(apiService, tokenManager)
    val locationRepository = ParcialLocationRepository(apiService)

    val loginViewModel: ParcialLoginViewModel = viewModel(
        factory = ParcialViewModelFactory { ParcialLoginViewModel(authRepository) }
    )

    val locationViewModel: ParcialLocationViewModel = viewModel(
        factory = ParcialViewModelFactory { ParcialLocationViewModel(locationRepository) }
    )

    val loginState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val captureState by locationViewModel.captureState.collectAsStateWithLifecycle()
    val listState by locationViewModel.listState.collectAsStateWithLifecycle()
    val mapState by locationViewModel.mapState.collectAsStateWithLifecycle()

    val startDestination = if (authRepository.isLoggedIn()) {
        ParcialNavRoutes.DASHBOARD
    } else {
        ParcialNavRoutes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ParcialNavRoutes.LOGIN) {
            LaunchedEffect(loginState.isLoggedIn) {
                if (loginState.isLoggedIn) {
                    navController.navigate(ParcialNavRoutes.DASHBOARD) {
                        popUpTo(ParcialNavRoutes.LOGIN) { inclusive = true }
                    }
                }
            }

            ParcialLoginScreen(
                state = loginState,
                onEmailChanged = loginViewModel::onEmailChanged,
                onPasswordChanged = loginViewModel::onPasswordChanged,
                onLoginClicked = loginViewModel::login
            )
        }

        composable(ParcialNavRoutes.DASHBOARD) {
            ParcialDashboardScreen(
                userEmail = authRepository.getEmail(),
                onNavigateToCapture = {
                    locationViewModel.resetCaptureState()
                    navController.navigate(ParcialNavRoutes.CAPTURE)
                },
                onNavigateToList = {
                    locationViewModel.fetchLocations()
                    navController.navigate(ParcialNavRoutes.LIST)
                },
                onNavigateToMap = {
                    locationViewModel.fetchLocations()
                    navController.navigate(ParcialNavRoutes.MAP)
                },
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(ParcialNavRoutes.LOGIN) {
                        popUpTo(ParcialNavRoutes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        composable(ParcialNavRoutes.CAPTURE) {
            ParcialLocationCaptureScreen(
                state = captureState,
                onNameChanged = locationViewModel::onCaptureNameChanged,
                onDescriptionChanged = locationViewModel::onCaptureDescriptionChanged,
                onCoordinatesCaptured = locationViewModel::setCapturedCoordinates,
                onOpenMapPickerClicked = {
                    navController.navigate(ParcialNavRoutes.PICKER)
                },
                onSaveClicked = locationViewModel::saveCurrentLocation,
                onSuccessHandled = {
                    navController.popBackStack()
                },
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable(ParcialNavRoutes.PICKER) {
            ParcialLocationPickerScreen(
                initialLatitude = captureState.latitude,
                initialLongitude = captureState.longitude,
                onLocationConfirmed = { lat, lng ->
                    locationViewModel.setCapturedCoordinates(lat, lng)
                    navController.popBackStack()
                },
                onCancelClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable(ParcialNavRoutes.LIST) {
            ParcialLocationListScreen(
                state = listState,
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable(ParcialNavRoutes.MAP) {
            ParcialMapScreen(
                state = mapState,
                onMarkerClicked = locationViewModel::onMarkerClicked,
                onDismissDialog = locationViewModel::dismissDialog,
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}
