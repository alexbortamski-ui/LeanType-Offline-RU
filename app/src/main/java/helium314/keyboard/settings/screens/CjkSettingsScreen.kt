// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.settings.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import helium314.keyboard.latin.BuildConfig
import helium314.keyboard.latin.R
import helium314.keyboard.latin.cjk.CjkPluginLoader
import helium314.keyboard.settings.SearchSettingsScreen
import helium314.keyboard.settings.preferences.LoadCjkPluginPreference
import helium314.keyboard.settings.preferences.Preference
import helium314.keyboard.settings.preferences.PreferenceCategory
import helium314.keyboard.settings.preferences.SwitchPreference

@Composable
fun CjkSettingsScreen(
    onClickBack: () -> Unit,
) {
    val context = LocalContext.current
    var cjkInstalled by remember { mutableStateOf(CjkPluginLoader.hasPlugin(context)) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                cjkInstalled = CjkPluginLoader.hasPlugin(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    SearchSettingsScreen(
        onClickBack = onClickBack,
        title = stringResource(R.string.cjk_settings_title),
        settings = emptyList()
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            // Card 1: Plugin Management
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column {
                    PreferenceCategory(stringResource(R.string.cjk_plugin_management))

                    LoadCjkPluginPreference(
                        title = stringResource(R.string.load_cjk_plugin),
                        summary = if (cjkInstalled) stringResource(R.string.libraries_status_active) else stringResource(R.string.libraries_status_not_installed),
                        icon = R.drawable.ic_settings_languages,
                        onSuccess = { cjkInstalled = CjkPluginLoader.hasPlugin(context) }
                    )
                }
            }

            // Card 2: Supported Languages & Engine Status
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column {
                    PreferenceCategory(stringResource(R.string.cjk_supported_languages_title))

                    Preference(
                        name = stringResource(R.string.cjk_lang_chinese_title),
                        description = stringResource(R.string.cjk_lang_chinese_summary),
                        icon = R.drawable.ic_settings_languages,
                        onClick = {}
                    )

                    Preference(
                        name = stringResource(R.string.cjk_lang_japanese_title),
                        description = stringResource(R.string.cjk_lang_japanese_summary),
                        icon = R.drawable.ic_settings_languages,
                        onClick = {}
                    )

                    Preference(
                        name = stringResource(R.string.cjk_lang_korean_title),
                        description = stringResource(R.string.cjk_lang_korean_summary),
                        icon = R.drawable.ic_settings_languages,
                        onClick = {}
                    )
                }
            }

            // Card 3: Engine Utilities
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column {
                    PreferenceCategory(stringResource(R.string.cjk_engine_options_title))

                    if (BuildConfig.DEBUG) {
                        SwitchPreference(
                            name = stringResource(R.string.cjk_mock_engine_title),
                            description = stringResource(R.string.cjk_mock_engine_summary),
                            key = CjkPluginLoader.PREF_ENABLE_MOCK,
                            default = BuildConfig.DEBUG
                        )
                    }

                    Preference(
                        name = stringResource(R.string.cjk_clear_user_dict_title),
                        description = stringResource(R.string.cjk_clear_user_dict_summary),
                        onClick = {
                            CjkPluginLoader.clearUserDictionaries(context)
                            Toast.makeText(context, context.getString(R.string.cjk_clear_user_dict_success), Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
