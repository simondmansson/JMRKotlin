package com.kalk.jmr

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log

/**
 * Created by kalk on 3/13/18.
 */

class GPS @SuppressLint("MissingPermission")
constructor(private val mContext: Context) : Service(), LocationListener {
    private var location: Location? = null
    private var mLocationManager: LocationManager? = null

    private var GPSEnabled = false
    private var networkEnabled = false

    init {
        try {
            mLocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            GPSEnabled = mLocationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
            networkEnabled = mLocationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false

            if (networkEnabled) {
                mLocationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                location = mLocationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            if (GPSEnabled) {
                mLocationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                location = mLocationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            this.location = location
        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}

    override fun onProviderEnabled(s: String) {}

    override fun onProviderDisabled(s: String) {}

    override fun onBind(intent: Intent): IBinder? = null

    companion object {
        private val TAG = GPS::class.java.simpleName
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters
        private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    }

}