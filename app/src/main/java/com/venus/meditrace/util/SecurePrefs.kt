package com.venus.meditrace.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Thin wrapper around [EncryptedSharedPreferences].
 *
 * All sensitive tokens (JWT, user ID, etc.) are stored via this class so they
 * are AES-256-GCM encrypted on-device rather than written as plain text.
 *
 * Usage:
 *   SecurePrefs.putString(context, Constants.KEY_AUTH_TOKEN, token)
 *   val token = SecurePrefs.getString(context, Constants.KEY_AUTH_TOKEN)
 *   SecurePrefs.remove(context, Constants.KEY_AUTH_TOKEN)
 *   SecurePrefs.clearAll(context)   // call on logout
 */
object SecurePrefs {

    private const val FILE_NAME = "meditrace_secure_prefs"

    private fun prefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun putString(context: Context, key: String, value: String) =
        prefs(context).edit().putString(key, value).apply()

    fun getString(context: Context, key: String): String? =
        prefs(context).getString(key, null)

    fun putBoolean(context: Context, key: String, value: Boolean) =
        prefs(context).edit().putBoolean(key, value).apply()

    fun getBoolean(context: Context, key: String, default: Boolean = false): Boolean =
        prefs(context).getBoolean(key, default)

    fun remove(context: Context, key: String) =
        prefs(context).edit().remove(key).apply()

    /** Wipe all secure preferences — call on user logout. */
    fun clearAll(context: Context) =
        prefs(context).edit().clear().apply()
}