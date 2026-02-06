package com.example.aniverse.util

/**
 * Lightweight success/error wrapper for async data flows.
 */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : Resource<Nothing>()

    data object Loading : Resource<Nothing>()
}

