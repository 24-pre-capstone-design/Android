package com.cookandroid.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StartingActivity : AppCompatActivity() {

    lateinit var buttonGPT : Button
    lateinit var buttonBasic : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        setContentView(R.layout.activity_starting)

        buttonGPT = findViewById<Button>(R.id.buttonGPT)
        buttonGPT.setOnClickListener {
            var intent = Intent(applicationContext, OrderingActivityTemp::class.java)
            startActivity(intent)
        }

        buttonBasic = findViewById<Button>(R.id.buttonBasic)
        buttonBasic.setOnClickListener {
            var intent = Intent(applicationContext, OrderingActivityTemp::class.java)
            startActivity(intent)
        }
    }

}
