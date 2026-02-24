package com.smsgateway24.nosignalclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smsgateway24.nosignalclub.ui.Screen
import com.smsgateway24.nosignalclub.ui.screens.AboutScreen
import com.smsgateway24.nosignalclub.ui.screens.HomeScreen
import com.smsgateway24.nosignalclub.ui.screens.SettingsScreen
import com.smsgateway24.nosignalclub.ui.theme.NoSignalClubTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoSignalClubTheme {
                App()
            }
        }
    }
}

@Composable
private fun App() {
    val nav = rememberNavController()

    Scaffold { padding ->
        NavHost(
            navController = nav,
            startDestination = Screen.Home.route,
            modifier = Modifier
        ) {
            composable(Screen.Home.route) { HomeScreen(nav) }
            composable(Screen.Settings.route) { SettingsScreen(nav) }
            composable(Screen.About.route) { AboutScreen(nav) }
        }
    }
}