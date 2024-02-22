package com.example.fastrun

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlin.collections.Map

var cel_steps = arrayOf(4000, 5000, 6000, 7000, 8000, 9000, 10000)
var lang = arrayOf("Русский","English")
var cel_shag:Int = 7000
var selectedPosition:Int = 1000
var select_lang:Int = 0

lateinit var txt_cel:TextView
lateinit var img_sett:ImageView
lateinit var img_total:ImageView
lateinit var img_cel_steps:ImageView
lateinit var img_weight:ImageView
lateinit var img_language:ImageView
lateinit var img_connection:ImageView
lateinit var img_share:ImageView
lateinit var img_policy:ImageView
lateinit var settings_map:ImageButton
lateinit var settings_home:ImageButton
lateinit var settings_analitic:ImageButton
lateinit var settings_profile:ImageButton
lateinit var txt_weight:TextView


class Settings : AppCompatActivity() {

    var km:Float? = 0f
    var ckal:Float? = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_settings2)

        img_sett = findViewById(R.id.img_sett)
        img_total = findViewById(R.id.img_total)
        img_cel_steps = findViewById(R.id.img_cel_steps)
        img_weight = findViewById(R.id.img_weight)
        img_language = findViewById(R.id.language)
        img_connection = findViewById(R.id.connection)
        img_share = findViewById(R.id.share)
        img_policy = findViewById(R.id.policy)


        val prefs: SharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lal:SharedPreferences = getSharedPreferences("FastPrefs", Context.MODE_PRIVATE)
        val editor_steps = lal.edit()
        val editor = prefs.edit()
        val ves_select:Float = prefs.getFloat("ves",0f)
        var id:Int = prefs.getInt("id",0)
        getData(id)
        /*var km:Float = lal.getFloat("km", 0f)
        var ckal:Float = lal.getFloat("ckal", 0f)*/

        settings_map = findViewById(R.id.settings_maps)
        settings_home = findViewById(R.id.settings_home)
        settings_analitic = findViewById(R.id.settings_analitic)
        settings_profile = findViewById(R.id.settings_prof)
        var intent_sett_map: Intent = Intent(this@Settings, Maps::class.java)
        var intent_sett_home: Intent = Intent(this@Settings, Home::class.java)

        settings_map.setOnClickListener {
            startActivity(intent_sett_map)
//            overridePendingTransition(R.anim.to_right_in, R.anim.to_right_out)

        }
        settings_analitic.setOnClickListener {

            overridePendingTransition(R.anim.to_right_in, R.anim.to_right_out)
        }
        settings_home.setOnClickListener {
            startActivity(intent_sett_home)
            overridePendingTransition(R.anim.to_right_in, R.anim.to_right_out)
        }
        settings_profile.setOnClickListener {

            overridePendingTransition(R.anim.to_right_in, R.anim.to_right_out)
        }

        var police: ImageView = findViewById(R.id.policy)
        police.setOnClickListener {
            val url = "https://www.privacypolicygenerator.info/live.php?token=wtzzgnSsjMYNDOyl1XRKCqX2ZEFoJyDn"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }



        val spin: Spinner = findViewById(R.id.spin_shagi)
        val aa = ArrayAdapter(this@Settings,R.layout.spinner_fon_shagi,R.id.spin_txt_shagi, cel_steps)
        spin.adapter = aa

        spin.setSelection(prefs.getInt("spinnerSelection", 3))
        selectedPosition = prefs.getInt("spinnerSelection", 3)
        spin.onItemSelectedListener = CelSelectClass(editor)
        txt_cel = findViewById(R.id.txt_sett_shagi)
        if(selectedPosition == 0){
            txt_cel.text = "4000"
        }
        else if(selectedPosition == 1){
            txt_cel.text = "5000"
        }
        else if(selectedPosition == 2){
            txt_cel.text = "6000"
        }
        else if(selectedPosition == 3){
            txt_cel.text = "7000"
        }
        else if(selectedPosition == 4){
            txt_cel.text = "8000"
        }
        else if(selectedPosition == 5){
            txt_cel.text = "9000"
        }
        else if(selectedPosition == 6){
            txt_cel.text = "10000"
        }

        val spin2:Spinner = findViewById(R.id.spin_language)
        val bb = ArrayAdapter(this@Settings,R.layout.spinner_fon_lang,R.id.spin_txt_lang, lang)
        spin2.adapter = bb

        spin2.setSelection(prefs.getInt("select_lang", 0))
        val editor_lang = prefs.edit()
        spin2.onItemSelectedListener = LangSelectClass(editor_lang, prefs)

        var select_ves:Float
        var ves:ImageButton = findViewById(R.id.btn_weight)
        txt_weight = findViewById(R.id.txt_weight)
        if (select_lang == 0){
            if (ves_select == 0f){
                txt_weight.text = "0 кг"
            }else{
                txt_weight.text = "${ves_select.toFloat()} кг"
            }
        }else if (select_lang == 1){
            if (ves_select == 0f){
                txt_weight.text = "0 kg"
            }else{
                txt_weight.text = "${ves_select.toFloat()} kg"
            }
        }

        ves.setOnClickListener {
            val view = View.inflate(this@Settings, R.layout.weight_selection,null)
            val builder = AlertDialog.Builder(this@Settings)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            val btn_confirm: ImageButton = view.findViewById(R.id.btn_confirm)
            val edt_ves: EditText = view.findViewById(R.id.edt_ves)
            val text_weight1:ImageView = view.findViewById(R.id.img_text_weight1)
            val text_weight2:ImageView = view.findViewById(R.id.img_text_weight2)
            val btn_conf:ImageButton = view.findViewById(R.id.btn_confirm)
            if (select_lang == 0){
                edt_ves.hint = "Ваш вес"
                text_weight1.setBackgroundResource(R.drawable.text_weight1)
                text_weight2.setBackgroundResource(R.drawable.text_weight2)
                btn_conf.setBackgroundResource(R.drawable.btn_confirm_weight)
            }
            else if (select_lang == 1){
                edt_ves.hint = "Your weight"
                text_weight1.setBackgroundResource(R.drawable.text_weight1_e)
                text_weight2.setBackgroundResource(R.drawable.text_weight2_e)
                btn_conf.setBackgroundResource(R.drawable.btn_confirm_weight_e)
            }
            btn_confirm.setOnClickListener {
                if(edt_ves.text.toString() != "") {
                    select_ves = edt_ves.text.toString().toFloat()
                    if (select_ves == 0f){
                        if (select_lang == 0){
                            txt_weight.text = "0 кг"
                        }else{
                        txt_weight.text = "It is necessary to calculate calories"
                    }
                    }else{
                        if (select_lang == 0) {
                            txt_weight.text = "$select_ves кг"
                        } else if (select_lang == 1) {
                            txt_weight.text = "$select_ves kg"
                        }
                    }
                    editor.putFloat("ves", select_ves)
                    editor.apply()
                    dialog.dismiss()
                }else{
                    dialog.dismiss()
                }


            }
        }
    }

    private fun getData(id:Int){
        var c:Int = 0
        var txt_km:TextView = findViewById(R.id.none_km_sett)
        var txt_ckal:TextView = findViewById(R.id.none_kkal_sett)
        lifecycleScope.launch{
            val bd = supabase.from("FastRan").select().decodeList<FastRan>()
            while (c < bd.size){
                if (id == bd[c].id.toInt()){
                    km = bd[c].All_km?.toFloat()
                    ckal = bd[c].All_ckal?.toFloat()
                    txt_km.text = "${String.format("%.2f", km)}"
                    txt_ckal.text = "${String.format("%.2f", ckal)}"
                    break
                }
                c++
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

internal class CelSelectClass(editor: SharedPreferences.Editor) : AdapterView.OnItemSelectedListener {
    val editor = editor
    override fun onItemSelected(parent: AdapterView<*>?, v: View, position: Int, id: Long) {

        editor.putInt("spinnerSelection", position)
        editor.apply()
        selectedPosition = position
        if(selectedPosition == 0){
            txt_cel.text = "4000"
        }
        else if(selectedPosition == 1){
            txt_cel.text = "5000"
        }
        else if(selectedPosition == 2){
            txt_cel.text = "6000"
        }
        else if(selectedPosition == 3){
            txt_cel.text = "7000"
        }
        else if(selectedPosition == 4){
            txt_cel.text = "8000"
        }
        else if(selectedPosition == 5){
            txt_cel.text = "9000"
        }
        else if(selectedPosition == 6){
            txt_cel.text = "10000"
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}

internal class LangSelectClass(editor: SharedPreferences.Editor, prefs: SharedPreferences): AdapterView.OnItemSelectedListener {
    val editor = editor
    val prefs = prefs
    override fun onItemSelected(parent: AdapterView<*>?, v: View, position: Int, id: Long) {
        editor.putInt("select_lang", position)
        editor.apply()
        select_lang = position
        val ves_select:Float = prefs.getFloat("ves",0f)

    if (select_lang == 0){
            img_sett.setBackgroundResource(R.drawable.txt_settings)
            img_total.setBackgroundResource(R.drawable.total)
            img_cel_steps.setBackgroundResource(R.drawable.target)
            img_weight.setBackgroundResource(R.drawable.weight)
            img_language.setBackgroundResource(R.drawable.language)
            img_connection.setBackgroundResource(R.drawable.connection)
            img_share.setBackgroundResource(R.drawable.share)
            img_policy.setBackgroundResource(R.drawable.policy)
            settings_map.setBackgroundResource(R.drawable.map)
            settings_home.setBackgroundResource(R.drawable.home)
            settings_analitic.setBackgroundResource(R.drawable.resource_static)
            settings_profile.setBackgroundResource(R.drawable.prof)
            if (ves_select == 0f){
                txt_weight.text = "0 кг"
            }else{
                txt_weight.text = "${ves_select.toFloat()} кг"
            }
    }else if (select_lang == 1){
            img_sett.setBackgroundResource(R.drawable.txt_settings_e)
            img_total.setBackgroundResource(R.drawable.total_ee)
            img_cel_steps.setBackgroundResource(R.drawable.target_e)
            img_weight.setBackgroundResource(R.drawable.weight_e2)
            img_language.setBackgroundResource(R.drawable.l_e)
            img_connection.setBackgroundResource(R.drawable.connect_e)
            img_share.setBackgroundResource(R.drawable.share_e)
            img_policy.setBackgroundResource(R.drawable.policy_e)
            settings_map.setBackgroundResource(R.drawable.map_e)
            settings_home.setBackgroundResource(R.drawable.main_e)
            settings_analitic.setBackgroundResource(R.drawable.static_e)
            settings_profile.setBackgroundResource(R.drawable.prof_e)
            if (ves_select == 0f){
                txt_weight.text = "0 kg"
            }else{
                txt_weight.text = "${ves_select.toFloat()} kg"
            }
        }

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}