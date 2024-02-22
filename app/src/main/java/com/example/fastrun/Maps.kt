package com.example.fastrun

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class Maps : AppCompatActivity() {
    lateinit var mapview: MapView
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_map)

        var menu_home: ImageButton = findViewById(R.id.menu_home_map)
        var menu_analitic: ImageButton = findViewById(R.id.menu_analitic_map)
        var menu_profile: ImageButton = findViewById(R.id.menu_prof_map)
        var menu_settings: ImageButton = findViewById(R.id.menu_settings_map)

        val prefs: SharedPreferences = this@Maps.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        select_lang = prefs.getInt("select_lang", 0)

        if (select_lang == 0){
            menu_home.setBackgroundResource(R.drawable.home)
            menu_analitic.setBackgroundResource(R.drawable.resource_static)
            menu_profile.setBackgroundResource(R.drawable.prof)
            menu_settings.setBackgroundResource(R.drawable.settings)
        }else if (select_lang == 1){
            menu_home.setBackgroundResource(R.drawable.main_e)
            menu_analitic.setBackgroundResource(R.drawable.static_e)
            menu_profile.setBackgroundResource(R.drawable.prof_e)
            menu_settings.setBackgroundResource(R.drawable.settings_e)
        }

        val intent_maps_home:Intent = Intent(this@Maps, Home::class.java)
        val intent_maps_sett:Intent = Intent(this@Maps, Settings::class.java)
        menu_home.setOnClickListener {
            startActivity(intent_maps_home)
            overridePendingTransition(R.anim.to_left_in, R.anim.to_left_out)
        }
        menu_settings.setOnClickListener {
            startActivity(intent_maps_sett)
            overridePendingTransition(R.anim.to_left_in, R.anim.to_left_out)
        }
        menu_analitic.setOnClickListener {

            overridePendingTransition(R.anim.to_left_in, R.anim.to_left_out)
        }
        menu_profile.setOnClickListener {

            overridePendingTransition(R.anim.to_left_in, R.anim.to_left_out)
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapview = findViewById(R.id.mapview)

        var mapKit: MapKit = MapKitFactory.getInstance()
        requestLocationPermission()
        getLastLocation()
        val prefs_c: SharedPreferences = this@Maps.getSharedPreferences("coordinati", Context.MODE_PRIVATE)
        var longit:Double = prefs_c.getFloat("longit", 37.617698F).toDouble()
        var latit:Double = prefs_c.getFloat("latit", 55.755864F).toDouble()

        var locationonmapkit = mapKit.createUserLocationLayer(mapview.mapWindow)
        mapview.map.move(
            CameraPosition(Point(latit, longit), 16.8f, 0.0f,0.0f),
            Animation(Animation.Type.SMOOTH,5f),null)
        locationonmapkit.isVisible = true
        locationonmapkit.isAutoZoomEnabled = true
        locationonmapkit.isHeadingEnabled = true

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val prefs: SharedPreferences = this@Maps.getSharedPreferences("coordinati", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        editor.putFloat("latit",location.latitude.toFloat())
                        editor.putFloat("longit",location.longitude.toFloat())
                        editor.apply()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val prefs: SharedPreferences = this@Maps.getSharedPreferences("coordinati", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            var mLastLocation: Location? = locationResult.lastLocation
            if (mLastLocation != null) {
                editor.putFloat("latit",mLastLocation.latitude.toFloat())
                editor.apply()
            }
            if (mLastLocation != null) {
                editor.putFloat("longit",mLastLocation.longitude.toFloat())
                editor.apply()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview.onStart()
    }

    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun requestLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            return
        }
    }
}