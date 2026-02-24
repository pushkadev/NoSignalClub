package com.smsgateway24.nosignalclub.sms

import android.content.Context
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

// English comments as requested
object SmsSender {

    fun sendSms(context: Context, to: String, message: String) {
        if (to.isBlank()) return

        // SmsManager can split long messages automatically
        val sms = context.getSystemService(SmsManager::class.java)
        val parts = sms.divideMessage(message)
        sms.sendMultipartTextMessage(to, null, parts, null, null)
    }
}