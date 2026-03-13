package com.smsgateway24.nosignalclub.ui.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smsgateway24.nosignalclub.R
import com.smsgateway24.nosignalclub.data.SettingsStore
import com.smsgateway24.nosignalclub.ui.Screen
import com.smsgateway24.nosignalclub.ui.theme.*
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
    ) { }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulseAlpha"
    )

    val statusColor by animateColorAsState(
        targetValue = if (enabled) NeonGreen else TextMuted,
        animationSpec = tween(500), label = "statusColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        Box(modifier = Modifier.fillMaxSize().drawBehind { drawGrid(this) })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(listOf(NeonGreen.copy(alpha = 0.2f), NeonGreenDim.copy(alpha = 0.05f))))
                        .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("NoSignalClub", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Text(stringResource(R.string.app_subtitle), color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { nav.navigate(Screen.Settings.route) }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
                }
            }

            Spacer(Modifier.height(4.dp))

            // Status card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(width = 1.dp, color = if (enabled) NeonGreen.copy(alpha = 0.25f) else BorderSubtle, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.status_service), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor.copy(alpha = if (enabled) pulseAlpha else 0.4f)))
                            Text(
                                stringResource(if (enabled) R.string.status_active else R.string.status_stopped),
                                color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(SurfaceDark).border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)).padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Text(
                                if (targetNumber.isBlank()) stringResource(R.string.number_not_set) else targetNumber,
                                color = if (targetNumber.isBlank()) TextMuted else TextPrimary,
                                fontSize = 14.sp, fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                                scope.launch { store.setEnabled(true) }
                            },
                            enabled = !enabled,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DeepBlack, disabledContainerColor = BorderSubtle, disabledContentColor = TextMuted)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.btn_start), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { scope.launch { store.setEnabled(false) } },
                            enabled = enabled,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(if (enabled) listOf(DangerRed.copy(alpha = 0.6f), DangerRed.copy(alpha = 0.3f)) else listOf(BorderSubtle, BorderSubtle))),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed, disabledContentColor = TextMuted)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.btn_stop), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Text(stringResource(R.string.section_system_access), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp, modifier = Modifier.padding(top = 4.dp))

            AccessRow(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.access_notifications_title),
                subtitle = stringResource(R.string.access_notifications_subtitle),
                onClick = { ctx.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
            )

            AccessRow(
                icon = Icons.Default.BatteryChargingFull,
                title = stringResource(R.string.access_battery_title),
                subtitle = stringResource(R.string.access_battery_subtitle),
                onClick = { ctx.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) }
            )

            Spacer(Modifier.height(4.dp))
            OutlinedButton(
                onClick = { nav.navigate(Screen.About.route) },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(10.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle))),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.btn_about), fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun AccessRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceCard).border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))) {
        TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(4.dp), shape = RoundedCornerShape(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceDark), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(subtitle, color = TextSecondary, fontSize = 11.sp)
                }
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
            }
        }
    }
}

private fun drawGrid(scope: DrawScope) {
    val step = 48.dp.value * scope.density
    val color = Color(0xFF0D1A24)
    var x = 0f
    while (x < scope.size.width) { scope.drawLine(color, Offset(x, 0f), Offset(x, scope.size.height), strokeWidth = 1f); x += step }
    var y = 0f
    while (y < scope.size.height) { scope.drawLine(color, Offset(0f, y), Offset(scope.size.width, y), strokeWidth = 1f); y += step }
}
