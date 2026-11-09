package helium314.keyboard.latin.provider

import android.net.Uri
import androidx.core.content.FileProvider

class KeyboardFileProvider : FileProvider() {
    override fun getType(uri: Uri): String? {
        val path = uri.path.orEmpty()
        if (path.endsWith(".wasticker")) {
            return "image/webp.wasticker"
        }
        return super.getType(uri)
    }
}
