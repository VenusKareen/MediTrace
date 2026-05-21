package com.venus.meditrace.ui.navigation

sealed class Screen(val route: String) {
    object Splash          : Screen("splash")
    object Onboarding      : Screen("onboarding")
    object Home            : Screen("home")
    object Scan            : Screen("scan")
    object ScanHistory     : Screen("scan_history")
    object About           : Screen("about")
    object ProductNotFound : Screen("product_not_found")
    object ReportProduct   : Screen("report_product")

    object ProductDetails : Screen("product_details/{batchId}") {
        const val ARG_BATCH_ID = "batchId"
        fun createRoute(batchId: String) = "product_details/$batchId"
    }
}