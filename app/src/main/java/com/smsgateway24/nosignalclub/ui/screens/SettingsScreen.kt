package com.smsgateway24.nosignalclub.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.smsgateway24.nosignalclub.R
import com.smsgateway24.nosignalclub.data.SettingsStore
import com.smsgateway24.nosignalclub.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(nav: NavController) {
    val ctx = LocalContext.current
    val store = remember { SettingsStore(ctx) }
    val scope = rememberCoroutineScope()

    val savedNumber by store.targetNumberFlow.collectAsState(initial = "")
    var number by remember(savedNumber) { mutableStateOf(savedNumber) }
    var saved by remember { mutableStateOf(false) }

    var receiveSmsGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }

    var sendSmsGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        receiveSmsGranted = permissions[Manifest.permission.RECEIVE_SMS] == true ||
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        sendSmsGranted = permissions[Manifest.permission.SEND_SMS] == true ||
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Back + title
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { nav.popBackStack() },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(stringResource(R.string.settings_title), color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.settings_subtitle), color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            // PHONE NUMBER
            Text(stringResource(R.string.section_phone_number), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .border(1.dp, if (number.isNotBlank()) NeonGreen.copy(alpha = 0.3f) else BorderSubtle, RoundedCornerShape(12.dp))
            ) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it; saved = false },
                    placeholder = { Text(stringResource(R.string.number_placeholder), color = TextMuted, fontFamily = FontFamily.Monospace) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = if (number.isNotBlank()) NeonGreen else TextMuted, modifier = Modifier.size(18.dp))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = NeonGreen,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 16.sp)
                )
            }

            Text(stringResource(R.string.number_hint), color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)

            Button(
                onClick = { scope.launch { store.setTargetNumber(number.trim()); saved = true } },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DeepBlack)
            ) {
                if (saved) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_saved), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_save), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            // SMS PERMISSIONS
            Text("SMS PERMISSIONS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PermissionRow(title = "Receive SMS", granted = receiveSmsGranted)
                    PermissionRow(title = "Send SMS", granted = sendSmsGranted)
                    Button(
                        onClick = {
                            smsPermissionLauncher.launch(arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS))
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = TextPrimary)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Request SMS permissions", fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // SMS COMMANDS reference
            Text(stringResource(R.string.section_sms_commands), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Message, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp).padding(top = 1.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.sms_commands_hint), color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
                    }
                    HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                    CommandRow(cmd = "start",    desc = stringResource(R.string.cmd_start_desc))
                    CommandRow(cmd = "stop",     desc = stringResource(R.string.cmd_stop_desc))
                    CommandRow(cmd = "start wa", desc = stringResource(R.string.cmd_start_wa_desc))
                    CommandRow(cmd = "stop wa",  desc = stringResource(R.string.cmd_stop_wa_desc))
                    CommandRow(cmd = "start tg", desc = stringResource(R.string.cmd_start_tg_desc))
                    CommandRow(cmd = "stop tg",  desc = stringResource(R.string.cmd_stop_tg_desc))
                }
            }
        }
    }
}

@Composable
private fun CommandRow(cmd: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = cmd,
            color = NeonGreen,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(80.dp)
        )
        Text(text = "—  $desc", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PermissionRow(title: String, granted: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (granted) Icons.Default.Check else Icons.Default.Lock,
            contentDescription = null,
            tint = if (granted) NeonGreen else TextMuted,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(text = title, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(
            text = if (granted) "Granted" else "Not granted",
            color = if (granted) NeonGreen else TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
