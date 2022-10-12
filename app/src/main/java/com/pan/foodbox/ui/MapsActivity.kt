package com.pan.foodbox.ui

import android.Manifest
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.pan.foodbox.GeofenceHelper
import com.pan.foodbox.R
import com.pan.foodbox.adapter.InfoWindowAdapter
import com.pan.foodbox.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var geofenceHelper: GeofenceHelper
    private var GEOFENCE_ID: String = "SOME_GEOFENCE_ID"
    private val GEOFENGS_RADIUS = 200
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var mMap: GoogleMap
    private val TAG = "mapActivity"


    val mingalarRd = LatLng(16.8247013, 96.1269312)
    val itVisionHub = LatLng(16.82521999076325, 96.1258090411045)

    private val FINE_LOCATION_ACCESS_REQUEST_CODE = 1001
    private val BACKGRAOUND_LOCATION_ACCESS_CODE = 1002
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        // val sydney = LatLng(16.825222, 96.125808)
        mMap.addMarker(MarkerOptions().position(mingalarRd).title("DNI"))
        mMap.addMarker(MarkerOptions().position(itVisionHub).title("ITVisionHub"))

        mMap.moveCamera(CameraUpdateFactory.newLatLng(mingalarRd))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(itVisionHub))

        enableUserLocation()
        mMap.setOnMapLongClickListener(this)

        addCircle(mingalarRd, GEOFENGS_RADIUS.toFloat())
        addCircle(itVisionHub, GEOFENGS_RADIUS.toFloat())
        addGeofence(mingalarRd, GEOFENGS_RADIUS.toFloat())
        addGeofence(itVisionHub, GEOFENGS_RADIUS.toFloat())
        // addPolyLine()
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
        }
        mMap.setInfoWindowAdapter(InfoWindowAdapter(this))

    }
/*
    private fun addPolyLine() {
        val polyline = mMap.addPolyline(PolylineOptions().apply {
            add(mingalarRd, itVisionHub)
            width(5f)
            color(Color.BLUE)
        })

    }*/



    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val array: Array<String> = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this, array, 100)
            } else {
                val array: Array<String> = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this, array, 100)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mMap.isMyLocationEnabled = true
            }
        }

        if (requestCode == BACKGRAOUND_LOCATION_ACCESS_CODE) {
            if (grantResults.isNotEmpty()) {
                // Toast.makeText(this, "You can add geofence..", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Background location access is necessary for geofence to trigger...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onMapLongClick(latLng: LatLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                handleMapLongClick(latLng)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                ) {
                    //we show a dialog and ask for permission
                    val array: Array<String> =
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

                    ActivityCompat.requestPermissions(this, array, BACKGRAOUND_LOCATION_ACCESS_CODE)
                } else {
                    val array: Array<String> =
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    ActivityCompat.requestPermissions(this, array, BACKGRAOUND_LOCATION_ACCESS_CODE)
                }
            }
        } else {
            handleMapLongClick(latLng)
        }
    }

    private fun handleMapLongClick(latLng: LatLng) {
        mMap.clear()
        addMarker(latLng)
        addCircle(latLng, GEOFENGS_RADIUS.toFloat())
        addGeofence(latLng, GEOFENGS_RADIUS.toFloat())
        addGeofence(latLng, GEOFENGS_RADIUS.toFloat())
    }

    private fun addGeofence(latLng: LatLng, radius: Float) {
        val geofence = geofenceHelper.getGeofence(
            GEOFENCE_ID,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofenceRequest: GeofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent = geofenceHelper.getPendingIntent()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        geofencingClient.addGeofences(geofenceRequest, pendingIntent)
            .addOnSuccessListener {
                //  Log.d(TAG, "success::Geofence Added...")
            }
            .addOnFailureListener { it ->
                // Log.d(TAG, "onFailure: ${it}")
            }
    }

    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        mMap.addMarker(markerOptions)

    }

    private fun addCircle(latLng: LatLng, radius: Float) {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4F)
        mMap.addCircle(circleOptions)
    }
}


