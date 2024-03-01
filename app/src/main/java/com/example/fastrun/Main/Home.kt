package com.example.fastrun.Main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.fastrun.Adverse.AlarmReceiver
import com.example.fastrun.Entrance.MainActivity
import com.example.fastrun.R
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


var currentSteps:Int = 0


class Home : AppCompatActivity(), SensorEventListener {

//    lateinit var mSensorManager: SensorManager
//    var mStepCounter:Float = 0f
    lateinit var txt_prog: TextView
    lateinit var txt_put: TextView
    lateinit var txt_ccal: TextView
//    var mStepDetector:Float = 0f
//    var isActivityRunning: Boolean? = null
//    var mStepDetectCounter:Int = 0
    var put_km:Float = 0f
    var ckal:Float = 0f
    var ves:Float = 0f
    var ckal_all:Float = 0f
    var put_km_all:Float = 0f
    lateinit var cel_steps:String
    lateinit var txt_km:TextView
    lateinit var txt_ckal:TextView

    val ACTIVITY = 102
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var totalSteps_all = 0f
    private var previousTotalSteps_all = 0f
    var currentSteps_all:Int = 0

    private lateinit var alarmIntent: PendingIntent
    private val calendar = Calendar.getInstance()

    private val channelId = "sample_channel"
    private val notificationId = 101

    @SuppressLint("ScheduleExactAlarm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_home)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmIntent = Intent(this@Home, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this@Home, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        calendar.set(Calendar.HOUR_OF_DAY,23)
        calendar.set(Calendar.MINUTE,59)
        alarmManager?.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
        
        var menu_maps:ImageButton = findViewById(R.id.menu_maps)
        var menu_analitic:ImageButton = findViewById(R.id.menu_analitic)
        var menu_profile:ImageButton = findViewById(R.id.menu_prof)
        var menu_settings:ImageButton = findViewById(R.id.menu_settings)
        var home_km:LinearLayout = findViewById(R.id.home_km)
        var home_ckal:LinearLayout = findViewById(R.id.home_ckal)
        var home_time:LinearLayout = findViewById(R.id.home_time)
        var home_date:ImageView = findViewById(R.id.img_date)
        var txt_steps:ImageView = findViewById(R.id.steps)
        var txt_date:TextView = findViewById(R.id.txt_date)

        val prefs: SharedPreferences = this@Home.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        select_lang = prefs.getInt("select_lang", 0)

        val dateTime_d = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd"))
        val dateTime_y = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"))
        val dateTime_m = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM"))
        var mounce:String = ""


        if (select_lang == 0){
            menu_maps.setBackgroundResource(R.drawable.map)
            menu_analitic.setBackgroundResource(R.drawable.resource_static)
            menu_profile.setBackgroundResource(R.drawable.prof)
            menu_settings.setBackgroundResource(R.drawable.settings)
            home_km.setBackgroundResource(R.drawable.km)
            home_ckal.setBackgroundResource(R.drawable.kal)
            home_time.setBackgroundResource(R.drawable.min)
            home_date.setBackgroundResource(R.drawable.date)
            txt_steps.setBackgroundResource(R.drawable.steps)
            if (dateTime_m == "01"){
                mounce = "января"
            }
            else if (dateTime_m == "02"){
                mounce = "февраля"
            }
            else if (dateTime_m == "03"){
                mounce = "марта"
            }
            else if (dateTime_m == "04"){
                mounce = "апреля"
            }
            else if (dateTime_m == "05"){
                mounce = "мая"
            }
            else if (dateTime_m == "06"){
                mounce = "июня"
            }
            else if (dateTime_m == "07"){
                mounce = "июля"
            }
            else if (dateTime_m == "08"){
                mounce = "августа"
            }
            else if (dateTime_m == "09"){
                mounce = "сентября"
            }
            else if (dateTime_m == "10"){
                mounce = "октября"
            }
            else if (dateTime_m == "11"){
                mounce = "ноября"
            }
            else if (dateTime_m == "12"){
                mounce = "декабря"
            }
        }
        else if (select_lang == 1){
            menu_maps.setBackgroundResource(R.drawable.map_e)
            menu_analitic.setBackgroundResource(R.drawable.static_e)
            menu_profile.setBackgroundResource(R.drawable.prof_e)
            menu_settings.setBackgroundResource(R.drawable.settings_e)
            home_km.setBackgroundResource(R.drawable.km_english)
            home_ckal.setBackgroundResource(R.drawable.kal_english)
            home_time.setBackgroundResource(R.drawable.min_english)
            home_date.setBackgroundResource(R.drawable.date_e)
            txt_steps.setBackgroundResource(R.drawable.steps_english)
            if (dateTime_m == "01"){
                mounce = "January"
            }
            else if (dateTime_m == "02"){
                mounce = "February"
            }
            else if (dateTime_m == "03"){
                mounce = "March"
            }
            else if (dateTime_m == "04"){
                mounce = "April"
            }
            else if (dateTime_m == "05"){
                mounce = "May"
            }
            else if (dateTime_m == "06"){
                mounce = "June"
            }
            else if (dateTime_m == "07"){
                mounce = "July"
            }
            else if (dateTime_m == "08"){
                mounce = "August"
            }
            else if (dateTime_m == "09"){
                mounce = "September"
            }
            else if (dateTime_m == "10"){
                mounce = "October"
            }
            else if (dateTime_m == "11"){
                mounce = "November"
            }
            else if (dateTime_m == "12"){
                mounce = "December"
            }
        }
        txt_date.text = "$dateTime_d $mounce $dateTime_y"


        loadData()
        /*resetSteps()*/
        checkForPermissions(android.Manifest.permission.ACTIVITY_RECOGNITION, "activity", ACTIVITY)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        txt_km = findViewById(R.id.none_km)
        txt_ckal = findViewById(R.id.none_kkal)

        var intent_home_maps:Intent = Intent(this@Home, Maps::class.java)
        var intent_home_sett:Intent = Intent(this@Home, Settings::class.java)

        menu_maps.setOnClickListener {
            startActivity(intent_home_maps)
            overridePendingTransition(R.anim.to_right_in, R.anim.to_right_out)
        }
        menu_analitic.setOnClickListener {

            overridePendingTransition(R.anim.to_left_in, R.anim.to_left_out)
        }
        menu_settings.setOnClickListener {
            startActivity(intent_home_sett)
            overridePendingTransition(R.anim.to_left_in, R.anim.to_left_out)
        }
        menu_profile.setOnClickListener {

            overridePendingTransition(R.anim.to_right_in, R.anim.to_right_out)
        }

        val txt_cel_shagi:TextView = findViewById(R.id.txt_cel_shagi)
        selectedPosition = prefs.getInt("spinnerSelection", 3)

        if(selectedPosition == 0){
            txt_cel_shagi.text = "4000"
            cel_steps = "4000"
        }
        else if(selectedPosition == 1){
            txt_cel_shagi.text = "5000"
            cel_steps = "5000"
        }
        else if(selectedPosition == 2){
            txt_cel_shagi.text = "6000"
            cel_steps = "6000"
        }
        else if(selectedPosition == 3){
            txt_cel_shagi.text = "7000"
            cel_steps = "7000"
        }
        else if(selectedPosition == 4){
            txt_cel_shagi.text = "8000"
            cel_steps = "8000"
        }
        else if(selectedPosition == 5){
            txt_cel_shagi.text = "9000"
            cel_steps = "9000"
        }
        else if(selectedPosition == 6){
            txt_cel_shagi.text = "10000"
            cel_steps = "10000"
        }


        ves = prefs.getFloat("ves",0f)

        txt_prog = findViewById(R.id.txt_shagi)
        txt_put = findViewById(R.id.none_km)
        txt_ccal = findViewById(R.id.none_kkal)

//        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) && packageManager.hasSystemFeature(
//                PackageManager.FEATURE_SENSOR_STEP_DETECTOR)) {
//            mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        } else {
//            Toast.makeText(this, "Ваш телефон не поддерживает наше приложение ╥﹏╥ ", Toast.LENGTH_LONG).show()
//        }
//        onResume()

    }

//    override fun onResume() {
//        super.onResume()
//
//
//        isActivityRunning = true
//        val countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
//        val detectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
//
//        val prefs: SharedPreferences = this@Home.getSharedPreferences("settings", Context.MODE_PRIVATE)
//        val editor = prefs.edit()
//
//        val sListener = object : SensorEventListener {
//            override fun onSensorChanged(p0: SensorEvent?) {
//                when (p0?.sensor?.type) {
//                    Sensor.TYPE_STEP_COUNTER -> if (isActivityRunning!!) {
//                        mStepCounter = p0.values[0]
//                        txt_prog.text = "${mStepCounter.toInt()}/"
//
//                        put_km = ((mStepCounter*0.7)/1000).toFloat()
//                        ckal = (0.0006*0.9*mStepCounter*ves).toFloat()
//                        txt_km.text = "${String.format("%.2f", put_km)}"
//                        txt_ckal.text = "${String.format("%.2f", ckal)}"
//                        editor.putFloat("km",put_km)
//                        editor.putFloat("ckal",ckal)
//                        editor.apply()
//
//                        /*if(select_lang == 0){
//                            txt_put.text = "${String.format("%.2f", put_km)}\nКм"
//                            txt_ccal.text = "${String.format("%.2f", ckal)}\nКкал"
//                        }else{
//                            txt_put.text = "${String.format("%.2f", put_km)}\nKm"
//                            txt_ccal.text = "${String.format("%.2f", ckal)}\ncKal"
//                        }
//
//                        steps = mStepCounter.toInt()*/
//
//
//                    }
//
//                    Sensor.TYPE_STEP_DETECTOR -> if (isActivityRunning!!) {
//                        mStepDetectCounter++
//                        mStepDetector = (mStepDetectCounter/2).toFloat()
//                        /*txt_prog.text = "${mStepDetector.toInt()}/"
//                        circularProgressBar.progress = mStepDetector*/
//                    }
//
//                }
//            }
//            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//
//            }
//
//        }
//
//        if (countSensor != null) {
//            mSensorManager.registerListener(sListener, countSensor, SensorManager.SENSOR_DELAY_UI)
//        } else {
//            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show()
//        }
//        if (detectSensor != null) {
//            mSensorManager.registerListener(sListener, detectSensor, SensorManager.SENSOR_DELAY_UI)
//        } else {
//            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show()
//        }
//    }

    private fun checkForPermissions(permission: String, name:String, requestCode:Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            when {
                ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
//                    Toast.makeText(applicationContext,"$name permission on", Toast.LENGTH_SHORT).show()
                }
                shouldShowRequestPermissionRationale(permission) -> showDialog(permission,name,requestCode)
                else -> ActivityCompat.requestPermissions(this@Home, arrayOf(permission), requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String){
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(applicationContext,"$name permission off(", Toast.LENGTH_SHORT).show()
            }
            else{
//                Toast.makeText(applicationContext,"$name permission on)", Toast.LENGTH_SHORT).show()
            }
        }

        when(requestCode) {
            ACTIVITY -> {innerCheck("activity")}
        }

    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this@Home)
        builder.apply {
            setMessage("Если вы не дадите разрешение\nто это приложение не будет работать\n╥﹏╥")
            setTitle("Это нам нужно!")
            setPositiveButton("OK"){ dialog, which ->
                ActivityCompat.requestPermissions(this@Home, arrayOf(permission), requestCode)
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        running = true

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        if (stepSensor == null) {
//            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }


//    override fun onPause() {
//        super.onPause()
//        isActivityRunning = false
//    }


    override fun onSensorChanged(event: SensorEvent?) {


        var tv_stepsTaken = findViewById<TextView>(R.id.txt_shagi)

        val sharedPreferences = getSharedPreferences("FastPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getInt("key2", 0)
        val savedNumber_all = sharedPreferences.getInt("key2_1", 0)
        val editor = sharedPreferences.edit()
        val prefs: SharedPreferences = this@Home.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor2 = prefs.edit()
        val id:Int = prefs.getInt("id", 1)
        if (running) {

            totalSteps = event!!.values[0]
            totalSteps_all = event!!.values[0]


            currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            currentSteps_all = totalSteps_all.toInt() - previousTotalSteps_all.toInt()

            ckal = (0.0006*0.9* currentSteps *ves).toFloat()
            put_km = ((currentSteps *0.7)/1000).toFloat()
            ckal_all = (0.0006*0.9*currentSteps_all*ves).toFloat()
            put_km_all = ((currentSteps_all*0.7)/1000).toFloat()

            if (currentSteps.toString() == cel_steps){
                createNotificationChannel()
                sendNotification()
            }

            tv_stepsTaken.text = "$currentSteps/"
            txt_km.text = "${String.format("%.2f", put_km)}"
            txt_ckal.text = "${String.format("%.2f", ckal)}"
            editor.putInt("shagi_1", currentSteps)
            /*editor.putFloat("km",put_km_all)
            editor.putFloat("ckal",ckal_all)*/
            editor.apply()
            updateData(id, put_km_all, ckal_all)
            if (savedNumber.toInt() == 0){
                tv_stepsTaken.text = 0.toString()
                Zapusk()
                editor.putFloat("km",0f )
                editor.putFloat("ckal",0f)
                editor.putInt("key2", 1)
                editor.apply()
            }

            if (savedNumber_all.toInt() == 0){
                Zapusk2()
                editor.putInt("key2_1", 1)
                editor.apply()
            }
        }
    }


    /*fun resetSteps() {
        var tv_stepsTaken = findViewById<TextView>(R.id.txt_shagi)
        tv_stepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        tv_stepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            tv_stepsTaken.text = "0/"
            txt_km.text = "0"
            txt_ckal.text = "0"

            // This will save the data
            saveData()

            true
        }
    }*/

    fun Zapusk(){
        var tv_stepsTaken = findViewById<TextView>(R.id.txt_shagi)
        previousTotalSteps = totalSteps
        tv_stepsTaken.text = "0/"
        txt_km.text = "0"
        txt_ckal.text = "0"
        saveData()
    }

    fun Zapusk2(){
        previousTotalSteps_all = totalSteps_all
        saveData2()
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("FastPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun saveData2() {
        val sharedPreferences = getSharedPreferences("FastPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1_1", previousTotalSteps_all)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("FastPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        val savedNumber2 = sharedPreferences.getFloat("key1_1", 0f)
//        Log.d("MainActivity", "$savedNumber")
//        Log.d("MainActivity", "$savedNumber2")
        previousTotalSteps = savedNumber
        previousTotalSteps_all = savedNumber2
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Поздравляем!"
            val descriptionText = "Вы достигли цели по кол-ву шагов!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun sendNotification() {
        val notificationIntent = Intent(
            this@Home,
            MainActivity::class.java
        )
        val pendingIntent = PendingIntent.getActivity(
            this@Home,
            0, notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icon_reg)
            .setContentTitle("Поздравляем!")
            .setContentIntent(pendingIntent)
            .setContentText("Вы достигли цели по кол-ву шагов!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@Home,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }
    }

    private fun updateData(id:Int, km:Float, ckal:Float) {
        lifecycleScope.launch {
            supabase.from("FastRan").update(
                {
                    set("All_km", km)
                    set("All_ckal",ckal)
                }
            ) {
                filter {
                    eq("id", id)
                }
            }
        }
    }

    val supabase = createSupabaseClient(
        supabaseUrl = "https://lxvlzpeedcjccaugjrhx.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imx4dmx6cGVlZGNqY2NhdWdqcmh4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTg2Njg3MDcsImV4cCI6MjAxNDI0NDcwN30.XUVf8JimDWRWXul6W-s7iSPiJ52_KH50G-B6mHeURlE"
    ) {
        install(Postgrest)
    }
}