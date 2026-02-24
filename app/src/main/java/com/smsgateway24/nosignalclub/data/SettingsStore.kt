package com.smsgateway24.nosignalclub.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// English comments as requested
private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {

    companion object {
        private val KEY_TARGET_NUMBER = stringPreferencesKey("target_number")
        private val KEY_ENABLED = booleanPreferencesKey("enabled")
    }

    val targetNumberFlow: Flow<String> =
        context.dataStore.data.map { it[KEY_TARGET_NUMBER] ?: "" }

    val enabledFlow: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_ENABLED] ?: false }

    suspend fun setTargetNumber(number: String) {
        context.dataStore.edit { prefs -> prefs[KEY_TARGET_NUMBER] = number }
    }

    suspend fun setEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_ENABLED] = enabled }
    }

    // Helper for service: synchronous read is not available, service will use flows or cached value
}