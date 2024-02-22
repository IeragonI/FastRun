package com.example.fastrun

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.yandex.mapkit.MapKitFactory

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("1da58d25-4286-456e-88fa-cc9042a587d8")
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        var txt:ImageView = findViewById(R.id.txt_splash)
        val prefs: SharedPreferences = this@SplashScreen.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        select_lang = prefs.getInt("select_lang", 0)

        if (select_lang == 0){
            txt.setBackgroundResource(R.drawable.apprun_text)
        }else if (select_lang == 1){
            txt.setBackgroundResource(R.drawable.apprun_text_e)
        }


        Handler().postDelayed({
            val prefs: SharedPreferences = this.getSharedPreferences("pref_aureg", Context.MODE_PRIVATE)
            var zn = prefs.getInt("ke", 0)
            val intent: Intent = Intent(this@SplashScreen, MainActivity::class.java)
            val intent_main: Intent = Intent(this@SplashScreen, Home::class.java)
            if (zn == 0){
                startActivity(intent)
            }else if(zn == 1){
                startActivity(intent_main)
            }

        },2000)


    }
}