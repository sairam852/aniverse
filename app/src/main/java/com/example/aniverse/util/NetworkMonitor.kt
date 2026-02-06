package com.example.aniverse.util

import kotlinx.coroutines.flow.StateFlow

/**
 * Exposes whether the device currently has a validated network connection.
 *
 * Implementation will be added later using ConnectivityManager.NetworkCallback.
 */
interface NetworkMonitor {
    val isOnline: StateFlow<Boolean>
}

