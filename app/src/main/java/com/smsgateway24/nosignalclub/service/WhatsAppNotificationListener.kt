package com.smsgateway24.nosignalclub.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.smsgateway24.nosignalclub.data.SettingsStore
import com.smsgateway24.nosignalclub.sms.SmsSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.app.Notification
import android.os.Bundle
import java.util.concurrent.ConcurrentHashMap

class WhatsAppNotificationListener : NotificationListenerService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    // WhatsApp package names (consumer + business)
    private val waPackages = setOf("com.whatsapp", "com.whatsapp.w4b")


    private val recent = ConcurrentHashMap<String, Long>()
    private val dedupeWindowMs = 60_000L // 60 seconds, for safety

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName ?: return
        if (pkg !in waPackages) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString().orEmpty()
        val extracted = extractWhatsAppMessage(extras)

        val content = extracted.text

        if (title.isBlank() && content.isBlank()) return
        if (extracted.isSummaryOnly) return  // skip "X new messages" noise

        // Filter out empty or ongoing system-like notifications
        if (title.isBlank() && content.isBlank()) return

        scope.launch {
            val store = SettingsStore(applicationContext)
            val enabled = store.enabledFlow.first()
            if (!enabled) return@launch

            val targetNumber = store.targetNumberFlow.first()
            if (targetNumber.isBlank()) return@launch

            val normalized = normalizeMessage(title, content)
            if (normalized.isBlank()) return@launch

            val key = messageKey(normalized)
            if (!shouldSendNow(key)) return@launch

            SmsSender.sendSms(applicationContext, targetNumber, normalized)
        }
    }


    // English comments as requested
    private fun buildDedupeKey(sbn: StatusBarNotification, message: String): String {
        // sbn.key is stable for the notification instance; message guards against updates
        return "${sbn.key}::${message.trim()}"
    }

    // English comments as requested
    private fun isDuplicate(key: String): Boolean {
        val now = System.currentTimeMillis()

        // Clean up old entries occasionally (simple & cheap)
        recent.entries.removeIf { now - it.value > dedupeWindowMs }

        val last = recent[key]
        return if (last != null && now - last < dedupeWindowMs) {
            true
        } else {
            recent[key] = now
            false
        }
    }
    // English comments as requested
    private fun messageKey(message: String): String {
        // Normalize whitespace to keep keys stable
        return message.trim().replace(Regex("\\s+"), " ")
    }

    // English comments as requested
    private fun shouldSendNow(key: String): Boolean {
        val now = System.currentTimeMillis()

        // Occasionally clean old entries
        if (recent.size > 500) {
            recent.entries.removeIf { now - it.value > dedupeWindowMs }
        }

        while (true) {
            val last = recent[key]

            // Duplicate within window -> do not send
            if (last != null && now - last < dedupeWindowMs) return false

            // Try to atomically set the timestamp
            if (last == null) {
                if (recent.putIfAbsent(key, now) == null) return true
            } else {
                if (recent.replace(key, last, now)) return true
            }
            // If CAS failed, loop and retry
        }
    }

    private fun normalizeMessage(title: String, content: String): String {
        // English comments as requested
        // Avoid duplicating title when content already includes the same sender/chat label.
        val t = title.trim()
        val c = content.trim()

        val combined = when {
            t.isBlank() -> c
            c.isBlank() -> t
            c.startsWith(t) -> c // e.g. "+49..: Ok" already contains title/sender
            c.startsWith("$t:") -> c
            c.startsWith("$t —") -> c
            else -> "$t — $c"
        }

        val lower = combined.lowercase()
        val isCall = lower.contains("calling") ||
                lower.contains("звон") ||
                lower.contains("voice call") ||
                lower.contains("video call")

        return if (isCall) {
            "WA: входящий звонок — $combined"
        } else {
            "WA: $combined"
        }
    }

    // English comments as requested
    private data class ExtractedMessage(
        val title: String,
        val text: String,
        val isSummaryOnly: Boolean
    )

    private fun extractWhatsAppMessage(extras: Bundle): ExtractedMessage {
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()

        // 1) Try MessagingStyle payload: android.messages
        val messages = extras.getParcelableArray(Notification.EXTRA_MESSAGES)
        if (!messages.isNullOrEmpty()) {
            // Each item is a Bundle with keys like "text", "sender", "time"
            val last = messages.lastOrNull() as? Bundle
            val msgText = last?.getCharSequence("text")?.toString().orEmpty()
            val sender = last?.getCharSequence("sender")?.toString().orEmpty()

            val combined = when {
                sender.isNotBlank() && msgText.isNotBlank() -> "$sender: $msgText"
                msgText.isNotBlank() -> msgText
                else -> ""
            }

            if (combined.isNotBlank()) {
                return ExtractedMessage(
                    title = title,
                    text = combined,
                    isSummaryOnly = false
                )
            }
        }

        // 2) Fallback: normal text / bigText
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()

        val content = when {
            bigText.isNotBlank() -> bigText
            text.isNotBlank() -> text
            else -> ""
        }

        // 3) Detect summary like "4 new messages" / "4 новых сообщений"
        val isSummary = isLikelySummary(content)

        return ExtractedMessage(
            title = title,
            text = content,
            isSummaryOnly = isSummary && content.isNotBlank()
        )
    }

    // English comments as requested
    private fun isLikelySummary(content: String): Boolean {
        val s = content.trim().lowercase()

        // Common summary patterns (EN/RU/DE)
        if (Regex("""^\d+\s+(new\s+messages|unread\s+messages)$""").matches(s)) return true
        if (Regex("""^\d+\s+нов(ых|ые|ое)\s+сообщени(й|я|е)$""").matches(s)) return true
        if (Regex("""^\d+\s+neue\s+nachrichten$""").matches(s)) return true

        // WhatsApp sometimes uses shorter variants
        if (s.contains("нов") && s.contains("сообщ")) {
            if (Regex(""".*\b\d+\b.*""").containsMatchIn(s)) return true
        }

        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}