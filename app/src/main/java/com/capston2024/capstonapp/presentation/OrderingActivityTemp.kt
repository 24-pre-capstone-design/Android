package com.cookandroid.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class OrderingActivityTemp : AppCompatActivity() {
    lateinit var tempBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordering_temp)
        
        tempBtn = findViewById<Button>(R.id.tempBtn)
        tempBtn.setOnClickListener {
            var intent = Intent(applicationContext, PayingActivity::class.java)
            startActivity(intent)
        }
    }
}
