package com.pan.foodbox.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.pan.foodbox.ui.MapsActivity

class GeofenceBroadcastReciver : BroadcastReceiver() {

    val mapActivity:Class<MapsActivity> = MapsActivity::class.java
    private var TAG: String = "GeofenceBroadcastReceive"
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show()

        val notificationHelper= NotificationHelper(context)


        val geofencingEvent: GeofencingEvent = GeofencingEvent.fromIntent(intent)!!
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive :Error receiving geofence event...")
            return
        }

        val geofenceList: List<Geofence> = geofencingEvent.triggeringGeofences!!


        for (geofence in geofenceList){
            Log.d(TAG,"onReceive:" + geofence.requestId)
        }

        val transitionTypes=geofencingEvent.geofenceTransition
        val position :com.google.android.gms.maps.model.LatLng=com.google.android.gms.maps.model.LatLng(geofenceList.last().latitude,geofenceList.last().longitude)

        val hledan= com.google.android.gms.maps.model.LatLng(16.834680, 96.124014)
        val itVisionHub=
            com.google.android.gms.maps.model.LatLng(16.82521999076325, 96.1258090411045)

        var locationName=""
        var discount=0
        when(position){
            hledan -> {"Hledan Kaung Shae"
            discount=0}

            itVisionHub -> {"itVisionHub"
                discount=10
            }
            else ->{
                locationName="Click location"
            }
        }

        when (geofencingEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER","",mapActivity)
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show()
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {

                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL","",mapActivity)

                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT","",mapActivity)
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show()
            }

        }

    }
}