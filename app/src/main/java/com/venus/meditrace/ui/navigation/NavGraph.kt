package com.venus.meditrace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.venus.meditrace.ui.screens.about.AboutScreen
import com.venus.meditrace.ui.screens.history.ScanHistoryScreen
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

    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route
    ) {

        // ── Splash ────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // ── Onboarding ────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ──────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                onScanClick   = { navController.navigate(Screen.Scan.route) }
            )
        }

        // ── Scan ──────────────────────────────────────────────────────────
        composable(Screen.Scan.route) {
            val scanViewModel: ScanViewModel = viewModel()
            ScanScreen(
                viewModel  = scanViewModel,
                onVerified = { batchId ->
                    navController.navigate(Screen.ProductDetails.createRoute(batchId))
                },
                onNotFound = { navController.navigate(Screen.ProductNotFound.route) },
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
                ?.getString(Screen.ProductDetails.ARG_BATCH_ID)
                ?: return@composable

            val scanViewModel: ScanViewModel = viewModel()
            ProductDetailsScreen(
                viewModel = scanViewModel,
                batchId   = batchId,
                onBack    = { navController.popBackStack() }
            )
        }

        // ── Product Not Found ─────────────────────────────────────────────
        composable(Screen.ProductNotFound.route) {
            ProductNotFoundScreen(
                onReportCounterfeit = {
                    navController.navigate(Screen.ReportProduct.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Report Product ────────────────────────────────────────────────
        composable(Screen.ReportProduct.route) {
            val reportViewModel: ReportViewModel = viewModel()
            ReportProductScreen(
                viewModel = reportViewModel,
                onBack    = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
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