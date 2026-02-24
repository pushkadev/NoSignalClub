package com.smsgateway24.nosignalclub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.smsgateway24.nosignalclub.data.SettingsStore
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(nav: NavController) {
    val ctx = LocalContext.current
    val store = remember { SettingsStore(ctx) }
    val scope = rememberCoroutineScope()

    val savedNumber by store.targetNumberFlow.collectAsState(initial = "")
    var number by remember(savedNumber) { mutableStateOf(savedNumber) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Номер для отправки SMS") },
            placeholder = { Text("+49...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { scope.launch { store.setTargetNumber(number.trim()) } },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Сохранить") }

        OutlinedButton(
            onClick = { nav.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Назад") }
    }
}