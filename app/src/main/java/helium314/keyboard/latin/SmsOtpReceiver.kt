// SPDX-License-Identifier: GPL-3.0-only

package helium314.keyboard.latin

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import androidx.core.content.ContextCompat
import helium314.keyboard.latin.settings.Defaults
import helium314.keyboard.latin.settings.Settings
import helium314.keyboard.latin.utils.prefs

/** Receives new SMS broadcasts only; it never queries the SMS provider or logs message data. */
class SmsOtpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        OtpSuggestionManager.logDiagnostic(context, "sms_broadcast_received")
        if (!context.prefs().getBoolean(Settings.PREF_AUTO_READ_OTP, Defaults.PREF_AUTO_READ_OTP)) {
            OtpSuggestionManager.logDiagnostic(context, "sms_ignored", detail = "feature_disabled")
            return
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            OtpSuggestionManager.logDiagnostic(context, "sms_ignored", detail = "permission_missing")
            return
        }

        val messages = try {
            Telephony.Sms.Intents.getMessagesFromIntent(intent)
        } catch (_: Exception) {
            OtpSuggestionManager.logDiagnostic(context, "sms_pdu_parse_error")
            return
        } ?: run {
            OtpSuggestionManager.logDiagnostic(context, "sms_pdu_missing")
            return
        }
        if (messages.isEmpty()) {
            OtpSuggestionManager.logDiagnostic(context, "sms_pdu_empty")
            return
        }
        OtpSuggestionManager.logDiagnostic(context, "sms_pdu_received", detail = "segments=${messages.size}")

        // Keep multipart content local to this call and publish only an extracted code.
        val body = messages.joinToString(separator = "") { it.messageBody.orEmpty() }
        val otp = OtpSuggestionManager.extractOtp(body)
        if (otp == null) {
            OtpSuggestionManager.logDiagnostic(context, "sms_parse_result", detail = "no_candidate")
        } else {
            OtpSuggestionManager.logDiagnostic(context, "sms_parse_result", detail = "candidate")
            OtpSuggestionManager.onOtpReceived(context, otp)
        }
    }
}
