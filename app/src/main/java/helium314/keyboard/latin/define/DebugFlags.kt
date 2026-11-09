/*
 * Copyright (C) 2014 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */

package helium314.keyboard.latin.define

import android.content.Context
import android.os.Build
import helium314.keyboard.latin.BuildConfig
import helium314.keyboard.latin.settings.DebugSettings
import helium314.keyboard.latin.settings.Defaults
import helium314.keyboard.latin.utils.DeviceProtectedUtils
import helium314.keyboard.latin.utils.Log
import helium314.keyboard.latin.utils.prefs
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DebugFlags {
    var DEBUG_ENABLED = false

    /**
     * Logs per-candidate raw/weighted/boosted scores from all dictionary
     * sources during suggestion computation. Enable ONLY when debugging
     * dictionary scoring or suggestion ranking regressions.
     */
    const val SCORE_AUDIT = false

    fun init(context: Context) {
        DEBUG_ENABLED = BuildConfig.DEBUG || context.prefs().getBoolean(DebugSettings.PREF_DEBUG_MODE, Defaults.PREF_DEBUG_MODE)
        CrashReportExceptionHandler(context.applicationContext).install()
    }
}

// basically copied from StreetComplete
private class CrashReportExceptionHandler(val appContext: Context) : Thread.UncaughtExceptionHandler {
    private var defaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null

    fun install(): Boolean {
        val ueh = Thread.getDefaultUncaughtExceptionHandler()
        if (ueh is CrashReportExceptionHandler)
            return false
        defaultUncaughtExceptionHandler = ueh
        Thread.setDefaultUncaughtExceptionHandler(this)
        return true
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        val stackTrace = StringWriter()

        e.printStackTrace(PrintWriter(stackTrace))
        writeCrashReportToFile("""
Thread: ${t.name}
App version: ${BuildConfig.VERSION_NAME}
Device: ${Build.BRAND} ${Build.DEVICE}, Android ${Build.VERSION.RELEASE}
Locale: ${Locale.getDefault()}
Stack trace:
$stackTrace
Last log:
${Log.getLog(100).joinToString("\n")}
""")
        defaultUncaughtExceptionHandler?.uncaughtException(t, e)
    }

    private fun writeCrashReportToFile(text: String) {
        val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Calendar.getInstance().time)
        try {
            // Use internal private storage - reliable on all Android versions and user profiles,
            // no external storage permissions or IStorageManager.mkdirs() required.
            val dir = appContext.filesDir
            val crashReportFile = File(dir, "crash_report_$date.txt")
            crashReportFile.appendText(text)
        } catch (_: Throwable) {
            // filesDir unavailable (device locked before Direct Boot unlock);
            // fall back to device-protected storage which is always accessible.
            val dir = DeviceProtectedUtils.getFilesDir(appContext) ?: return
            val crashReportFile = File(dir, "crash_report_unprotected_$date.txt")
            crashReportFile.appendText(text)
        }
    }
}
