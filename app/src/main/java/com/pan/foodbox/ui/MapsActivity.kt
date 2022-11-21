package com.pan.foodbox.ui

import android.Manifest
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.datatransport.runtime.ExecutionModule_ExecutorFactory.create
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.PolyUtil
import com.pan.foodbox.*
import com.pan.foodbox.R
import com.pan.foodbox.apiInterface.DirectionApiInterface
import com.pan.foodbox.databinding.ActivityMapsBinding
//import com.pan.foodbox.map.GeofenceHelper
import com.pan.foodbox.modDirection.MapDatas
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

  //  private lateinit var geofenceHelper: GeofenceHelper
    private var GEOFENCE_ID: String = "SOME_GEOFENCE_ID"
    private val GEOFENGS_RADIUS = 200
    private lateinit var geofencingClient: GeofencingClient
    private var apiRequest: DirectionApiInterface? = null
    private lateinit var mMap: GoogleMap
    private var fusedLocation: FusedLocationProviderClient? = null
    private var originLocationLat: Double = 0.0
    private var originalLocationLong: Double = 0.0
    private var popupWindow: PopupWindow? = null
    private var destinationLocation: LatLng? = null
    private var mWith = 0
    private var mHeight = 0
    private var maker: Marker? = null
    private var origin: String = "0.0,0.0"
    private val itVisionHub = LatLng(16.784796, 96.181905)
    private lateinit var locationInterval: LocationRequest

    //googlemapi error code
    private var RES_ERROR_CODE = 201
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest

    private var apikey = ""
    private val FINE_LOCATION_ACCESS_REQUEST_CODE = 1001
  //  private val BACKGRAOUND_LOCATION_ACCESS_CODE = 1002
    private lateinit var binding: ActivityMapsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        apikey = BuildConfig.API_KEY

        if (checkGooglePlayServices()) {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        } else {
            Toast.makeText(
                this@MapsActivity,
                "Google Play Service Not Available",
                Toast.LENGTH_SHORT
            ).show()
        }

        geofencingClient = LocationServices.getGeofencingClient(this)
        //geofenceHelper = GeofenceHelper(this)


        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://maps.googleapis.com/")
            .build()
        apiRequest = retrofit.create(DirectionApiInterface::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        destinationLocation = LatLng(itVisionHub.latitude, itVisionHub.longitude)

        mMap.addMarker(MarkerOptions().position(destinationLocation!!).title("ITVisionHub"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destinationLocation!!))
        //mMap.setOnMapLongClickListener(this)

        // addCircle(origin, GEOFENGS_RADIUS.toFloat())
        addCircle(itVisionHub, GEOFENGS_RADIUS.toFloat())
        //   addGeofence(origin, GEOFENGS_RADIUS.toFloat())
        //addGeofence(itVisionHub, GEOFENGS_RADIUS.toFloat())


        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
        }
        checkGps()
        mMap.setOnMapClickListener {
            popupWindow?.dismiss()
            popupWindow = null
        }
        mMap.setOnMarkerClickListener {
            if (popupWindow != null) {
                popupWindow?.dismiss()
            }
            val view = layoutInflater.inflate(R.layout.infowindow, null)
            val newPopupWindow = PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val display = this.display
            val size = Point()
            val title = view.findViewById<TextView>(R.id.title)
            title.text = it.title.toString()
            /* val btn = view.findViewById<Button>(R.id.btnDirection)
             btn.setOnClickListener {
                 //Using CoroutineScope
                 CoroutineScope(Dispatchers.IO)
                     .launch {
                         val factResponse = apiRequest!!.getDirection().execute()
                         val fact = factResponse.body()
                         launch(Dispatchers.Main) {
                             if (factResponse.isSuccessful && fact != null) {
                                 drawPolyLine(factResponse)
                             *//* MaterialAlertDialogBuilder(this@MapsActivity)
                                    .setMessage(fact.status)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show()*//*
                            }
                        }
                    }
            }*/
            display?.getCurrentSizeRange(size, size)
            view.measure(size.x, size.y)
            mWith = view.measuredWidth
            mHeight = view.measuredHeight
            maker = it
            popupWindow = newPopupWindow
            updateWindow()
            false
        }
        mMap.setOnCameraMoveListener {
            updateWindow()
        }
    }

    private fun checkGps() {
        val locationManager: LocationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnable = false
        var networkEnable = false

        try {
            gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
        }
        try {
            networkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
        }
        if (!gpsEnable && !networkEnable) {
            AlertDialog.Builder(this)
                .setTitle("GPS is require for this app to work,Please enable GPS!")
                .setMessage("Do you want to enable location")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                    gpsEnable()
                    getCurrentLocation()
                    mMap.isMyLocationEnabled = true
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .show()
        } else {
            isLocationEnabled()
            getCurrentLocation()


        }
    }

    private fun gpsEnable() {
        val intent = Intent()
        intent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        startActivity(intent)

     //   activityResult.launch(intent, null)

    }

  /*  private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resData = result.data
            Toast.makeText(this@MapsActivity, "$resData", Toast.LENGTH_SHORT).show()

        }*/

    private fun getCurrentLocation() {
        if (enableUserLocation()) {
            if (isLocationEnabled()) {
                //final latitude and longitude
                fusedLocation?.lastLocation?.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        //Toast.makeText(this, "Turn on your Location", Toast.LENGTH_SHORT).show()
                    } else {
                        originLocationLat = location.latitude
                        originalLocationLong = location.longitude
                        origin = "$originLocationLat,$originalLocationLong"
                        requestPolyline()
                    }
                }
            }
        }
    }

    private fun requestPolyline() {
        apiRequest!!.getDirection(
            "$originLocationLat,$originalLocationLong",
            "${destinationLocation?.latitude},${destinationLocation?.longitude}",
            apikey
        ).enqueue(object : Callback<MapDatas> {
            override fun onResponse(
                call: Call<MapDatas>,
                response: Response<MapDatas>
            ) {
                if (response.body()?.routes?.size!! > 0) {
                    drawPolyLine(response)
                } else {
                    Toast.makeText(this@MapsActivity, "route size is zero", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<MapDatas>, t: Throwable) {
            }
        })
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun drawPolyLine(response: Response<MapDatas>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(10f)
            .color(Color.RED)
        mMap.addPolyline(polyline)
    }

    private fun updateWindow() {
        if (maker != null && popupWindow != null) {
            if (mMap.projection.visibleRegion.latLngBounds.contains(maker!!.position)) {
                if (!popupWindow?.isShowing!!) {
                    popupWindow!!.showAtLocation(View(this), Gravity.NO_GRAVITY, 0, 0)
                }
                val p = mMap.projection.toScreenLocation(maker!!.position)
                popupWindow!!.update(p.x - mWith / 2, p.y - mHeight + 100, -1, -1)
            } else {
                popupWindow?.dismiss()
            }
        }
    }

    private fun enableUserLocation(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            return true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    ACCESS_FINE_LOCATION
                )
            ) {
                val array: Array<String> = arrayOf(ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this, array, 100)
            } else {
                val array: Array<String> = arrayOf(ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this, array, 100)
            }
            return false
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
                        ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    getCurrentLocation()
                    return
                }
                mMap.isMyLocationEnabled = true
            }
        }

        /*if (requestCode == BACKGRAOUND_LOCATION_ACCESS_CODE) {
            if (grantResults.isNotEmpty()) {
                // Toast.makeText(this, "You can add geofence..", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Background location access is necessary for geofence to trigger...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }*/
    }

    /*override fun onMapLongClick(latLng: LatLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //  handleMapLongClick(latLng)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        ACCESS_BACKGROUND_LOCATION
                    )
                ) {
                    //we show a dialog and ask for permission
                    val array: Array<String> =
                        arrayOf(ACCESS_BACKGROUND_LOCATION)

                    ActivityCompat.requestPermissions(this, array, BACKGRAOUND_LOCATION_ACCESS_CODE)
                } else {
                    val array: Array<String> =
                        arrayOf(ACCESS_BACKGROUND_LOCATION)
                    ActivityCompat.requestPermissions(this, array, BACKGRAOUND_LOCATION_ACCESS_CODE)
                }
            }
        } else {
            // handleMapLongClick(latLng)
        }
    }*/


    private fun checkGooglePlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val res = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (res == ConnectionResult.SUCCESS) {
            return true
        } else if (googleApiAvailability.isUserResolvableError(res)) {
            val dialog = googleApiAvailability.getErrorDialog(
                this,
                res,
                RES_ERROR_CODE,
                DialogInterface.OnCancelListener {

                    Toast.makeText(this@MapsActivity, "User cancel dialog", Toast.LENGTH_SHORT)
                        .show()

                })
            dialog?.show()

        }
        return false
    }


/*    private fun addGeofence(latLng: LatLng, radius: Float) {
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
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        geofencingClient.addGeofences(geofenceRequest, pendingIntent)
            .addOnSuccessListener {
                //  Log.d(TAG, "success::Geofence Added...")
            }
            .addOnFailureListener { _ ->
                // Log.d(TAG, "onFailure: ${it}")
            }
    }*/

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


/*   private interface ApiService {
       @GET("maps/api/directions/json?")
       fun getDirection(
           @Query("origin") origin: String,
           @Query("destination") destination: String,
           @Query("key") apiKey: String
       ): Call<MapDatas>

   }*/
/*
    private object RetrofitClient {
        fun apiService(): ApiService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .build()
            return retrofit.create<ApiService>(ApiService::class.java)
        }
    }*/
}


