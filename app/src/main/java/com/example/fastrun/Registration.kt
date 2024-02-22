package com.example.fastrun

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
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

class Registration : AppCompatActivity() {
    var dost:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_registration)

        val intent = Intent(this@Registration, Authorization::class.java)
        var edt_fio:EditText = findViewById(R.id.edt_fio)
        var edt_pass:EditText = findViewById(R.id.edt_pass)
        var edt_email:EditText = findViewById(R.id.edt_email)
        var btn_auth:ImageButton = findViewById(R.id.btn_auth)
        var btn_login:ImageButton = findViewById(R.id.btn_login_reg)


        /*val prefs: SharedPreferences = this.getSharedPreferences("pref_aureg", Context.MODE_PRIVATE)
        val editor:Editor = prefs.edit()*/

        btn_auth.setOnClickListener {
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            var name:String = edt_fio.text.toString()
            var login:String = edt_email.text.toString()
            var pass:String = edt_pass.text.toString()
            getData(login)
            if (dost != -1){
                if ((name != "") and (login != "") and (pass != "")){
                    insertData(name,login,pass)
                    startActivity(Intent(this@Registration,Authorization::class.java))
                }else{
                    Toast.makeText(this@Registration, "Нельзя оставлять пустыми\nполя ввода", Toast.LENGTH_SHORT).show()
                }
            }else if (dost == -1){
                Toast.makeText(this@Registration,"Такой логин уже занят", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun insertData(name:String, login:String, password:String){
        lifecycleScope.launch{
            var nlp = FastRan(Name = name, Login = login, Password = password)
            supabase.from("FastRan").insert(nlp)
        }
    }

    private fun getData(login: String){
        var a:Int = 0
        lifecycleScope.launch{
            val bd = supabase.from("FastRan").select().decodeList<FastRan>()
            while (a < bd.size){
                if (login == bd[a].Login){
                    dost = -1
                    break
                }else if (login != bd[a].Login){
                    dost = 1
                }
                a++
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


@kotlinx.serialization.Serializable
data class FastRan(
    val id:Int = 0,
    val Name:String = "",
    val Login:String = "",
    val Password:String = "",
    val All_km:Float? = 0f,
    val All_ckal:Float? = 0f,
    val All_time:Float? = 0f) {
    override fun toString():String {
        return "${id} ${Name} ${Login} ${Password} ${All_km} ${All_ckal} ${All_time}"
    }
}