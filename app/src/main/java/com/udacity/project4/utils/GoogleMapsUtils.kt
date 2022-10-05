package com.udacity.project4.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import java.util.*

class GoogleMapsUtils(private val context:Context, private val map: GoogleMap) {
    var lastMarker:Marker? = null
    var PoiText:String? =null

    fun enableLocationSelection(isSelected:Boolean){
        if (isSelected){
            setMapLongClick(map)
            setPoiClick(map)
        }
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(),
                context.getString(R.string.lat_long_snippet),
                latLng.latitude,
                latLng.longitude
            )
            val currentMarker =map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(context.getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            )
            if (lastMarker != null){
                lastMarker?.remove()
            }
            lastMarker = currentMarker
        }

    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->

            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
            PoiText = poi.name
            if (lastMarker != null){
                lastMarker?.remove()
            }
            lastMarker = poiMarker
        }
    }

    fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(AuthenticationActivity.TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(AuthenticationActivity.TAG, "Can't find style. Error: ", e)
        }
    }

    @SuppressLint("MissingPermission")
    fun autoZoomToUserLocation(map: GoogleMap){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    // use your location object
                    // get latitude , longitude and other info from this
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude), 15f));
                }
            }
    }



}