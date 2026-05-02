package com.venus.meditrace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.venus.meditrace.ui.screens.home.HomeScreen
import com.venus.meditrace.ui.screens.onboarding.OnboardingScreen
import com.venus.meditrace.ui.screens.report.ReportProductScreen
import com.venus.meditrace.ui.screens.result.ProductDetailsScreen
import com.venus.meditrace.ui.screens.result.ProductNotFoundScreen
import com.venus.meditrace.ui.screens.scan.ScanScreen
import com.venus.meditrace.ui.screens.splash.SplashScreen
import com.venus.meditrace.viewmodel.ReportViewModel
import com.venus.meditrace.viewmodel.ScanViewModel

@Composable
fun MediTraceNavGraph(navController: NavHostController) {

    // ScanViewModel shared across Scan → Result screens
    val scanViewModel: ScanViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route
    ) {

        // ── Splash ─────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding ─────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ───────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                onScanClick = {
                    scanViewModel.startScanning()
                    navController.navigate(Screen.Scan.route)
                }
            )
        }

        // ── Scan ───────────────────────────────────────────────────────────
        composable(Screen.Scan.route) {
            ScanScreen(
                viewModel  = scanViewModel,
                onVerified = {
                    navController.navigate(Screen.ProductDetails.route) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                    }
                },
                onNotFound = {
                    navController.navigate(Screen.ProductNotFound.route) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Product Details (Verified) ──────────────────────────────────────
        composable(Screen.ProductDetails.route) {
            ProductDetailsScreen(
                viewModel = scanViewModel,
                onBack    = {
                    scanViewModel.reset()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Product Not Found ───────────────────────────────────────────────
        composable(Screen.ProductNotFound.route) {
            ProductNotFoundScreen(
                onReportCounterfeit = {
                    navController.navigate(Screen.ReportProduct.route)
                },
                onBack = {
                    scanViewModel.reset()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Report Product ──────────────────────────────────────────────────
        composable(Screen.ReportProduct.route) {
            val reportVm: ReportViewModel = viewModel()
            ReportProductScreen(
                viewModel = reportVm,
                onBack    = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}