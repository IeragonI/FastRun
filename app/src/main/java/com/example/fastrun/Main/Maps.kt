package com.example.fastrun.Main

import android.Manifest
import android.annotation.SuppressLint

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fastrun.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.*
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError

class Maps : AppCompatActivity(), UserLocationObjectListener, CameraListener, Session.SearchListener, DrivingSession.DrivingRouteListener{
    lateinit var probkibut: Button
    lateinit var mapview:MapView
    lateinit var locationmapkit: UserLocationLayer
    lateinit var searchEdit: EditText
    lateinit var searchManager: SearchManager
    lateinit var searchSession: Session
    private var ROUTE_START_LOCATION = Point(54.98, 73.37) // НАЧАЛО МАРШРУТА
    private var ROUTE_END_LOCATION = Point(32.32, 32.32) // КОНЕЦ МАРШРУТА
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession:DrivingSession? = null
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private fun sumbitQuery(query:String){
        searchSession = searchManager.submit(query, VisibleRegionUtils.toPolygon(mapview.map.visibleRegion), SearchOptions(), this)
    }

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

        mapview = findViewById(R.id.mapview)
        var mapKit:MapKit = MapKitFactory.getInstance()
        requestLocationPermission()
        locationmapkit = mapKit.createUserLocationLayer(mapview.mapWindow)
        mapview.map.move(CameraPosition(ROUTE_START_LOCATION, 8f, 0f, 0f))

        locationmapkit.isVisible = true
        locationmapkit.setObjectListener(this)
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = mapview.map.mapObjects.addCollection()
        sumbitRequest()

        probkibut = findViewById(R.id.probkibut)

    }

    private fun requestLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            return
        }
    }
    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {

        userLocationView.arrow.setIcon(ImageProvider.fromResource(this, R.drawable.user_arrow))    //C
        val picIcon = userLocationView.pin.useCompositeIcon()
        picIcon.setIcon("icon", ImageProvider.fromResource(this, R.drawable.nothing), IconStyle().
        setAnchor(PointF(0f,0f))

        )
        picIcon.setIcon("pin", ImageProvider.fromResource(this, R.drawable.nothing),
            IconStyle().setAnchor(PointF(0.5f,0.5f)).setRotationType(RotationType.NO_ROTATION).setZIndex(1f).setScale(0.5f))
        userLocationView.accuracyCircle.fillColor = Color.BLUE
        var x = true
        probkibut.setOnClickListener {
            when (x) {
                true -> {
                    x = false
                    locationmapkit.setAnchor(
                        PointF(
                            (mapview.width() * 0.5).toFloat(),
                            (mapview.height() * 0.5).toFloat()
                        ),
                        PointF(
                            (mapview.width() * 0.5).toFloat(),
                            (mapview.height() * 0.83).toFloat()
                        )
                    )
                    locationmapkit.isAutoZoomEnabled = true
                    probkibut.setBackgroundResource(R.drawable.simpleblue)
                }

                false -> {
                    x = true
                    locationmapkit.resetAnchor()
                    locationmapkit.isAutoZoomEnabled = false
                    probkibut.setBackgroundResource(R.drawable.blueoff)
                }
            }
        }
    }

    override fun onObjectRemoved(p0: UserLocationView) {
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    override fun onSearchResponse(p0: Response) {
        TODO("Not yet implemented")
    }


    override fun onSearchError(error: Error) {
        var errorMessage="Неизвестная Ошибка"
        if(error is RemoteError){
            errorMessage ="Беспрводная ошибка"
        } else if(error is NetworkError){
            errorMessage = "Проблема с интернетом"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if(finished){
            sumbitQuery(searchEdit.text.toString())
        }
    }

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        for(route in p0){
            mapObjects!!.addPolyline(route.geometry)
        }
    }

    override fun onDrivingRoutesError(p0: Error) {
        var errorMessage = "Неизвестная ошибка!"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
    }
    private fun sumbitRequest() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()
        val requestPoints:ArrayList<RequestPoint> = ArrayList()
        requestPoints.add(RequestPoint(ROUTE_START_LOCATION, RequestPointType.WAYPOINT,null))
        requestPoints.add(RequestPoint(ROUTE_END_LOCATION, RequestPointType.WAYPOINT,null))
        drivingSession = drivingRouter!!.requestRoutes(requestPoints,drivingOptions,vehicleOptions,this)
    }
}