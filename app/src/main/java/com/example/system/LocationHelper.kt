package com.example.system

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.domain.BangladeshDistricts
import com.example.domain.District
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

sealed class LocationDetectResult {
    data class Success(
        val district: District,
        val latitude: Double,
        val longitude: Double,
        val isWithinBangladesh: Boolean,
    ) : LocationDetectResult()

    data class Error(val messageBn: String) : LocationDetectResult()
}

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        return coarse || fine
    }

    @SuppressLint("MissingPermission")
    suspend fun detectBangladeshDistrict(context: Context): LocationDetectResult {
        if (!hasLocationPermission(context)) {
            return LocationDetectResult.Error("অবস্থান শনাক্ত করার অনুমতি দেওয়া হয়নি।")
        }

        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return LocationDetectResult.Error("লোকেশন সেবা পাওয়া যায়নি।")

        val bestLastKnown = getBestLastKnownLocation(manager)
        val freshLocation = withTimeoutOrNull(8_000L) {
            requestCurrentCoarseLocation(context, manager)
        } ?: bestLastKnown

        if (freshLocation == null) {
            return LocationDetectResult.Error(
                "অবস্থান পাওয়া যায়নি। অনুগ্রহ করে ডিভাইসের লোকেশন চালু করুন অথবা তালিকা থেকে জেলা নির্বাচন করুন।",
            )
        }

        val lat = freshLocation.latitude
        val lon = freshLocation.longitude
        val (nearestDistrict, distanceKm) = BangladeshDistricts.findNearest(lat, lon)
        val inBangladeshBounds = lat in 20.3..26.8 && lon in 88.0..92.8 && distanceKm < 250.0

        return LocationDetectResult.Success(
            district = nearestDistrict,
            latitude = if (inBangladeshBounds) lat else nearestDistrict.latitude,
            longitude = if (inBangladeshBounds) lon else nearestDistrict.longitude,
            isWithinBangladesh = inBangladeshBounds,
        )
    }

    @SuppressLint("MissingPermission")
    private fun getBestLastKnownLocation(manager: LocationManager): Location? {
        val providers = listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
            LocationManager.PASSIVE_PROVIDER,
        )
        var best: Location? = null
        for (provider in providers) {
            val loc = runCatching { manager.getLastKnownLocation(provider) }.getOrNull() ?: continue
            if (best == null || loc.time > best.time) {
                best = loc
            }
        }
        return best
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentCoarseLocation(
        context: Context,
        manager: LocationManager,
    ): Location? = suspendCancellableCoroutine { cont ->
        val provider = when {
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            else -> null
        }
        if (provider == null) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val cancellationSignal = CancellationSignal()
            cont.invokeOnCancellation { cancellationSignal.cancel() }
            runCatching {
                manager.getCurrentLocation(
                    provider,
                    cancellationSignal,
                    ContextCompat.getMainExecutor(context),
                ) { location ->
                    if (cont.isActive) cont.resume(location)
                }
            }.onFailure {
                if (cont.isActive) cont.resume(null)
            }
        } else {
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    runCatching { manager.removeUpdates(this) }
                    if (cont.isActive) cont.resume(location)
                }

                @Deprecated("Deprecated in API 29")
                override fun onStatusChanged(p: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(p: String) {}
                override fun onProviderDisabled(p: String) {}
            }
            cont.invokeOnCancellation { runCatching { manager.removeUpdates(listener) } }
            runCatching {
                @Suppress("DEPRECATION")
                manager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
            }.onFailure {
                if (cont.isActive) cont.resume(null)
            }
        }
    }
}
