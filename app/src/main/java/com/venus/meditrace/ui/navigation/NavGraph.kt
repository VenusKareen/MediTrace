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

    val scanViewModel:   ScanViewModel   = viewModel()
    val reportViewModel: ReportViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                onScanClick   = { navController.navigate(Screen.Scan.route) }
            )
        }

        composable(Screen.Scan.route) {
            ScanScreen(
                viewModel  = scanViewModel,
                onVerified = { navController.navigate(Screen.ProductDetails.route) },
                onNotFound = { navController.navigate(Screen.ProductNotFound.route) },
                onBack     = { navController.popBackStack() }
            )
        }

        composable(Screen.ProductDetails.route) {
            ProductDetailsScreen(
                viewModel = scanViewModel,
                onBack    = { navController.popBackStack() }
            )
        }

        composable(Screen.ProductNotFound.route) {
            ProductNotFoundScreen(
                onReportCounterfeit = { navController.navigate(Screen.ReportProduct.route) },
                onBack              = { navController.popBackStack() }
            )
        }

        composable(Screen.ReportProduct.route) {
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
    }
}