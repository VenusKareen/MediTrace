package com.venus.meditrace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.venus.meditrace.data.local.TokenManager
import com.venus.meditrace.data.remote.RetrofitClient
import com.venus.meditrace.data.repository.AuthRepository
import com.venus.meditrace.ui.auth.AuthViewModel
import com.venus.meditrace.ui.auth.LoginScreen
import com.venus.meditrace.ui.auth.RegisterScreen
import com.venus.meditrace.ui.screens.about.AboutScreen
import com.venus.meditrace.ui.screens.history.ScanHistoryScreen
import com.venus.meditrace.ui.screens.home.HomeScreen
import com.venus.meditrace.ui.screens.onboarding.OnboardingScreen
import com.venus.meditrace.ui.screens.report.ReportProductScreen
import com.venus.meditrace.ui.screens.result.ProductDetailsScreen
import com.venus.meditrace.ui.screens.result.ProductNotFoundScreen
import com.venus.meditrace.ui.screens.scan.ScanScreen
import com.venus.meditrace.util.Constants
import com.venus.meditrace.util.SecurePrefs
import com.venus.meditrace.viewmodel.ReportViewModel
import com.venus.meditrace.viewmodel.ScanViewModel

private object AuthRoutes {
    const val LOGIN    = "login"
    const val REGISTER = "register"
}

@Composable
fun MediTraceNavGraph(
    navController: NavHostController,
    tokenManager:  TokenManager
) {
    val context = LocalContext.current

    // First launch ever -> show Onboarding once.
    // Every launch after that -> go straight to Home, no login required.
    // Login is never a forced starting point — only reached if the user
    // chooses to sign in (pharmacist flow) from inside Home.
    val onboardingDone = remember {
        SecurePrefs.getBoolean(context, Constants.KEY_ONBOARDING_DONE, false)
    }
    val startDestination = if (onboardingDone) Screen.Home.route else Screen.Onboarding.route

    val authRepository = remember {
        AuthRepository(
            api          = RetrofitClient.create(tokenManager),
            tokenManager = tokenManager,
            context      = context
        )
    }

    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authRepository) as T
            }
        }
    )

    val scanViewModel:   ScanViewModel   = viewModel()
    val reportViewModel: ReportViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {

        //Onboarding

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        //Auth

        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                viewModel            = authViewModel,
                onNavigateToRegister = { navController.navigate(AuthRoutes.REGISTER) },
                onLoginSuccess       = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(AuthRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(AuthRoutes.REGISTER) {
            RegisterScreen(
                viewModel         = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(AuthRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        //Home

        composable(Screen.Home.route) {
            val isLoggedIn by produceState(initialValue = false, tokenManager) {
                value = tokenManager.isLoggedIn()
            }

            HomeScreen(
                navController = navController,
                onScanClick   = {
                    scanViewModel.startScanning()
                    navController.navigate(Screen.Scan.route)
                },
                isLoggedIn   = isLoggedIn,
                onLoginClick = { navController.navigate(AuthRoutes.LOGIN) }
            )
        }

        //Scan

        composable(Screen.Scan.route) {
            ScanScreen(
                viewModel  = scanViewModel,
                onVerified = { batchId ->
                    navController.navigate(Screen.ProductDetails.createRoute(batchId)) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                    }
                },
                onNotFound = {
                    navController.navigate(Screen.ProductNotFound.route) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                    }
                },
                onBack     = { navController.popBackStack() }
            )
        }

        //Product Details

        composable(
            route     = Screen.ProductDetails.route,
            arguments = listOf(
                navArgument(Screen.ProductDetails.ARG_BATCH_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments
                ?.getString(Screen.ProductDetails.ARG_BATCH_ID) ?: ""
            ProductDetailsScreen(
                viewModel = scanViewModel,
                batchId   = batchId,
                onBack    = { navController.popBackStack() }
            )
        }

        //Product Not Found

        composable(Screen.ProductNotFound.route) {
            ProductNotFoundScreen(
                onBack              = { navController.popBackStack() },
                onReportCounterfeit = { navController.navigate(Screen.ReportProduct.route) }
            )
        }

        //Report Product

        composable(Screen.ReportProduct.route) {
            ReportProductScreen(
                viewModel = reportViewModel,
                onBack    = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        //Scan History

        composable(Screen.ScanHistory.route) {
            ScanHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        //About

        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}