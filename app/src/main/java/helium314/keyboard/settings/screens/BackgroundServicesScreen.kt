// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.settings.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import helium314.keyboard.latin.R
import helium314.keyboard.latin.settings.Defaults
import helium314.keyboard.latin.settings.Settings
import helium314.keyboard.latin.utils.prefs
import helium314.keyboard.settings.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundServicesScreen(
    onClickBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.prefs() }

    var spellCheckerEnabled by remember {
        mutableStateOf(prefs.getBoolean(Settings.PREF_ENABLE_SPELL_CHECKER_SERVICE, Defaults.PREF_ENABLE_SPELL_CHECKER_SERVICE))
    }
    var clipboardEnabled by remember {
        mutableStateOf(prefs.getBoolean(Settings.PREF_ENABLE_CLIPBOARD_LISTENER, Defaults.PREF_ENABLE_CLIPBOARD_LISTENER))
    }
    var appSyncEnabled by remember {
        mutableStateOf(prefs.getBoolean(Settings.PREF_USE_APPS, Defaults.PREF_USE_APPS))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.background_services_title)) },
                navigationIcon = { BackButton(onClickBack) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.background_services_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 1. Spell Checker Service
            CompactServiceCard(
                title = stringResource(R.string.bg_service_spellchecker_title),
                description = stringResource(R.string.bg_service_spellchecker_desc),
                status = if (spellCheckerEnabled) stringResource(R.string.status_active) else stringResource(R.string.status_disabled),
                enabled = spellCheckerEnabled,
                onToggle = { enabled ->
                    spellCheckerEnabled = enabled
                    prefs.edit().putBoolean(Settings.PREF_ENABLE_SPELL_CHECKER_SERVICE, enabled).apply()
                },
                onStopClicked = {
                    spellCheckerEnabled = false
                    prefs.edit().putBoolean(Settings.PREF_ENABLE_SPELL_CHECKER_SERVICE, false).apply()
                    Toast.makeText(context, context.getString(R.string.bg_service_spellchecker_stopped), Toast.LENGTH_SHORT).show()
                }
            )

            // 2. Clipboard History Listener
            CompactServiceCard(
                title = stringResource(R.string.bg_service_clipboard_title),
                description = stringResource(R.string.bg_service_clipboard_desc),
                status = if (clipboardEnabled) stringResource(R.string.status_listening) else stringResource(R.string.status_disabled),
                enabled = clipboardEnabled,
                onToggle = { enabled ->
                    clipboardEnabled = enabled
                    prefs.edit().putBoolean(Settings.PREF_ENABLE_CLIPBOARD_LISTENER, enabled).apply()
                },
                onStopClicked = {
                    clipboardEnabled = false
                    prefs.edit().putBoolean(Settings.PREF_ENABLE_CLIPBOARD_LISTENER, false).apply()
                    Toast.makeText(context, context.getString(R.string.bg_service_clipboard_stopped), Toast.LENGTH_SHORT).show()
                }
            )

            // 3. App Name Launcher Sync
            CompactServiceCard(
                title = stringResource(R.string.bg_service_appsync_title),
                description = stringResource(R.string.bg_service_appsync_desc),
                status = if (appSyncEnabled) stringResource(R.string.status_listening) else stringResource(R.string.status_disabled),
                enabled = appSyncEnabled,
                onToggle = { enabled ->
                    appSyncEnabled = enabled
                    prefs.edit().putBoolean(Settings.PREF_USE_APPS, enabled).apply()
                },
                onStopClicked = {
                    appSyncEnabled = false
                    prefs.edit().putBoolean(Settings.PREF_USE_APPS, false).apply()
                    Toast.makeText(context, context.getString(R.string.bg_service_appsync_stopped), Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun CompactServiceCard(
    title: String,
    description: String,
    status: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onStopClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = " • $status",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            if (enabled) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = onStopClicked,
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(stringResource(R.string.bg_service_stop_free_memory), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

fun createBackgroundServicesSettings(context: android.content.Context): List<helium314.keyboard.settings.Setting> = listOf(
    helium314.keyboard.settings.Setting(
        key = Settings.PREF_ENABLE_SPELL_CHECKER_SERVICE,
        title = context.getString(R.string.bg_service_spellchecker_title),
        description = context.getString(R.string.bg_service_spellchecker_desc)
    ) { setting ->
        var enabled by remember { mutableStateOf(context.prefs().getBoolean(setting.key, Defaults.PREF_ENABLE_SPELL_CHECKER_SERVICE)) }
        helium314.keyboard.settings.preferences.SwitchPreference(
            name = setting.title,
            key = setting.key,
            default = Defaults.PREF_ENABLE_SPELL_CHECKER_SERVICE,
            description = setting.description,
            onCheckedChange = {
                enabled = it
                context.prefs().edit().putBoolean(setting.key, it).apply()
            }
        )
    },
    helium314.keyboard.settings.Setting(
        key = Settings.PREF_ENABLE_CLIPBOARD_LISTENER,
        title = context.getString(R.string.bg_service_clipboard_title),
        description = context.getString(R.string.bg_service_clipboard_desc)
    ) { setting ->
        var enabled by remember { mutableStateOf(context.prefs().getBoolean(setting.key, Defaults.PREF_ENABLE_CLIPBOARD_LISTENER)) }
        helium314.keyboard.settings.preferences.SwitchPreference(
            name = setting.title,
            key = setting.key,
            default = Defaults.PREF_ENABLE_CLIPBOARD_LISTENER,
            description = setting.description,
            onCheckedChange = {
                enabled = it
                context.prefs().edit().putBoolean(setting.key, it).apply()
            }
        )
    },
    helium314.keyboard.settings.Setting(
        key = Settings.PREF_USE_APPS,
        title = context.getString(R.string.bg_service_appsync_title),
        description = context.getString(R.string.bg_service_appsync_desc)
    ) { setting ->
        var enabled by remember { mutableStateOf(context.prefs().getBoolean(setting.key, Defaults.PREF_USE_APPS)) }
        helium314.keyboard.settings.preferences.SwitchPreference(
            name = setting.title,
            key = setting.key,
            default = Defaults.PREF_USE_APPS,
            description = setting.description,
            onCheckedChange = {
                enabled = it
                context.prefs().edit().putBoolean(setting.key, it).apply()
            }
        )
    }
)
