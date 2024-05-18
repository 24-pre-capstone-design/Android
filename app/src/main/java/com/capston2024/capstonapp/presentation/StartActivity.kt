package com.capston2024.capstonapp.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.presentation.main.MainActivity

class StartActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start)

        var newActivity = findViewById<LinearLayout>(R.id.startScreen)
        newActivity.setOnClickListener() {
            var intent = Intent(applicationContext, MainActivity :: class.java)
            startActivity(intent)
        }
    }


}