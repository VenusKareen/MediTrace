package com.venus.meditrace.util

object Constants {
    const val PREFS_NAME          = "meditrace_prefs"
    const val KEY_AUTH_TOKEN      = "auth_token"
    const val KEY_USER_ROLE       = "user_role"
    const val KEY_USER_ID         = "user_id"
    const val KEY_USER_EMAIL      = "user_email"
    const val KEY_USER_NAME       = "user_name"

    const val STATUS_VALID        = "VALID"
    const val STATUS_EXPIRED      = "EXPIRED"
    const val STATUS_COUNTERFEIT  = "COUNTERFEIT"
    const val STATUS_NOT_FOUND    = "NOT_FOUND"

    const val CONNECT_TIMEOUT     = 30L
    const val READ_TIMEOUT        = 30L
    const val WRITE_TIMEOUT       = 30L
}