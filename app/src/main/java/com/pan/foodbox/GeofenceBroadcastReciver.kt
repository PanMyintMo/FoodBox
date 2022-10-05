package com.pan.foodbox

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.pan.foodbox.ui.MapsActivity

class GeofenceBroadcastReciver : BroadcastReceiver() {

    val masActivity:Class<MapsActivity> = MapsActivity::class.java
    private var TAG: String = "GeofenceBroadcastReceive"
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show()

        val notificationHelper=NotificationHelper(context)


        val geofencingEvent: GeofencingEvent = GeofencingEvent.fromIntent(intent)!!
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive :Error receiving geofence event...")
            return
        }

        val geofenceList: List<Geofence> = geofencingEvent.triggeringGeofences!!


        for (geofence in geofenceList){
            Log.d(TAG,"onReceive:" + geofence.requestId)
        }

        when (geofencingEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER","",masActivity)
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show()
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {

                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL","",masActivity)

                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT","",masActivity)
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show()
            }

        }

    }
}