package com.example.fastrun

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class Authorization : AppCompatActivity() {
    var tost:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_authorization)
        val edt_email:EditText = findViewById(R.id.edt_email_au)
        val edt_pass:EditText = findViewById(R.id.edt_pass_au)
        val btn_reg: ImageButton = findViewById(R.id.btn_reg)
        val btn_log_au:ImageButton = findViewById(R.id.btn_login_au)


        val intent_to_reg: Intent = Intent(this@Authorization, Registration::class.java)

        btn_reg.setOnClickListener {
            startActivity(intent_to_reg)
        }

        btn_log_au.setOnClickListener {
            tost = 0
            var login:String = edt_email.text.toString()
            var pass:String = edt_pass.text.toString()
            getData(login,pass)

        }
    }


    private fun getData(log:String,pass:String){
        val prefs: SharedPreferences = this.getSharedPreferences("pref_aureg", Context.MODE_PRIVATE)
        val preferenc: SharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val edit_pref = preferenc.edit()
        val editor = prefs.edit()
        var b:Int = 0
        var id:Int = 0
        lifecycleScope.launch{
            val bd = supabase.from("FastRan").select().decodeList<FastRan>()
            b = 0
            val intent = Intent(this@Authorization,Home::class.java)
            while (b < bd.size){
                if((log == bd[b].Login.toString()) and (pass == bd[b].Password.toString())){
                    id = bd[b].id.toInt()
                    editor.putInt("ke", 1).apply()
                    edit_pref.putInt("id",id).apply()
                    startActivity(intent)
                    break
                }else if((log == bd[b].Login.toString()) and (pass != bd[b].Password.toString())){
                    tost = 2
                }else{
                    tost = 1
                }
                b++
            }
            if (tost == 1){
                Toast.makeText(this@Authorization, "Такого аккаунта не существует", Toast.LENGTH_SHORT).show()
            }else if(tost == 2){
                Toast.makeText(this@Authorization, "Пароль не подходит!", Toast.LENGTH_SHORT).show()
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