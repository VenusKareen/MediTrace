package com.venus.meditrace.util

object Constants {

    // ── Shared Preferences ────────────────────────────────────────────────
    const val PREFS_NAME      = "meditrace_prefs"
    const val KEY_AUTH_TOKEN  = "auth_token"
    const val KEY_USER_ROLE   = "user_role"
    const val KEY_USER_ID     = "user_id"
    const val KEY_USER_EMAIL  = "user_email"
    const val KEY_USER_NAME   = "user_name"
    /** Set to true after the user completes onboarding once. */
    const val KEY_ONBOARDING_DONE = "onboarding_done"

    // ── Verification statuses (must match backend enum) ───────────────────
    const val STATUS_VALID       = "VALID"
    const val STATUS_EXPIRED     = "EXPIRED"
    const val STATUS_COUNTERFEIT = "COUNTERFEIT"
    const val STATUS_NOT_FOUND   = "NOT_FOUND"

    // ── Network timeouts (seconds) ────────────────────────────────────────
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT    = 30L
    const val WRITE_TIMEOUT   = 30L

    // ── QR parsing ────────────────────────────────────────────────────────
    /** Query-parameter key that carries the HMAC signature in QR URLs. */
    const val QR_SIG_PARAM = "sig"
}