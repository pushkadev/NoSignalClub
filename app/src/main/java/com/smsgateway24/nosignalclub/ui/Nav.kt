package com.smsgateway24.nosignalclub.ui

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object About : Screen("about")
}