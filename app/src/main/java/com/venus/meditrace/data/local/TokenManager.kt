// File: data/local/TokenManager.kt
package com.venus.meditrace.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "meditrace_auth")

class TokenManager(private val context: Context) {

    companion object {
        private val KEY_ACCESS_TOKEN  = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_USER_ID       = stringPreferencesKey("user_id")
        private val KEY_USER_NAME     = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL    = stringPreferencesKey("user_email")
        private val KEY_USER_ROLE     = stringPreferencesKey("user_role")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit {
            it[KEY_ACCESS_TOKEN]  = accessToken
            it[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveUser(id: String, name: String, email: String, role: String) {
        context.dataStore.edit {
            it[KEY_USER_ID]    = id
            it[KEY_USER_NAME]  = name
            it[KEY_USER_EMAIL] = email
            it[KEY_USER_ROLE]  = role
        }
    }

    suspend fun getAccessToken(): String? =
        context.dataStore.data.map { it[KEY_ACCESS_TOKEN] }.first()

    suspend fun getRefreshToken(): String? =
        context.dataStore.data.map { it[KEY_REFRESH_TOKEN] }.first()

    suspend fun getUserName(): String? =
        context.dataStore.data.map { it[KEY_USER_NAME] }.first()

    suspend fun getUserEmail(): String? =
        context.dataStore.data.map { it[KEY_USER_EMAIL] }.first()

    suspend fun getUserRole(): String? =
        context.dataStore.data.map { it[KEY_USER_ROLE] }.first()

    suspend fun isLoggedIn(): Boolean = getAccessToken() != null

    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }
}