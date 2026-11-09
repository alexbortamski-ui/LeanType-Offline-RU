// SPDX-License-Identifier: GPL-3.0-only

package helium314.keyboard.latin.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Telephony

/**
 * Utility helper for detecting the system default SMS messaging app.
 */
object SmsPackageProvider {

    /**
     * Resolves the package name of the active default SMS application.
     */
    fun getDefaultSmsPackage(context: Context): String? {
        val telephonyDefault = try {
            Telephony.Sms.getDefaultSmsPackage(context)
        } catch (e: Exception) {
            null
        }
        if (!telephonyDefault.isNullOrBlank()) {
            return telephonyDefault
        }
        return try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"))
            val resolveInfo = context.packageManager.resolveActivity(intent, 0)
            val pkg = resolveInfo?.activityInfo?.packageName
            if (pkg != null && pkg != "android") pkg else null
        } catch (e: Exception) {
            null
        }
    }
}
