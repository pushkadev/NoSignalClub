package com.smsgateway24.nosignalclub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
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
                    Text("Настройки", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Конфигурация шлюза", color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "НОМЕР ТЕЛЕФОНА",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            // Custom styled TextField
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .border(
                        1.dp,
                        if (number.isNotBlank()) NeonGreen.copy(alpha = 0.3f) else BorderSubtle,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                OutlinedTextField(
                    value = number,
                    onValueChange = {
                        number = it
                        saved = false
                    },
                    placeholder = { Text("+49...", color = TextMuted, fontFamily = FontFamily.Monospace) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = if (number.isNotBlank()) NeonGreen else TextMuted, modifier = Modifier.size(18.dp))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = NeonGreen,
                        focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 16.sp)
                )
            }

            Text(
                "SMS будет отправлено на этот номер при получении WhatsApp уведомления",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(4.dp))

            // Save button
            Button(
                onClick = {
                    scope.launch {
                        store.setTargetNumber(number.trim())
                        saved = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen,
                    contentColor = DeepBlack
                )
            ) {
                if (saved) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Сохранено", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Сохранить", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}