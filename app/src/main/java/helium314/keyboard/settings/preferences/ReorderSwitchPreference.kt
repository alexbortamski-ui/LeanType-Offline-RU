// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.settings.preferences

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import helium314.keyboard.keyboard.KeyboardSwitcher
import helium314.keyboard.keyboard.internal.KeyboardIconsSet
import helium314.keyboard.latin.R
import helium314.keyboard.latin.common.Constants.Separators
import helium314.keyboard.latin.utils.getStringResourceOrName
import helium314.keyboard.latin.utils.prefs
import helium314.keyboard.settings.Setting
import helium314.keyboard.settings.dialogs.ReorderDialog
import helium314.keyboard.settings.screens.GetIcon
import androidx.compose.runtime.Immutable
import androidx.core.content.edit

@Composable
fun ReorderSwitchPreference(
    setting: Setting,
    default: String,
    filter: (String) -> Boolean = { true },
    enabled: Boolean = true,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    Preference(
        name = setting.title,
        description = setting.description,
        enabled = enabled,
        onClick = { showDialog = true },
    )
    if (showDialog) {
        val ctx = LocalContext.current
        val prefs = ctx.prefs()
        val items = remember(setting.key) {
            val raw = prefs.getString(setting.key, default) ?: default
            val normalized = if (raw.contains(";") || raw.contains(",")) {
                raw.replace(";", Separators.ENTRY).replace(",", Separators.KV)
            } else raw
            normalized.split(Separators.ENTRY)
                .filter { it.contains(Separators.KV) }
                .map {
                    val both = it.split(Separators.KV)
                    KeyAndState(both.first(), both.last().toBoolean())
                }
                .distinctBy { it.name }
                .filter { filter(it.name) }
        }
        ReorderDialog(
            onConfirmed = { reorderedItems ->
                val raw = prefs.getString(setting.key, default) ?: default
                val normalized = if (raw.contains(";") || raw.contains(",")) {
                    raw.replace(";", Separators.ENTRY).replace(",", Separators.KV)
                } else raw
                val allRawEntries = normalized.split(Separators.ENTRY)
                    .filter { it.contains(Separators.KV) }
                val reorderedNames = reorderedItems.map { it.name }.toSet()
                val hiddenEntries = allRawEntries
                    .filterNot { filter(it.split(Separators.KV).first()) }
                    .filterNot { it.split(Separators.KV).first() in reorderedNames }
                    .distinctBy { it.split(Separators.KV).first() }
                val reorderedEntries = reorderedItems.map { it.name + Separators.KV + it.state }
                val value = (reorderedEntries + hiddenEntries).joinToString(Separators.ENTRY)
                prefs.edit { putString(setting.key, value) }
                KeyboardSwitcher.getInstance().setThemeNeedsReload()
            },
            onDismissRequest = { showDialog = false },
            onNeutral = { prefs.edit { remove(setting.key)} },
            neutralButtonText = if (prefs.contains(setting.key)) stringResource(R.string.button_default) else null,
            items = items,
            title = { Text(setting.title) },
            displayItem = { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KeyboardIconsSet.instance.GetIcon(item.name)
                    val customTag = if (item.name.startsWith("CUSTOM_AI_")) {
                        val index = item.name.removePrefix("CUSTOM_AI_").toIntOrNull()
                        if (index != null) prefs.getString("pref_custom_ai_tag_$index", "") ?: "" else ""
                    } else ""
                    val actualText = if (customTag.isNotBlank()) {
                        "$customTag (${item.name.lowercase().getStringResourceOrName("", ctx)})"
                    } else {
                        val text = item.name.lowercase().getStringResourceOrName("", ctx)
                        if (text != item.name.lowercase()) text
                        else item.name.lowercase().getStringResourceOrName("popup_keys_", ctx)
                    }
                    Text(actualText, Modifier.weight(1f))
                    key(item.name) {
                        Switch(
                            checked = item.state,
                            onCheckedChange = {
                                item.state = it
                            }
                        )
                    }
                }
            },
            getKey = { it.name }
        )
    }
}

private class KeyAndState(val name: String, initialState: Boolean) {
    var state by mutableStateOf(initialState)
}
