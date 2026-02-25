package com.smsgateway24.nosignalclub.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun AboutScreen(nav: NavController) {
    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("О приложении", style = MaterialTheme.typography.headlineSmall)
        val packageInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
        Text("Версия: ${packageInfo.versionName}")

        OutlinedButton(onClick = {
            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pushkadev/NoSignalClub")))
        }) { Text("GitHub репозиторий") }

        OutlinedButton(onClick = {
            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pushkadev/NoSignalClub/issues")))
        }) { Text("Сообщить об ошибке или предложить изменения") }

        OutlinedButton(onClick = { nav.popBackStack() }) { Text("Назад") }
    }
}