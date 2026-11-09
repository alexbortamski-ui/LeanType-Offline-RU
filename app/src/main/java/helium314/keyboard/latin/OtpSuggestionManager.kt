// SPDX-License-Identifier: GPL-3.0-only

package helium314.keyboard.latin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import helium314.keyboard.compat.isDeviceLocked
import helium314.keyboard.event.HapticEvent
import helium314.keyboard.keyboard.internal.keyboard_parser.floris.KeyCode
import helium314.keyboard.latin.common.ColorType
import helium314.keyboard.latin.databinding.OtpSuggestionBinding
import helium314.keyboard.latin.settings.Defaults
import helium314.keyboard.latin.settings.Settings
import helium314.keyboard.latin.utils.Log
import helium314.keyboard.latin.utils.prefs
import helium314.keyboard.latin.utils.ToolbarKey

/**
 * Optional, opt-in helper that surfaces one-time passcodes (OTPs) from incoming SMS as a
 * suggestion-strip chip the user can tap to insert (similar to the clipboard/screenshot
 * suggestions, see [ClipboardHistoryManager.getClipboardSuggestionView]).
 *
 * Privacy: OTPs are extracted from incoming SMS broadcasts only when enabled in Settings.
 * Codes remain in process memory and are cleared after insertion, dismissal, or two minutes.
 */
class OtpSuggestionManager(private val latinIME: LatinIME) {

    private val mainHandler = Handler(Looper.getMainLooper())

    fun start() {
        activeInstance = this
        val expired: OtpStateStore.Snapshot?
        synchronized(expirationLock) {
            val snapshot = stateStore.current()
            expired = if (snapshot != null) scheduleExpirationLocked(snapshot) else {
                cancelExpirationLocked()
                null
            }
        }
        expired?.let { logDiagnostic(diagnosticContextForEvents(), "otp_expired", it.generation) }
        logDiagnostic(latinIME, "ime_open", stateStore.currentGeneration())
        latinIME.refreshOtpSuggestion()
    }

    fun stop() {
        if (activeInstance === this) {
            activeInstance = null
        }
        logDiagnostic(latinIME, "ime_close", stateStore.currentGeneration())
    }

    /**
     * Build the OTP suggestion chip if a recent code is available, else null.
 * Called from [LatinIME.refreshOtpSuggestion].
     */
    fun getOtpSuggestionView(parent: ViewGroup?): View? {
        if (parent == null) return null
        val settings = latinIME.mSettings.current
        if (!latinIME.prefs().getBoolean(Settings.PREF_AUTO_READ_OTP, Defaults.PREF_AUTO_READ_OTP)) return null
        if (ContextCompat.checkSelfPermission(latinIME, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) return null
        if (isDeviceLocked(latinIME) || settings.mIncognitoModeEnabled) return null
        val snapshot = stateStore.current() ?: return null
        val otp = snapshot.code ?: return null
        val diagnosticMode = isDiagnosticModeEnabled(latinIME)

        val binding = OtpSuggestionBinding.inflate(LayoutInflater.from(latinIME), parent, false)
        val textView = binding.otpSuggestionText
        latinIME.mSettings.getCustomTypeface()?.let { textView.typeface = it }
        textView.text = if (diagnosticMode) latinIME.getString(R.string.otp_local_diagnostic_label, otp) else otp
        textView.contentDescription = latinIME.getString(R.string.insert_otp_suggestion, otp)
        textView.setOnClickListener {
            val codeToInsert = consumeOtp(snapshot.generation) ?: run {
                refreshActiveOtpSuggestion()
                return@setOnClickListener
            }
            latinIME.onTextInput(codeToInsert)
            AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(KeyCode.NOT_SPECIFIED, it, HapticEvent.KEY_PRESS)
            binding.root.isGone = true
        }
        val closeButton = binding.otpSuggestionClose
        closeButton.setImageDrawable(latinIME.mKeyboardSwitcher.keyboard?.mIconsSet?.getIconDrawable(ToolbarKey.CLOSE_HISTORY.name.lowercase()))
        closeButton.setOnClickListener {
            AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(KeyCode.NOT_SPECIFIED, it, HapticEvent.KEY_PRESS)
            binding.root.isGone = true
            if (!clearOtp(reason = "dismiss", expectedGeneration = snapshot.generation)) {
                refreshActiveOtpSuggestion()
            }
        }

        val colors = latinIME.mSettings.current.mColors
        textView.setTextColor(colors.get(ColorType.KEY_TEXT))
        colors.setColor(closeButton, ColorType.REMOVE_SUGGESTION_ICON)
        colors.setBackground(binding.root, ColorType.CLIPBOARD_SUGGESTION_BACKGROUND)

        return binding.root
    }

    companion object {
        private const val TAG = "OtpLocalDiag"

        private val codePattern = Regex("(?<![0-9])([0-9]{4,8})(?![0-9])")
        private val urlPattern = Regex("(?i)(https?://\\S+|www\\.\\S+)")
        private val webOtpPattern = Regex("(?:^|\\s)@[\\w.-]+\\s+#([0-9]{4,8})\\b")
        private val explicitCodePattern = Regex(
            "(?iu)\\b(?:otp|verification\\s+code|verify(?: your)?|passcode|pin|security\\s+code|login\\s+code|code|one[- ]?time\\s+(?:password|code))\\b" +
                "(?:\\s+(?:is|was))?[\\s:=#-]{0,12}([0-9]{4,8})\\b"
        )
        private val reverseCodePattern = Regex(
            "(?iu)\\b([0-9]{4,8})\\b[\\s,:=-]+(?:is\\s+)?(?:your\\s+)?(?:[\\w-]{1,15}\\s+){0,4}(?:otp|verification\\s+code|security\\s+code|login\\s+code|passcode)\\b"
        )
        private val indicatorPattern = Regex(
            "(?iu)(?:\\b(?:otp|verification|verify|passcode|pin|security code|login code|one[- ]?time(?: password| code)?)\\b|" +
                "однораз\\p{L}*|подтверж\\p{L}*|провер\\p{L}*|" +
                "код.{0,24}(?:вход|подтверж)|(?:вход|подтверж).{0,24}код)"
        )
        private val amountPattern = Regex("(?iu)(?:[$€£₽]|\\b(?:руб(?:лей|ля|\\.)?|usd|eur|inr)\\b)")

        private val stateStore = OtpStateStore({ SystemClock.elapsedRealtime() })
        @Volatile private var activeInstance: OtpSuggestionManager? = null
        @Volatile private var diagnosticContext: Context? = null
        @Volatile private var localOtpAutofillSuppressionUntilElapsedRealtime = 0L
        private val expirationHandler by lazy { Handler(Looper.getMainLooper()) }
        private val expirationLock = Any()
        private var expirationRunnable: Runnable? = null
        private var expirationGeneration: Long? = null

        /** Caller holds [expirationLock], serializing timer changes with every state mutation. */
        private fun scheduleExpirationLocked(snapshot: OtpStateStore.Snapshot): OtpStateStore.Snapshot? {
            val remaining = stateStore.remainingMillis(snapshot.generation) ?: return null
            if (remaining == 0L) {
                val cleared = stateStore.expire(snapshot.generation)
                if (cleared != null) cancelExpirationLocked(snapshot.generation)
                return cleared
            }

            cancelExpirationLocked()
            val runnable = object : Runnable {
                override fun run() {
                    val cleared: OtpStateStore.Snapshot?
                    synchronized(expirationLock) {
                        cleared = stateStore.expire(snapshot.generation)
                        if (cleared != null) {
                            clearScheduledExpirationLocked(this)
                        } else {
                            val nextDelay = stateStore.remainingMillis(snapshot.generation)
                            if (nextDelay != null && nextDelay > 0L) {
                                expirationHandler.postDelayed(this, nextDelay)
                            } else {
                                clearScheduledExpirationLocked(this)
                            }
                        }
                    }
                    cleared?.let {
                        logDiagnostic(diagnosticContextForEvents(), "otp_expired", it.generation)
                        notifyActiveIme(it.generation)
                    }
                }
            }
            expirationRunnable = runnable
            expirationGeneration = snapshot.generation
            expirationHandler.postDelayed(runnable, remaining)
            return null
        }

        private fun clearScheduledExpirationLocked(runnable: Runnable) {
            if (expirationRunnable === runnable) {
                expirationRunnable = null
                expirationGeneration = null
            }
        }

        private fun cancelExpirationLocked(expectedGeneration: Long? = null) {
            if (expectedGeneration != null && expirationGeneration != expectedGeneration) return
            expirationRunnable?.let(expirationHandler::removeCallbacks)
            expirationRunnable = null
            expirationGeneration = null
        }

        private fun notifyActiveIme(expectedGeneration: Long) {
            val instance = activeInstance ?: return
            val refresh = Runnable {
                if (instance === activeInstance && stateStore.currentGeneration() == expectedGeneration) {
                    logDiagnostic(instance.latinIME, "otp_ui_refresh", expectedGeneration)
                    instance.latinIME.refreshOtpSuggestion()
                }
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                refresh.run()
            } else {
                instance.mainHandler.post(refresh)
            }
        }

        /**
         * Called by [SmsOtpReceiver] after parsing an incoming SMS broadcast.
         */
        fun onOtpReceived(context: Context, otp: String, source: String = "sms") {
            diagnosticContext = context.applicationContext
            val snapshot: OtpStateStore.Snapshot
            val expired: OtpStateStore.Snapshot?
            synchronized(expirationLock) {
                snapshot = stateStore.publish(otp)
                expired = scheduleExpirationLocked(snapshot)
            }
            logDiagnostic(context, "otp_replaced", snapshot.generation, source)
            if (expired != null) {
                logDiagnostic(context, "otp_expired", expired.generation)
                notifyActiveIme(expired.generation)
            } else {
                notifyActiveIme(snapshot.generation)
            }
        }

        /** Clear the transient code after insertion, dismissal, or when the feature is disabled. */
        fun clearOtp(reason: String = "clear", expectedGeneration: Long? = null): Boolean {
            val cleared: OtpStateStore.Snapshot
            synchronized(expirationLock) {
                cleared = stateStore.clear(expectedGeneration) ?: return false
                cancelExpirationLocked(expectedGeneration)
                if (reason == "feature_disabled" || reason == "permission_revoked") {
                    localOtpAutofillSuppressionUntilElapsedRealtime = 0L
                }
            }
            logDiagnostic(diagnosticContextForEvents(), "otp_cleared", cleared.generation, reason)
            notifyActiveIme(cleared.generation)
            return true
        }

        private fun consumeOtp(expectedGeneration: Long): String? {
            val code: String
            val emptyGeneration: Long
            synchronized(expirationLock) {
                code = stateStore.consume(expectedGeneration) ?: return null
                cancelExpirationLocked(expectedGeneration)
                emptyGeneration = stateStore.currentGeneration()
            }
            logDiagnostic(diagnosticContextForEvents(), "otp_insert", expectedGeneration)
            notifyActiveIme(emptyGeneration)
            return code
        }

        fun refreshActiveOtpSuggestion() {
            notifyActiveIme(stateStore.currentGeneration())
        }

        fun markLocalOtpPresented() {
            synchronized(expirationLock) {
                val snapshot = stateStore.current() ?: return
                localOtpAutofillSuppressionUntilElapsedRealtime = maxOf(
                    localOtpAutofillSuppressionUntilElapsedRealtime,
                    snapshot.expiresAtElapsedRealtime,
                )
            }
        }

        fun shouldSuppressSystemSmsOtpAutofill(): Boolean =
            SystemClock.elapsedRealtime() < localOtpAutofillSuppressionUntilElapsedRealtime

        private fun diagnosticContextForEvents(): Context? = activeInstance?.latinIME ?: diagnosticContext

        fun isDiagnosticModeEnabled(context: Context): Boolean {
            return BuildConfig.DEBUG && context.prefs().getBoolean(
                Settings.PREF_OTP_LOCAL_DIAGNOSTICS,
                Defaults.PREF_OTP_LOCAL_DIAGNOSTICS,
            )
        }

        fun logDiagnostic(
            context: Context?,
            event: String,
            generation: Long? = null,
            detail: String? = null,
        ) {
            if (context == null || !isDiagnosticModeEnabled(context)) return
            val generationValue = generation?.toString() ?: "-"
            val detailValue = detail?.let { " detail=$it" } ?: ""
            Log.i(TAG, "event=$event generation=$generationValue elapsedRealtimeMs=${SystemClock.elapsedRealtime()}$detailValue")
        }

        /**
         * Extract one unambiguous numeric OTP from message text with nearby code context.
         */
        fun extractOtp(body: String): String? {
            if (body.isBlank()) return null
            val text = urlPattern.replace(body, " ")
            val webOtpCandidates = webOtpPattern.findAll(text).map { it.groupValues[1] }.distinct().toList()
            if (webOtpCandidates.isNotEmpty()) return webOtpCandidates.singleOrNull()
            val explicitCandidates = explicitCodePattern.findAll(text).map { it.groupValues[1] }.distinct().toList()
            if (explicitCandidates.isNotEmpty()) return explicitCandidates.singleOrNull()
            val reverseCandidates = reverseCodePattern.findAll(text).map { it.groupValues[1] }.distinct().toList()
            if (reverseCandidates.isNotEmpty()) return reverseCandidates.singleOrNull()
            val candidates = codePattern.findAll(text)
                .filter { match ->
                    val start = match.range.first
                    val endExclusive = match.range.last + 1
                    val code = match.groupValues[1]
                    val afterCode = text.substring(endExclusive, (endExclusive + 8).coerceAtMost(text.length))
                    if (code.length == 4 && code.startsWith("20") && Regex("^[-/.][0-9]{1,2}(?:[-/.][0-9]{1,2})?").containsMatchIn(afterCode)) {
                        return@filter false
                    }
                    val context = text.substring((start - 40).coerceAtLeast(0), (endExclusive + 40).coerceAtMost(text.length))
                    if (!indicatorPattern.containsMatchIn(context)) return@filter false
                    val adjacent = text.substring((start - 8).coerceAtLeast(0), (endExclusive + 8).coerceAtMost(text.length))
                    !amountPattern.containsMatchIn(adjacent)
                }
                .map { it.groupValues[1] }
                .distinct()
                .toList()
            return candidates.singleOrNull()
        }
    }
}
