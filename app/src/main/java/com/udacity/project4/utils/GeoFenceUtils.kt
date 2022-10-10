package com.udacity.project4.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.udacity.project4.R
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment.Companion.ACTION_GEOFENCE_EVENT

@SuppressLint("MissingPermission")
class GeoFenceUtils(
    val context: Context,
    private val ID: String,
    private val latLng: LatLng,
    private val radius: Float,
    private val transitionTypes: Int
) {
    private lateinit var geofencingClient: GeofencingClient

    fun addGeoFence() {
        geofencingClient = LocationServices.getGeofencingClient(context)
        geofencingClient.addGeofences(requestGeoFence(), createPendingIntent())
            .addOnSuccessListener {
                Log.d("TAG", "Geofence Added")
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Please give background location permission",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("TAG", context.getString(R.string.geofence_not_available))
            }
    }

    private fun buildGeoFence(): Geofence {
        return Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setRequestId(ID)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    private fun requestGeoFence(): GeofencingRequest {
        return GeofencingRequest.Builder()
            .addGeofence(buildGeoFence())
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createPendingIntent(): PendingIntent {

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
       return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

               PendingIntent.getBroadcast(
                   context,
                   0,
                   intent,
                   PendingIntent.FLAG_MUTABLE
               )
       } else {
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

    }
}