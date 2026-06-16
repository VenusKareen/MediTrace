package com.venus.meditrace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.venus.meditrace.ui.screens.report.ReportProductScreen
import com.venus.meditrace.ui.screens.result.ProductDetailsScreen
import com.venus.meditrace.ui.screens.result.ProductNotFoundScreen
import com.venus.meditrace.ui.screens.scan.ScanScreen
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
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = if (tokenManager.isLoggedIn()) Screen.Home.route else AuthRoutes.LOGIN
    }

    if (startDestination == null) return

    // Auth repo needs context to sync access token into SecurePrefs
    // so RetrofitClient.apiService interceptor can attach Bearer header
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

    // AndroidViewModels — created automatically with application context
    val scanViewModel:   ScanViewModel   = viewModel()
    val reportViewModel: ReportViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = startDestination!!
    ) {

        // ── Auth ──────────────────────────────────────────────────────────

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

        // ── Home ──────────────────────────────────────────────────────────

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                onScanClick   = {
                    scanViewModel.startScanning()
                    navController.navigate(Screen.Scan.route)
                }
            )
        }

        // ── Scan ──────────────────────────────────────────────────────────

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

        // ── Product Details ───────────────────────────────────────────────

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

        // ── Product Not Found ─────────────────────────────────────────────

        composable(Screen.ProductNotFound.route) {
            ProductNotFoundScreen(
                onBack              = { navController.popBackStack() },
                onReportCounterfeit = { navController.navigate(Screen.ReportProduct.route) }
            )
        }

        // ── Report Product ────────────────────────────────────────────────

        composable(Screen.ReportProduct.route) {
            ReportProductScreen(
                viewModel = reportViewModel,
                onBack    = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // ── Scan History ──────────────────────────────────────────────────

        composable(Screen.ScanHistory.route) {
            ScanHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── About ─────────────────────────────────────────────────────────

        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}