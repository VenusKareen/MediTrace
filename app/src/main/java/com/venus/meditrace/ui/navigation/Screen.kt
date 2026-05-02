package com.venus.meditrace.ui.navigation

sealed class Screen(val route: String) {
    object Splash          : Screen("splash")
    object Onboarding      : Screen("onboarding")
    object Home            : Screen("home")
    object Scan            : Screen("scan")
    object ProductDetails  : Screen("product_details")
    object ProductNotFound : Screen("product_not_found")
    object ReportProduct   : Screen("report_product")
}