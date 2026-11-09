package helium314.keyboard.latin.gif

import android.content.Context
import com.leanbitlab.leantype.gif.GifItem
import helium314.keyboard.latin.utils.prefs
import org.json.JSONArray
import org.json.JSONObject

object GifRecentManager {
    private const val PREF_GIF_RECENTS = "pref_gif_recent_items"
    private const val MAX_RECENTS = 30

    fun getRecents(context: Context): List<GifItem> {
        val jsonString = context.prefs().getString(PREF_GIF_RECENTS, null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonString)
            val list = mutableListOf<GifItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    GifItem(
                        id = obj.getString("id"),
                        provider = obj.getString("provider"),
                        kind = obj.getInt("kind"),
                        previewUri = obj.getString("previewUri"),
                        width = obj.optInt("width", 200),
                        height = obj.optInt("height", 200),
                        isPack = obj.optBoolean("isPack", false),
                        attribution = obj.optString("attribution").takeIf { it.isNotEmpty() },
                        extra = obj.optString("extra").takeIf { it.isNotEmpty() }
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addRecent(context: Context, item: GifItem) {
        val current = getRecents(context).toMutableList()
        current.removeAll { it.id == item.id }
        current.add(0, item)
        val trimmed = if (current.size > MAX_RECENTS) current.take(MAX_RECENTS) else current

        val array = JSONArray()
        for (it in trimmed) {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("provider", it.provider)
                put("kind", it.kind)
                put("previewUri", it.previewUri)
                put("width", it.width)
                put("height", it.height)
                put("isPack", it.isPack)
                it.attribution?.let { put("attribution", it) }
                it.extra?.let { put("extra", it) }
            }
            array.put(obj)
        }
        context.prefs().edit().putString(PREF_GIF_RECENTS, array.toString()).apply()
    }

    fun clearRecents(context: Context) {
        context.prefs().edit().remove(PREF_GIF_RECENTS).apply()
    }
}
