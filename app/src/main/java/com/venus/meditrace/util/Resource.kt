package com.venus.meditrace.util

/**
 * A sealed class representing the state of a data operation.
 *
 * - Idle    : No operation has been started yet (initial UI state).
 * - Loading : An operation is in progress.
 * - Success : Operation completed; carries the result payload.
 * - Error   : Operation failed; carries a human-readable message and
 *             an optional HTTP status code for callers that need it.
 */
sealed class Resource<out T> {
    object Idle                                          : Resource<Nothing>()
    object Loading                                       : Resource<Nothing>()
    data class Success<T>(val data: T)                   : Resource<T>()
    data class Error(
        val message: String,
        val code: Int? = null          // HTTP status code when available
    ) : Resource<Nothing>()
}