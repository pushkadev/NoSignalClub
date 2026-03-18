package com.smsgateway24.nosignalclub.sms

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.core.content.ContextCompat
import com.smsgateway24.nosignalclub.data.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SmsCommandReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SmsCommandReceiver", "onReceive called, action=${intent.action}")

        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED

        Log.d("SmsCommandReceiver", "RECEIVE_SMS granted=$hasPermission")

        if (!hasPermission) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                Log.d("SmsCommandReceiver", "messages count=${messages?.size ?: 0}")

                if (messages.isNullOrEmpty()) return@launch

                val sender = messages.firstOrNull()?.originatingAddress.orEmpty().trim()
                val body = messages.joinToString(separator = "") { it.messageBody.orEmpty() }
                    .trim()
                    .lowercase()

                Log.d("SmsCommandReceiver", "sender=[$sender]")
                Log.d("SmsCommandReceiver", "body=[$body]")

                if (sender.isBlank() || body.isBlank()) return@launch

                val store = SettingsStore(appContext)
                val targetNumber = store.targetNumberFlow.first()

                Log.d("SmsCommandReceiver", "targetNumber=[$targetNumber]")

                val sameNumber = isSamePhoneNumber(sender, targetNumber)
                Log.d("SmsCommandReceiver", "sameNumber=$sameNumber")

                if (!sameNumber) return@launch

                when (body) {
                    "start" -> {
                        Log.d("SmsCommandReceiver", "Command START received")
                        store.setEnabled(true)
                    }
                    "stop" -> {
                        Log.d("SmsCommandReceiver", "Command STOP received")
                        store.setEnabled(false)
                    }
                    else -> {
                        Log.d("SmsCommandReceiver", "Unknown command")
                    }
                }
            } catch (e: Exception) {
                Log.e("SmsCommandReceiver", "Receiver error", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun isSamePhoneNumber(incoming: String, configured: String): Boolean {
        if (incoming.isBlank() || configured.isBlank()) return false
        return PhoneNumberUtils.compare(incoming, configured)
    }
}