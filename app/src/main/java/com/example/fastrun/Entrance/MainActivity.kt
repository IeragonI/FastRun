package com.example.fastrun.Entrance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.fastrun.R

var x:Int = 0
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager>(R.id.viewpager)
        val adapter = SimpleFragmentPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        var intent:Intent = Intent(this@MainActivity, Registration::class.java)
        val btn_next: ImageButton = findViewById(R.id.btn_next)
        val btn_skip:ImageButton = findViewById(R.id.btn_skip)

        btn_skip.setOnClickListener {
            startActivity(intent)
        }

        btn_next.setOnClickListener {
            if (x == 0) {
                viewPager.setCurrentItem(0)
            }else if(x == 1){
                viewPager.setCurrentItem(1)
            }else if (x == 2){
                viewPager.setCurrentItem(2)
                x++
            }else{
                startActivity(intent)
            }
        }
    }
}

class SimpleFragmentPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
    when (position){
        0 -> {
            x = 0}
        1 ->{
            x = 1}
        else ->{
            x = 2}
    }

    return when (position) {
            0 -> {
                Onboarding1()
            }
            1 -> {
                Onboarding2()
            }
            else -> {
                Onboarding3()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }
}