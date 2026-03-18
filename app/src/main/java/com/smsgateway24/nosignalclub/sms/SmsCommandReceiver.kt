package com.smsgateway24.nosignalclub.sms

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import android.telephony.PhoneNumberUtils
import android.telephony.SmsManager
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

        val hasReceivePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED

        Log.d("SmsCommandReceiver", "RECEIVE_SMS granted=$hasReceivePermission")

        if (!hasReceivePermission) return

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
                        store.setEnabled(true)
                        sendReplySms(appContext, sender, "Service started")
                        Log.d("SmsCommandReceiver", "Command START processed")
                    }
                    "stop" -> {
                        store.setEnabled(false)
                        sendReplySms(appContext, sender, "Service stopped")
                        Log.d("SmsCommandReceiver", "Command STOP processed")
                    }
                    else -> {
                        sendReplySms(appContext, sender, "Unknown command")
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

    private fun sendReplySms(context: Context, phoneNumber: String, message: String) {
        val hasSendPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        Log.d("SmsCommandReceiver", "SEND_SMS granted=$hasSendPermission")

        if (!hasSendPermission) {
            Log.d("SmsCommandReceiver", "SEND_SMS permission not granted")
            return
        }

        if (phoneNumber.isBlank() || message.isBlank()) {
            Log.d("SmsCommandReceiver", "Reply SMS skipped because phone number or message is blank")
            return
        }

        SmsManager.getDefault().sendTextMessage(
            phoneNumber,
            null,
            message,
            null,
            null
        )

        Log.d("SmsCommandReceiver", "Reply SMS sent to [$phoneNumber]: [$message]")
    }

    private fun isSamePhoneNumber(incoming: String, configured: String): Boolean {
        if (incoming.isBlank() || configured.isBlank()) return false
        return PhoneNumberUtils.compare(incoming, configured)
    }
}