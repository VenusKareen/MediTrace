package com.venus.meditrace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.venus.meditrace.data.local.TokenManager
import com.venus.meditrace.data.remote.RetrofitClient
import com.venus.meditrace.data.repository.AuthRepository
import com.venus.meditrace.ui.auth.AuthViewModel
import com.venus.meditrace.ui.auth.LoginScreen
import com.venus.meditrace.ui.auth.RegisterScreen

object Routes {
    const val LOGIN    = "login"
    const val REGISTER = "register"
    const val HOME     = "home"
}

@Composable
fun MediTraceNavGraph(
    navController: NavHostController,
    tokenManager: TokenManager
) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = if (tokenManager.isLoggedIn()) Routes.HOME else Routes.LOGIN
    }

    if (startDestination == null) return

    val repository = AuthRepository(RetrofitClient.create(tokenManager), tokenManager)
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repository) as T
            }
        }
    )

    NavHost(
        navController    = navController,
        startDestination = startDestination!!
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel            = authViewModel,
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess       = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel         = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            // Your existing HomeScreen goes here
        }
    }
}