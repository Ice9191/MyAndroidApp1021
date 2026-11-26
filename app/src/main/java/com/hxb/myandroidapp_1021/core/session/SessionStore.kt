package com.hxb.myandroidapp_1021.core.session

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private val CONFIGURED_BASE_URL = stringPreferencesKey("configured_base_url")
private val SESSION_BASE_URL = stringPreferencesKey("session_base_url")
private val TOKEN_CIPHERTEXT = stringPreferencesKey("token_ciphertext")
private val EXPIRES_AT = stringPreferencesKey("expires_at")

@Singleton
internal class SessionStore @Inject constructor(@ApplicationContext context: Context) {
    data class Snapshot(
        val configuredBaseUrl: String?,
        val session: StoredSession?
    )

    data class StoredSession(
        val baseUrl: String,
        val encryptedToken: String,
        val expiresAt: String
    )

    // Keystore 的密钥绑定当前设备，会话文件放在系统备份范围之外。
    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
    ) {
        File(context.noBackupFilesDir, "session.preferences_pb")
    }

    suspend fun read(): Snapshot = dataStore.data.first().toSnapshot()

    suspend fun update(transform: (Snapshot) -> Snapshot): Snapshot =
        dataStore.edit { preferences ->
            val updated = transform(preferences.toSnapshot())
            preferences.setOrRemove(CONFIGURED_BASE_URL, updated.configuredBaseUrl)
            preferences.setOrRemove(SESSION_BASE_URL, updated.session?.baseUrl)
            preferences.setOrRemove(TOKEN_CIPHERTEXT, updated.session?.encryptedToken)
            preferences.setOrRemove(EXPIRES_AT, updated.session?.expiresAt)
        }.toSnapshot()

    private fun Preferences.toSnapshot(): Snapshot {
        val baseUrl = this[SESSION_BASE_URL]
        val encryptedToken = this[TOKEN_CIPHERTEXT]
        val expiresAt = this[EXPIRES_AT]
        val session = if (baseUrl != null && encryptedToken != null && expiresAt != null) {
            StoredSession(baseUrl, encryptedToken, expiresAt)
        } else {
            null
        }
        return Snapshot(configuredBaseUrl = this[CONFIGURED_BASE_URL], session = session)
    }

    private fun MutablePreferences.setOrRemove(key: Preferences.Key<String>, value: String?) {
        if (value == null) {
            remove(key)
        } else {
            this[key] = value
        }
    }
}
