package edu.uark.cm288.geo_camera.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Utility helper class responsible for retrieving the device's last known geographical location
 * utilizing Google Play Services [FusedLocationProviderClient].
 *
 * @property context Application or activity context.
 */
class LocationHelper(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Asynchronously fetches the device's last known location.
     * Suspends execution until the location task succeeds or fails.
     *
     * @return The last known [Location] object, or null if unavailable or permissions are missing.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }
}
