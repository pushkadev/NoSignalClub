package com.smsgateway24.nosignalclub.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smsgateway24.nosignalclub.data.SettingsStore
import com.smsgateway24.nosignalclub.ui.Screen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(nav: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { SettingsStore(ctx) }

    val enabled by store.enabledFlow.collectAsState(initial = false)
    val targetNumber by store.targetNumberFlow.collectAsState(initial = "")

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op */ }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("NoSignalClub", style = MaterialTheme.typography.headlineSmall)

        Text("Цель: пересылать WhatsApp уведомления в SMS на кнопочный телефон.")

        Card {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Статус: " + if (enabled) "АКТИВНО" else "ОСТАНОВЛЕНО")
                Text("Номер для SMS: " + if (targetNumber.isBlank()) "не задан" else targetNumber)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            // Request SMS permission when enabling
                            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                            scope.launch { store.setEnabled(true) }
                        },
                        enabled = !enabled
                    ) { Text("Запустить") }

                    OutlinedButton(
                        onClick = { scope.launch { store.setEnabled(false) } },
                        enabled = enabled
                    ) { Text("Остановить") }
                }
            }
        }

        Card {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Системные доступы")
                OutlinedButton(onClick = {
                    // Open Notification Access settings
                    ctx.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }) {
                    Text("Открыть доступ к уведомлениям")
                }

                OutlinedButton(onClick = {
                    // Battery optimization settings (optional)
                    ctx.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                }) {
                    Text("Настройки батареи (рекомендуется)")
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { nav.navigate(Screen.Settings.route) }) { Text("Настройки") }
            OutlinedButton(onClick = { nav.navigate(Screen.About.route) }) { Text("О приложении") }
        }
    }
}